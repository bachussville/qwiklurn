/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora;

import com.bville.qwiklurn.repository.flora.type.interfaces.IFloraSubType;
import com.bville.qwiklurn.repository.flora.type.interfaces.IFlora;
import com.bville.qwiklurn.repository.flora.type.AbstractFlora;
import com.github.mongobee.Mongobee;
import com.github.mongobee.exception.MongobeeException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Updates.*;

import java.io.*;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.mongodb.client.result.DeleteResult;
import org.bson.BsonDocument;
import org.bson.BsonObjectId;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.imageio.stream.ImageInputStreamImpl;

/**
 *
 * @author Bart
 */
public class DbManager {

    public String dummyFileId = "5df2dfddc653863f9211ad10";

    public static final String MEDIA_FILEUPLOAD = "fileUpload";
    public static final String MEDIA_GRIDFS = "gridFs";
    public static final String MEDIA_GRIDFS_ID = "gridFsId";

    private String dbName = "Qwiklurn";
    public static final String COLL_FLORA = "Flora";
    public static final String COLL_SPECIES = "Species";
    public static final String COLL_PROJECTS = "Projects";

    public DbManager(String databaseName) {
        if (databaseName != null) {
            dbName = databaseName;
        }
    }

    public String getDbName() {
        return dbName;
    }

    public void setUpDatabase() {
        Mongobee runner = new Mongobee("mongodb://localhost:27017/" + getDbName());
        runner.setDbName(getDbName());         // host must be set if not set in URI
        runner.setChangeLogsScanPackage(
                "com.bville.qwiklurn.database.changelogs"); // package to scan for changesets

        try {
            runner.execute();
        } catch (MongobeeException e) {
            throw new RuntimeException("MongoBee failed to run", e);
        }
    }

    public MongoDatabase connect() {
        String url = "mongodb://localhost:27017/" + getDbName();
        MongoClient mongo = MongoClients.create(url);
        return mongo.getDatabase(getDbName());

    }

