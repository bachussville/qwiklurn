/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.repository.flora;

import com.bville.qwiklurn.swing.InterrogationSetup;
import java.util.HashMap;
import javax.swing.JPanel;
import org.bson.Document;

/**
 *
 * @author Bart
 */
public interface IFloraSubType extends IFlora{

    public void setSubTypeAttributes(Document doc);

    public void setSubTypeAttributes(IFloraSubType source);

    public HashMap<String, Object> getUpdateAttributesList();
    
    public JPanel getSubTypePropertiesPanel();
    
    public void refreshSubTypeComponents();
    
    public void setUpPanelForInterrogation(InterrogationSetup interrS);
    
    public boolean validateInterrogatedValues();
    
    public void restoreBGColorForSubTypeComponents();

}
