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

@Serializable
public class Chunk implements java.io.Serializable {

    public final transient static float BLOCK_SIZE = 1f;
    public final transient static int CHUNK_SIZE = 16;
    public final transient static int CHUNK_AREA = CHUNK_SIZE * CHUNK_SIZE;
    public final transient static int CHUNK_VOLUME = CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE;

    protected int[] blocks = new int[CHUNK_VOLUME];
    protected Vector3f location = new Vector3f();

    /**
     * Only for serialization
     */
    public Chunk() {

    }

    public Chunk(int x, int z)  {
        for (int i = 0; i < CHUNK_SIZE; i++) {
            for (int j = 0; j < CHUNK_SIZE; j++) {
                for (int k = 0; k < CHUNK_SIZE; k++) {
                    if (j == 0) {
                        setIdAt(1,i,j,k);
                    } else if (i == 0 || k == 0 || i == 15 || k == 15) {
                        setIdAt(2,i,j,k);
                    }
                }
            }
        }
        location.set(x, 0, z);
    }

    public Vector3f getLocation() {
        return location;
    }

    public int getX() {
        return (int)location.getX();
    }

    public int getZ() {
        return (int)location.getZ();
    }

    public int getIdAt(int x, int y, int z) {
        return blocks[(x & 0xF)*Chunk.CHUNK_AREA+y*Chunk.CHUNK_SIZE+(z & 0xF)];
    }

    public Block getBlockAt(int x, int y, int z) {
        return new Block(this,(getX() << 4) + (x & 0xF), y & 0xFF , (getZ() << 4) + (z & 0xF));
    }

    public void setIdAt(int value, int x, int y, int z) {
        blocks[(x & 0xF)*Chunk.CHUNK_AREA+y*Chunk.CHUNK_SIZE+(z & 0xF)] = value;
    }

    public boolean isPopulated(int x, int y, int z) {
        int pos = x*CHUNK_AREA+y*CHUNK_SIZE+z;

        //Check if block is outside chunk
        if (x >= CHUNK_SIZE || y >= CHUNK_SIZE || z >= CHUNK_SIZE || x < 0 || y < 0 || z < 0)
            return false;

        //Check if block is not empty
        boolean res = blocks[pos] != 0;
        return res;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();

        for (int block : blocks) {
            oos.writeInt(block);
        }
        oos.writeObject(location);
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        for (int i = 0; i < CHUNK_VOLUME; i++) {
            blocks[i] = ois.readInt();
        }
        location = (Vector3f) ois.readObject();
    }

}
