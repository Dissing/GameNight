/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client.views;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.lassedissing.gamenight.client.Main;


public class BulletView extends EntityView {

    public BulletView(int id, Vector3f location, Node parent, Main app) {
        super(id);

        spatial = new Geometry("Bullet" + id, new Box(0.05f, 0.05f,0.1f));

        Material mat = new Material(app.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Orange);
        spatial.setMaterial(mat);
        parent.attachChild(spatial);

        spatial.setLocalTranslation(location);
    }

}
