/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.networking.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import com.lassedissing.gamenight.events.PlayerNewEvent;
import com.lassedissing.gamenight.world.Player;
import java.util.Collection;

@Serializable
public class WelcomeMessage extends AbstractMessage {

    public int playerId;
    public PlayerNewEvent[] otherPlayers;

    public WelcomeMessage(int playerId, Collection<Player> otherPlayers) {
        this.playerId = playerId;

        int i = 0;
        this.otherPlayers = new PlayerNewEvent[otherPlayers.size()];
        for (Player player : otherPlayers) {
            this.otherPlayers[i++] = new PlayerNewEvent(player.getId());
        }
    }

    /**
     * Serialization
     */
    public WelcomeMessage() {

    }
}
