/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.networking;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import com.lassedissing.gamenight.world.Chunk;

@Serializable
public class ChunkMessage extends AbstractMessage {

    public Chunk chunk;
    
    public ChunkMessage() {
        
    }
    
    public ChunkMessage(Chunk chunk) {
        this.chunk = chunk;
    }
}