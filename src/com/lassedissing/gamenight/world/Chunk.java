/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.world;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidParameterException;

@Serializable
public class Chunk implements java.io.Serializable {

    public final transient static float BLOCK_SIZE = 1f;
    public final transient static int CHUNK_SIZE = 16;
    public final transient static int CHUNK_HEIGHT = 32;
    public final transient static int CHUNK_AREA = CHUNK_SIZE * CHUNK_SIZE;
    public final transient static int CHUNK_VOLUME = CHUNK_SIZE * CHUNK_SIZE * CHUNK_HEIGHT;

    protected int[] blocks = new int[CHUNK_VOLUME];
    protected int x;
    protected int z;

    public Chunk(int x, int z)  {
        for (int i = 0; i < CHUNK_SIZE; i++) {
            for (int j = 0; j < CHUNK_HEIGHT; j++) {
                for (int k = 0; k < CHUNK_SIZE; k++) {
                    setIdAt(getLayerAtHeight(j), i, j, k);
                }
            }
        }
        this.x = x;
        this.z = z;
    }

    public static int getLayerAtHeight(int y) {
        if (y == 0) return 2;
        if (y < 6) return 1;
        if (y < 9) return 3;
        if (y == 9) return 4;
        return 0;
    }

    public Chunk(int x, int z, int[] blocks) {
        if (blocks.length != CHUNK_VOLUME) {
            throw new InvalidParameterException("Length of blocks array must be exactly " + CHUNK_VOLUME);
        }
        this.blocks = blocks;
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public int getIdAt(int x, int y, int z) {
        return blocks[(x & 0xF)*Chunk.CHUNK_HEIGHT*CHUNK_SIZE+y*Chunk.CHUNK_SIZE+(z & 0xF)];
    }

    public Block getBlockAt(int x, int y, int z) {
        return new Block(this,(getX() << 4) + (x & 0xF), y & 0xFF , (getZ() << 4) + (z & 0xF));
    }

    public void setIdAt(int value, int x, int y, int z) {
        blocks[(x & 0xF)*Chunk.CHUNK_HEIGHT*CHUNK_SIZE+y*Chunk.CHUNK_SIZE+(z & 0xF)] = value;
    }

    public boolean isPopulated(int x, int y, int z) {
        int pos = x*CHUNK_SIZE*CHUNK_HEIGHT+y*CHUNK_SIZE+z;

        //Check if block is outside chunk
        if (x >= CHUNK_SIZE || y >= CHUNK_HEIGHT || z >= CHUNK_SIZE || x < 0 || y < 0 || z < 0)
            return false;

        //Check if block is not empty
        boolean res = blocks[pos] != 0;
        return res;
    }

    public int[] getRawArray() {
        return blocks;
    }

    /**
    * Only for serialization
    */
    public Chunk() {

    }

}
