/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.events.entity;

import com.lassedissing.gamenight.eventmanagning.EventClosure;
import com.lassedissing.gamenight.events.Event;
import com.lassedissing.gamenight.world.EntityType;
import java.util.ArrayList;
import java.util.List;


public class EntityEvent extends Event {

    protected int id;
    protected EntityType type;

    public int getId() {
        return id;
    }

    public EntityType getType() {
        return type;
    }

    //ClosureHolder section

    private static List<EventClosure> closures = new ArrayList<>();

    @Override
    public int getClosureLevel() {
        return 1;
    }

    @Override
    public List<EventClosure> getClosures(int level) {
        switch (level) {
            case 0:     return super.getClosures(level);
            case 1:     return closures;
            default:    throw new UnsupportedOperationException("Level " + level + " is not supported by " + getEventName());
        }
    }

}
