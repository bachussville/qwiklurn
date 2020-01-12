/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.swing;

public enum ActionType {
    ADD_NEW(false, true, true, false),
    STUDY(true, false, true, false),
    INTERROGATION(true, false, false, true);

    private final boolean listProcessingAction;
    private final boolean allowsElementCreation;
    private final boolean allowsElementUpdate;
    private final boolean allowsElementIntgerrogation;

    private ActionType(boolean listProcessingAction, boolean allowsElementCreation, boolean allowsElementUpdate, boolean allowsElementIntgerrogation) {
        this.listProcessingAction = listProcessingAction;
        this.allowsElementCreation = allowsElementCreation;
        this.allowsElementUpdate = allowsElementUpdate;
        this.allowsElementIntgerrogation = allowsElementIntgerrogation;
        
    }

    public boolean isListProcessingAction() {
        return listProcessingAction;
    }

    public boolean allowsElementCreation() {
        return allowsElementCreation;
    }

    public boolean allowsElementUpdate() {
        return allowsElementUpdate;
    }
    
    public boolean allowsElementIntgerrogation() {
        return allowsElementIntgerrogation;
    }
    
}
