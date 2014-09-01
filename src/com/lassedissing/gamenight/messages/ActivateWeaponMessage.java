/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.messages;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class ActivateWeaponMessage extends AbstractMessage {

    private int sourceId;
    private Vector3f location;
    private Vector3f direction;

    public ActivateWeaponMessage(int sourceId, Vector3f location, Vector3f direction) {
        this.sourceId = sourceId;
        this.location = location;
        this.direction = direction;
    }

    public int getSourceId() {
        return sourceId;
    }

    public Vector3f getLocation() {
        return location;
    }

    public Vector3f getDirection() {
        return direction;
    }

    /**
     * Serialization
     */
    public ActivateWeaponMessage() {

    }

}
