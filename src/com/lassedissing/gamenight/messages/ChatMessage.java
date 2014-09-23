/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class ChatMessage extends AbstractMessage {

    private int playerId;
    private String message;
    private boolean fromServer;

    public ChatMessage(int playerId, String message) {
        this.playerId = playerId;
        this.message = message;
    }

    public ChatMessage(String message, boolean fromServer) {
        assert(fromServer);
        this.message = message;
        this.fromServer = fromServer;
        this.playerId = -1;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Serialization
     */
    public ChatMessage() {
    }

}
