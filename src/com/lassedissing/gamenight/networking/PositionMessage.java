/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.networking;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class PositionMessage extends AbstractMessage {
    
    public int playerId;
    public Vector3f playerPos;
    
    public PositionMessage() {
        
    }
    
    public PositionMessage(int playerId, Vector3f playerPos) {
        this.playerId = playerId;
        this.playerPos = playerPos;
    }

}
