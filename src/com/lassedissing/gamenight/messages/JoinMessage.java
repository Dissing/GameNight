/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class JoinMessage extends AbstractMessage {

    private String playerName;

    public JoinMessage(String playerName) {
        this.playerName = playerName;
    }

    public String getName() {
        return playerName;
    }

    /**
     * Serialization
     */
    public JoinMessage() {

    }

}
