/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn;

import com.bville.qwiklurn.repository.flora.DbManager;
import com.bville.qwiklurn.swing.Detail;
import com.bville.qwiklurn.swing.StartUp;
import java.awt.HeadlessException;
import java.io.IOException;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 *
 * @author Bart
 */
public class Runner {

    public static void main(String[] args) throws IOException {
        new Runner().run();
    }

    private void run() throws HeadlessException, IOException {
        new StartUp(new DbManager()).setVisible(true);
        /*
        db = new DbManager();

        List<String> preferredIds = db.getFloraIdsByLeastQueried(2);

        if (preferredIds != null && preferredIds.size() > 0) {
            FloraType elem = db.getFloraById(new ObjectId(preferredIds.get(0)));
            detailScreen = new Detail(db, "Manager");
            detailScreen.refreshWith(elem, null);
        } else {
            detailScreen = new Detail(db, "Aaargh");
        }

        detailScreen.setVisible(true);
*/

    }

}
