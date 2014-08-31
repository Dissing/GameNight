/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;


public class BulletView {

    private int id;
    private Geometry geo;

    public BulletView(int id, Vector3f location, Node parent, Main app) {

        geo = new Geometry("BulletBack " + id, new Box(0.05f, 0.05f,0.1f));

        Material mat = new Material(app.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Orange);
        geo.setMaterial(mat);
        parent.attachChild(geo);

        geo.setLocalTranslation(location);
        System.out.println("Spawning new bullet at " + location.toString());
    }

    public void setLocation(Vector3f location) {
        geo.setLocalTranslation(location);
    }

    public int getId() {
        return id;
    }

}
