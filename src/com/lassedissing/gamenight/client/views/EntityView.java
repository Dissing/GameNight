/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client.views;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;


public class EntityView {

    protected int id;
    protected Spatial spatial;

    public EntityView(int id) {
        this.id = id;
    }

    public void setLocation(Vector3f location) {
        spatial.setLocalTranslation(location);
    }

    public int getId() {
        return id;
    }

    public void destroy() {
        spatial.removeFromParent();
    }

}