    public void saveFlora(IFloraSubType element) {
        final AbstractFlora FloraElementWithMedia = (AbstractFlora) uploadNewFiles(element);

        if (FloraElementWithMedia.getId() != null) {
            Species currentSpecies = getFloraById(FloraElementWithMedia.getId()).getSpecies();
            updateSpeciesForFlora(FloraElementWithMedia, currentSpecies, FloraElementWithMedia.getSpecies());

            getFloraCollection().updateOne(eq("_id", new ObjectId(FloraElementWithMedia.getId().toHexString())),
                    combine(
                            set("subType", FloraElementWithMedia.getSubType().getCode()),
                            set("species", FloraElementWithMedia.getSpecies().getId()),
                            set("latinName", FloraElementWithMedia.getLatinName()),
                            set("commonName", FloraElementWithMedia.getCommonName()),
                            set("functions", FloraElementWithMedia.getFunctionTypes().stream().map((t) -> {
                                return t.getCode();
                            }).collect(Collectors.toList())),
                            set("soilTypes", FloraElementWithMedia.getSoilTypes().stream().map((t) -> {
                                return t.getCode();
                            }).collect(Collectors.toList())),
                            set("solarTypes", FloraElementWithMedia.getSolarTypes().stream().map((t) -> {
                                return t.getCode();
                            }).collect(Collectors.toList())),
                            set("specials", FloraElementWithMedia.getSpecialProperties().keySet().stream().map((t) -> {
                                BsonDocument elem = new BsonDocument();
                                elem.append("code", new BsonString(t.getCode()));
                                elem.append("value", new BsonString(FloraElementWithMedia.getSpecialProperties().get(t)));
                                return elem;
                            }).collect(Collectors.toList())),
                            set("blossomMonths", FloraElementWithMedia.getBlossomMonths().stream().map((t) -> {
                                return t;
                            }).collect(Collectors.toList())),
                            set("harvestMonths", FloraElementWithMedia.getHarvestMonths().stream().map((t) -> {
                                return t;
                            }).collect(Collectors.toList())),
                            set("mediaReferences", FloraElementWithMedia.getMediaReferences()),
                            set("maxHeight", FloraElementWithMedia.getMaxHeight()),
                            set("maxWidth", FloraElementWithMedia.getMaxWidth()),
                            set("winterLeaf", FloraElementWithMedia.getWinterLeaves()),
                            set("maintenance", FloraElementWithMedia.getMaintenance()),
                            set("modificationDate", new Date()),
                            set("interrogationDate", FloraElementWithMedia.getInterrogationDate())
                    ));

            updateSubTypeSpecificAttributes(FloraElementWithMedia);

        } else {
            try {

                if (getSpeciesById(FloraElementWithMedia.getSpecies().getId()) == null) {
                    //insert the new species and retrieve it's objectId
                    ObjectId speciesId = updateSpeciesForFlora(FloraElementWithMedia, null, FloraElementWithMedia.getSpecies());
                    FloraElementWithMedia.setSpecies(getSpeciesById(speciesId));
                }

                Document newDoc = FloraElementWithMedia.toBson();
                getFloraCollection().insertOne(newDoc);
                FloraElementWithMedia.setId(newDoc.getObjectId("_id"));
                updateSpeciesForFlora(FloraElementWithMedia, null, getSpeciesById(FloraElementWithMedia.getSpecies().getId()));
                updateSubTypeSpecificAttributes(FloraElementWithMedia);

            } catch (MongoWriteException e) {
                throw e;
                /*
                if (e.getError().getCode() == 11000) {
                    JOptionPane.showMessageDialog(null, "Dit element bestaat reeds. Gelieve aan te passen ipv toe te voegen");

                } else {
                    JOptionPane.showMessageDialog(null, "Dit element kon niet opgeslagen worden. Meer info in de output logging");
                    e.printStackTrace();
                }
                 */
            }
        }

    }

    private void updateSubTypeSpecificAttributes(IFlora element) {
        HashMap<String, Object> updAttrList = ((IFloraSubType) element).getUpdateAttributesList();
        updAttrList.forEach((s, v) -> {
            getFloraCollection().updateOne(eq("_id", new ObjectId(element.getId().toHexString())),
                    combine(
                            set("" + s, v)
                    ));
        }
        );

    }

    private ObjectId updateSpeciesForFlora(AbstractFlora floraElement, Species currentSpecies, Species newSpecies) {
        if (currentSpecies != null) {
            currentSpecies.getMembers().removeIf(m -> {
                return m.compareTo(floraElement.getId()) == 0;
            });

            saveSpecies(currentSpecies);
        }

        if (floraElement.getId() != null && newSpecies.getMembers().stream()
                .filter(o -> {
                    return ((ObjectId) o).compareTo(floraElement.getId()) == 0;
                })
                .findFirst().isEmpty()) {
            newSpecies.getMembers().add(floraElement.getId());

        }
        saveSpecies(newSpecies);

        return newSpecies.getId();
    }

    public void saveSpecies(Species species) {
        Species current = getSpeciesById(species.getId());
        if (current != null) {
            getSpeciesCollection().updateOne(eq("_id", species.getId()),
                    combine(
                            set("name", species.getName()),
                            set("members", species.getMembers())
                    ));
        } else {
            Document specDoc = species.toBson();
            getSpeciesCollection().insertOne(specDoc);
            species.setId(specDoc.getObjectId("_id"));
        }

    }

    public void saveProject(Project project) {
        Project current = getProjectById(project.getId());
        if (current != null) {
            getProjectCollection().updateOne(eq("_id", project.getId()),
                    combine(
                            set("name", project.getName()),
                            set("members", project.getMembers().stream().map(ProjectElement::toBson).collect(Collectors.toList()))
                    ));
        } else {
            project.setCreationStamp(Instant.now().getEpochSecond());
            Document specDoc = project.toBson();
            getProjectCollection().insertOne(specDoc);
            project.setId(specDoc.getObjectId("_id"));
        }

    }

