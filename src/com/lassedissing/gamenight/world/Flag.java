/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.world;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;
import com.lassedissing.gamenight.eventmanagning.EventManager;
import com.lassedissing.gamenight.events.entity.EntitySpawnedEvent;

@Serializable
public class Flag extends Entity {

    private boolean pickedUp = false;
    private int team;

    public Flag(int id, int team, Vector3f location) {
        super(id, location);
        this.team = team;
        EventManager.sendEvent(new EntitySpawnedEvent(this));
    }

    public void setIsPickedUp(boolean pickedUp) {
        this.pickedUp = pickedUp;
    }

    public boolean isPickedUp() {
        return pickedUp;
    }

    @Override
    public EntityType getType() {
        return EntityType.Flag;
    }

    public int getTeam() {
        return team;
    }

    /**
     * Serialization
     */
    public Flag() {

    }

}
