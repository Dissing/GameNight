/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.world;

import com.lassedissing.gamenight.eventmanagning.EventManager;
import com.lassedissing.gamenight.events.BlockChangeEvent;


public class Block {

    private final Chunk chunk;
    private final int x;
    private final int y;
    private final int z;

    public Block(Chunk chunk, int x, int y, int z) {
        this.chunk = chunk;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getType() {
        return chunk.getIdAt(x, y, z);
    }

    public boolean isBulletCollidable() {
        return getType() != 0;
    }

    public void setType(int type) {
        chunk.setIdAt(type, x & 0xF, y, z & 0xF);
        EventManager.sendEvent(new BlockChangeEvent(type, x, y, z));
    }

}