    public void updateFloraValidationDate(IFloraSubType FloraElement) throws FileNotFoundException {
        if (FloraElement.getId() != null) {
            getFloraCollection().updateOne(eq("_id", new ObjectId(FloraElement.getId().toHexString())),
                    combine(
                            set("interrogationDate", new Date())
                    ));
        }

    }

    public MongoCollection getFloraCollection() {
        return connect().getCollection(COLL_FLORA);
    }

    public MongoCollection getSpeciesCollection() {
        return connect().getCollection(COLL_SPECIES);
    }

    public MongoCollection getProjectCollection() {
        return connect().getCollection(COLL_PROJECTS);
    }

    public IFloraSubType getFloraById(ObjectId id) {
        FindIterable a = getFloraCollection().find(eq("_id", new ObjectId(id.toHexString())));

        return documentToIFloraSubType((Document) a.first());

    }

    public boolean deleteFloraById(ObjectId id) {
        DeleteResult dr = getFloraCollection().deleteOne(eq("_id", new ObjectId(id.toHexString())));
        return dr.getDeletedCount()>0;
    }

    public Species getSpeciesById(ObjectId speciesId) {
        if (speciesId == null) {
            return null;
        }
        FindIterable a = getSpeciesCollection().find(eq("_id", new ObjectId(speciesId.toHexString())));
        if (a.first() != null) {
            Document specDoc = (Document) a.first();
            return Species.fromBson(specDoc);
        } else {
            return null;
        }

    }

    public List<Species> listSpecies() {
        Document sortCrit = new Document();
        sortCrit.append("name", 1);

        MongoCursor a = getSpeciesCollection().find().sort(sortCrit).iterator();

        List<Species> result = new ArrayList();
        a.forEachRemaining(d -> {
            result.add(Species.fromBson((Document) d));
        });

        return result;

    }

    public Project getProjectById(ObjectId projectId) {
        if (projectId == null) {
            return null;
        }
        FindIterable a = getProjectCollection().find(eq("_id", new ObjectId(projectId.toHexString())));
        if (a.first() != null) {
            Document specDoc = (Document) a.first();
            return Project.fromBson(specDoc);
        } else {
            return null;
        }

    }

    public List<Project> listProjects() {
        Document sortCrit = new Document();
        sortCrit.append("creationStamp", -1);

        MongoCursor a = getProjectCollection().find().sort(sortCrit).iterator();

        List<Project> result = new ArrayList();
        a.forEachRemaining(d -> {
            result.add(Project.fromBson((Document) d));
        });

        return result;
    }

    public List<Project> listProjectsFor(IFloraSubType faunaElementId) {
        Document sortCrit = new Document();
        sortCrit.append("creationStamp", -1);

        MongoCursor a = getProjectCollection().find(elemMatch("members", eq("memberId", faunaElementId.getId()))).sort(sortCrit).iterator();

        List<Project> result = new ArrayList();
        
        a.forEachRemaining(d -> {
            result.add(Project.fromBson((Document) d));
        });

        return result;

    }

    public List<IFloraSubType> listFloraTypesAlphabetically() {
        Document sortCrit = new Document();
        sortCrit.append("latinName", 1);
        MongoCursor a = getFloraCollection().find()
                //                .limit(100)
                .sort(sortCrit).iterator();

        List<IFloraSubType> result = new ArrayList();
        a.forEachRemaining(e -> {
            result.add(documentToIFloraSubType((Document) e));
        });

        return result;
    }

