/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.world;

import com.jme3.math.Vector3f;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class World {
    
    private String name;
    private Map<Long,Chunk> chunks = new HashMap<>();
    
    public void generate(int width, int length) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < length; j++) {
                long pos = i;
                pos += j << 16;
                chunks.put(pos, new Chunk(i, j));
            }
        }
    }
    
    public void load(String filename) {
        
    }
    
    public Block getBlockAt(int x, int y, int z) {
        return getChunkAt(x, z).getBlockAt(x & 0xF, y, z & 0xF);
    }
    
    public Block getBlockAt(Vector3f position) {
        return getBlockAt((int)position.x, (int)position.y, (int)position.z);
    }
    
    public Chunk getChunkAt(int x, int z) {
        long pos = x;
        pos += z << 16;
        return chunks.get(pos);
    }
    
    public Chunk getChunkAt(Vector3f position) {
        return getChunkAt((int)position.x, (int)position.z);
    }
    
    public Collection<Chunk> getAllChunks() {
        return chunks.values();
    }
    
    public boolean save() {
        
        return false;
    }
    
}
