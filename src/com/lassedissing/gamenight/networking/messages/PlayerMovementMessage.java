/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.networking.messages;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import com.lassedissing.gamenight.events.PlayerMovedEvent;
import java.util.List;

@Serializable
public class PlayerMovementMessage extends AbstractMessage {

    public PlayerMovedEvent[] events;

    public PlayerMovementMessage() {

    }

    public PlayerMovementMessage(PlayerMovedEvent event) {
        events = new PlayerMovedEvent[1];
        events[0] = event;
    }

    public PlayerMovementMessage(List<PlayerMovedEvent> events) {
        this.events = (PlayerMovedEvent[]) events.toArray();
    }

}
