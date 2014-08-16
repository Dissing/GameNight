/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.world;

import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;


public class Chunk extends Mesh{
    
    private final transient static float BLOCK_SIZE = 1f;
    private final transient static int CHUNK_SIZE = 4;
    private final transient static int CHUNK_AREA = CHUNK_SIZE * CHUNK_SIZE;
    private final transient static int CHUNK_VOLUME = CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE;
    
    private int[] blocks = new int[CHUNK_VOLUME];
    
    private transient FloatBuffer vertices = BufferUtils.createFloatBuffer(CHUNK_VOLUME * 6 * 2 * 3 * 3); //Six faces each of 2 triangles of 3 vertices consisting of 3 floats 
    private transient IntBuffer blockInfo = BufferUtils.createIntBuffer(CHUNK_VOLUME * 6 * 2 * 3); //Six faces each of 2 triangles of 3 vertices consisting of 1 int
    
    public void create(boolean allStone)  {
        for (int i = 0; i < CHUNK_VOLUME; i++) {
            //Set all blocks to either stone or empty depending on the parameter
            blocks[i] = (Math.random() > 0.5) ? 1 : 0;
        }
    }
    
    public void buildMesh() {
        vertices.clear();
        blockInfo.clear();
        Vector3f v[] = new Vector3f[8];
        for (int i = 0; i < 8; i++) {
            v[i] = new Vector3f();
        }
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {
                    int type = blocks[x*CHUNK_AREA+y*CHUNK_SIZE+z];
                    if (type == 0) {
                        // Don't generate any mesh for empty blocks
                        continue;
                    }
                    
                    //Front quad
                     v[0].set(x, y, z+BLOCK_SIZE);
                     v[1].set(x+BLOCK_SIZE, y, z+BLOCK_SIZE);
                     v[2].set(x+BLOCK_SIZE, y+BLOCK_SIZE, z+BLOCK_SIZE);
                     v[3].set(x, y+BLOCK_SIZE, z+BLOCK_SIZE);
                     //Back quad
                     v[4].set(x, y, z);
                     v[5].set(x+BLOCK_SIZE, y, z);
                     v[6].set(x+BLOCK_SIZE, y+BLOCK_SIZE, z);
                     v[7].set(x, y+BLOCK_SIZE, z);
                     
                     //Front triangles
                     if (!isPopulated(x, y, z+1)) {
                        addVertices(v[0],v[1],v[2],false);
                        addVertices(v[0],v[2],v[3],false);
                     }
                     
                     //Back triangles
                     if (!isPopulated(x, y, z-1)) {
                        addVertices(v[6],v[5],v[4],false);
                        addVertices(v[7],v[6],v[4],false);
                     }
                     
                     //Left triangles
                     if (!isPopulated(x-1, y, z)) {
                        addVertices(v[0],v[7],v[4],false);
                        addVertices(v[0],v[3],v[7],false);
                     }
                     
                     //Right triangles
                     if (!isPopulated(x+1, y, z)) {
                        addVertices(v[1],v[5],v[6],false);
                        addVertices(v[1],v[6],v[2],false);
                     }
                     
                     //Top triangles
                     if (!isPopulated(x, y+1, z)) {
                        addVertices(v[2],v[7],v[3],true);
                        addVertices(v[2],v[6],v[7],true);
                     }
                     
                     //Bottom triangles
                     if (!isPopulated(x, y-1, z)) {
                        addVertices(v[4],v[1],v[0],true);
                        addVertices(v[4],v[5],v[1],true);
                     }
                    
                }
            }
        }
        
        vertices.flip();
        blockInfo.flip();
        setBuffer(VertexBuffer.Type.Position, 3, vertices);
        //setBuffer(VertexBuffer.Type.Normal,1,blockInfo);
        updateBound();
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
    
    public void addVertices(Vector3f v1, Vector3f v2, Vector3f v3, boolean yface) {
        vertices.put(v1.x);
        vertices.put(v1.y);
        vertices.put(v1.z);
        vertices.put(v2.x);
        vertices.put(v2.y);
        vertices.put(v2.z);
        vertices.put(v3.x);
        vertices.put(v3.y);
        vertices.put(v3.z);
        for (int i = 0; i < 3; i++) {
            blockInfo.put(2);
        }
    }
    
}
