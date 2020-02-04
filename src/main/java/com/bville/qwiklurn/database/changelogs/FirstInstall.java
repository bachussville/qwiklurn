/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.database.changelogs;

import com.bville.qwiklurn.repository.flora.DbManager;

/**
 *
 * @author Bart
 */
public class FirstInstall {
    public static void main(String[] args) {
        DbManager d = new DbManager("Qwiklurn");
        d.setUpDatabase();
    }
}
