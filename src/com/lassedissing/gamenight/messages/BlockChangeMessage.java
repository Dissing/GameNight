/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.messages;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class BlockChangeMessage extends AbstractMessage {

    private int blockType;
    private int x,y,z;

    public BlockChangeMessage(int blockType, int x, int y, int z) {
        this.blockType = blockType;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockChangeMessage(int blockType, Vector3f location) {
        this.blockType = blockType;
        this.x = (int)location.x;
        this.y = (int)location.y;
        this.z = (int)location.z;
    }

    public int getBlockType() {
        return blockType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    /**
     * Serialization
     */
    public BlockChangeMessage() {

    }

}
