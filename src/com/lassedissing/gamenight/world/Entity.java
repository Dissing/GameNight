/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.world;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;

@Serializable
public abstract class Entity {

    protected int id;
    protected Vector3f location;

    public Entity(int id, Vector3f location) {
        this.id = id;
        this.location = location;
    }

    public Vector3f getLocation() {
        return location;
    }

    public void setLocation(Vector3f position) {
        location.set(position);
    }

    public int getId() {
        return id;
    }

    public abstract EntityType getType();

    /**
     * Serialization
     */
    public Entity() {

    }

}
