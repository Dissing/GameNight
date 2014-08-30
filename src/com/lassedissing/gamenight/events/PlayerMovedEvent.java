/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.events;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;
import com.lassedissing.gamenight.eventmanagning.EventClosure;
import java.util.ArrayList;
import java.util.List;

@Serializable
public class PlayerMovedEvent extends Event {

    private static List<EventClosure> closures = new ArrayList<>();

    public int playerId;
    public Vector3f position;
    public Vector3f rotation;

    /**
     * Only for serialization
     */
    public PlayerMovedEvent() {

    }

    public PlayerMovedEvent(int playerId, Vector3f position, Vector3f rotation) {
        this.playerId = playerId;
        this.position = position;
        this.rotation = rotation;
    }

    @Override
    public List<EventClosure> getClosures() {
        return closures;
    }

}
