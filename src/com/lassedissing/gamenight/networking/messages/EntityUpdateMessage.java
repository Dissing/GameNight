/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.networking.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import com.lassedissing.gamenight.events.entity.EntityDiedEvent;
import com.lassedissing.gamenight.events.entity.EntityMovedEvent;
import com.lassedissing.gamenight.events.entity.EntitySpawnedEvent;
import com.lassedissing.gamenight.world.Bullet;
import java.util.List;

@Serializable
public class EntityUpdateMessage extends AbstractMessage {

    public EntityMovedEvent[] movedEvents;
    public EntitySpawnedEvent[] spawnedEvents;
    public EntityDiedEvent[] diedEvents;

    public EntityUpdateMessage(List<EntityMovedEvent> movedEvents, List<EntitySpawnedEvent> spawnedEvents, List<EntityDiedEvent> diedEvents) {
        this.movedEvents = (EntityMovedEvent[]) movedEvents.toArray(new EntityMovedEvent[movedEvents.size()]);
        this.spawnedEvents = (EntitySpawnedEvent[]) spawnedEvents.toArray(new EntitySpawnedEvent[spawnedEvents.size()]);
        this.diedEvents = (EntityDiedEvent[]) diedEvents.toArray(new EntityDiedEvent[diedEvents.size()]);
    }

    /**
     * Serialization
     */
    public EntityUpdateMessage() {

    }
}
