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
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.lassedissing.gamenight.client.Main;


public class FlagView extends EntityView {

    public FlagView(int id, int team, Vector3f location, Node parent, Main app) {
        super(id);

        Geometry flagGeo = new Geometry("Flag" + id, new Box(0.4f, 0.2f,0.02f));
        Geometry poleGeo = new Geometry("FlagPole" + id, new Box(0.015f, 0.9f, 0.015f));

        Material flagMat = new Material(app.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
        Material poleMat = flagMat.clone();
        flagMat.setColor("Color", ColorRGBA.Blue);
        poleMat.setColor("Color", ColorRGBA.Brown);
        flagGeo.setMaterial(flagMat);
        poleGeo.setMaterial(poleMat);

        spatial = new Node();
        ((Node)spatial).attachChild(flagGeo);
        ((Node)spatial).attachChild(poleGeo);
        parent.attachChild(spatial);

        spatial.setLocalTranslation(location);
        flagGeo.setLocalTranslation(0, 0.6f, 0);
    }

    public void hide(boolean enabled) {
        spatial.setCullHint(enabled ? Spatial.CullHint.Always : Spatial.CullHint.Dynamic);
    }

}
