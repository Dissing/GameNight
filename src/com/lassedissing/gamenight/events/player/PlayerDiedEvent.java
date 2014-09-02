/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.events.player;

import com.jme3.network.serializing.Serializable;
import com.lassedissing.gamenight.eventmanagning.EventClosure;
import java.util.ArrayList;
import java.util.List;

@Serializable
public class PlayerDiedEvent extends PlayerEvent {

    public PlayerDiedEvent(int playerId) {
        this.playerId = playerId;
    }

    /**
     * Only for serialization
     */
    public PlayerDiedEvent() {

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
