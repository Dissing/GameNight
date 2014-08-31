/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.eventmanagning;

import com.jme3.network.AbstractMessage;
import com.lassedissing.gamenight.events.entity.EntityDiedEvent;
import com.lassedissing.gamenight.events.entity.EntityEvent;
import com.lassedissing.gamenight.events.entity.EntityMovedEvent;
import com.lassedissing.gamenight.events.entity.EntitySpawnedEvent;
import com.lassedissing.gamenight.networking.messages.EntityUpdateMessage;
import java.util.ArrayList;
import java.util.List;


public class EventStacker implements EventListener {


    private List<EntitySpawnedEvent> spawnedEvents = new ArrayList<>();
    private List<EntityMovedEvent> movedEvents = new ArrayList<>();
    private List<EntityDiedEvent> diedEvents = new ArrayList<>();

    @EventHandler
    public void onEntityEvent(EntityEvent event) {
        if (event instanceof EntityMovedEvent) {
            movedEvents.add((EntityMovedEvent)event);
        } else if (event instanceof EntitySpawnedEvent) {
            spawnedEvents.add((EntitySpawnedEvent)event);
        } else if (event instanceof EntityDiedEvent) {
            diedEvents.add((EntityDiedEvent)event);
        }
    }

    public EntityUpdateMessage bakeEntityMessage() {
        EntityUpdateMessage msg = new EntityUpdateMessage(movedEvents,spawnedEvents,diedEvents);
        spawnedEvents.clear();
        movedEvents.clear();
        diedEvents.clear();
        return msg;
    }
}
