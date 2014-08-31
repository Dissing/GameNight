/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.networking.messages;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class ActivateWeaponMessage extends AbstractMessage {

    public Vector3f location;
    public Vector3f direction;

    /**
     * Serialization
     */
    public ActivateWeaponMessage() {

    }

    public ActivateWeaponMessage(Vector3f location, Vector3f direction) {
        this.location = location;
        this.direction = direction;
    }

}
