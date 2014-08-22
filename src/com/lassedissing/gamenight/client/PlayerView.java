/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;


public class PlayerView {
    
    private int id;
    private Geometry geo;
    
    
    public PlayerView(int id, Node parent, Main app) {
        this.id = id;
        Box b = new Box(0.8f,1.8f,0.8f);
        geo = new Geometry("Player: " + id, b);
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geo.setMaterial(mat);   
        geo.setLocalTranslation(0.8f, 18f, 0.8f);
        parent.attachChild(geo);
    }
    
    public void setPosition(Vector3f pos) {
        geo.setLocalTranslation(pos);
    }
    
}