    public List<IFloraSubType> listFloraTypesByLeastTested() {
        Document sortCrit = new Document();
        sortCrit.append("modificationDate", 1);
        List<IFloraSubType> result = new ArrayList();

        getFloraCollection().aggregate(
                Arrays.asList(
                        Aggregates.match(Filters.eq("interrogationDate", null)),
                        //                        Aggregates.limit(100),
                        Aggregates.sort(sortCrit)
                )
        ).iterator().forEachRemaining(e -> {
            result.add(documentToIFloraSubType((Document) e));
        });

        Document sortCrit2 = new Document();
        sortCrit2.append("interrogationDate", 1);

        getFloraCollection().aggregate(
                Arrays.asList(
                        Aggregates.match(Filters.ne("interrogationDate", null)),
                        //                        Aggregates.limit(100),
                        Aggregates.sort(sortCrit2)
                )
        ).iterator().forEachRemaining(e -> {
            result.add(documentToIFloraSubType((Document) e));
        });

        return result;

    }

    public List<IFloraSubType> findFloraByFilter(BsonDocument filter) {
        List<IFloraSubType> result = new ArrayList<>();

        FindIterable findIter;
        if (filter == null) {
            findIter = getFloraCollection().find();

        } else {
            findIter = getFloraCollection().find().filter(filter);
        }

        findIter.iterator().forEachRemaining(e -> {
            result.add(documentToIFloraSubType((Document) e));
        });

        return result;
    }

    private IFloraSubType uploadNewFiles(IFloraSubType FloraElement) {

        if (FloraElement.getMediaReferences() == null || FloraElement.getMediaReferences().size() == 0) {
            return FloraElement;
        }

        for (int i = 0; i < FloraElement.getMediaReferences().size(); i++) {
            if (FloraElement.getMediaReferences().get(i) instanceof Document){
                //Unchanged media reference
            }else if (FloraElement.getMediaReferences().get(i) instanceof File) {
                ObjectId createdId;
                if (((File) FloraElement.getMediaReferences().get(i)).getAbsolutePath().equalsIgnoreCase(getNoImageFoundFileName())) {
                    createdId = new ObjectId(dummyFileId);
                } else {
                    try {
                        createdId = saveFileInGridFs((File) FloraElement.getMediaReferences().get(i));
                    } catch (FileNotFoundException fnfe) {
                        throw new RuntimeException("Unable to store selected files", fnfe);
                    }
                }

                BsonDocument replaceValue = new BsonDocument();
                replaceValue.put("type", new BsonString(MEDIA_GRIDFS));
                replaceValue.put(MEDIA_GRIDFS_ID, new BsonObjectId(createdId));

                FloraElement.getMediaReferences().set(i, replaceValue);
            } else {
                throw new RuntimeException("Only File is allow as classType for upload into the database");
            }
        }
        return FloraElement;

    }

    private ObjectId saveFileInGridFs(File toSave) throws FileNotFoundException {
        GridFSBucket grid = GridFSBuckets.create(connect(), COLL_FLORA + "Media");
        ObjectId linkId = ObjectId.get();
        //ObjectId newId = 
        grid.uploadFromStream(linkId.toHexString(), new FileInputStream(toSave));

        return linkId;
    }

    public File getTempFile(String id) throws IOException {
        GridFSBucket grid = GridFSBuckets.create(connect(), COLL_FLORA + "Media");
        File result = File.createTempFile("qwklrn", "tmp");
        FileOutputStream out = new FileOutputStream(result);
        grid.downloadToStream(id, out);

        return result;
    }

    private IFloraSubType documentToIFloraSubType(Document document) {
        if(document == null){
            return null;
        }
        FloraSubTypeEnum FloraSubType = FloraSubTypeEnum.parse(document.getString("subType"));
        return FloraSubType.getInstance(document, this);

    }

    public static URL noImageUrl(){
        return ClassLoader.getSystemResource(getNoImageFoundFileName());
    }

    public static String getNoImageFoundFileName(){
        return "NoImageFound.jpg";
    }

}
