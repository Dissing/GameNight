/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.events.entity;

import com.jme3.network.serializing.Serializable;
import com.lassedissing.gamenight.eventmanagning.EventClosure;
import com.lassedissing.gamenight.world.Entity;
import com.lassedissing.gamenight.world.EntityType;
import java.util.ArrayList;
import java.util.List;

@Serializable
public class EntityDiedEvent extends EntityEvent {

    public EntityDiedEvent(int id, EntityType type) {
        this.id = id;
        this.type = type;
    }

    public EntityDiedEvent(Entity entity) {
        this.id = entity.getId();
        this.type = entity.getType();
    }

    /**
     * Serialization
     */
    public EntityDiedEvent() {

    }

    //ClosureHolder section

    private static List<EventClosure> closures = new ArrayList<>();

    @Override
    public int getClosureLevel() {
        return 2;
    }

    @Override
    public List<EventClosure> getClosures(int level) {
        switch (level) {
            case 0:
            case 1:     return super.getClosures(level);
            case 2:     return closures;
            default:    throw new UnsupportedOperationException("Level " + level + " is not supported by " + getEventName());
        }
    }

}
