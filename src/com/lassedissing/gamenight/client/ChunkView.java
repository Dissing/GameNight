/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client;

import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import com.lassedissing.gamenight.world.Chunk;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;


public class ChunkView {
    
    private final transient static int UP_NORMAL_BITMASK = 0x0F000000;
    
    private Chunk chunk;
    
    private Mesh mesh = new Mesh();
    
    private FloatBuffer vertices = BufferUtils.createFloatBuffer(Chunk.CHUNK_VOLUME * 6 * 2 * 3 * 3); //Six faces each of 2 triangles of 3 vertices consisting of 3 floats 
    private IntBuffer blockInfo = BufferUtils.createIntBuffer(Chunk.CHUNK_VOLUME * 6 * 2 * 3); //Six faces each of 2 triangles of 3 vertices consisting of 1 int
    
    
    public ChunkView(Chunk chunk) {
        this.chunk = chunk;
    }
    
    public void buildMesh() {
        vertices.clear();
        blockInfo.clear();
        Vector3f v[] = new Vector3f[8];
        for (int i = 0; i < 8; i++) {
            v[i] = new Vector3f();
        }
        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int y = 0; y < Chunk.CHUNK_SIZE; y++) {
                for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                    int type = chunk.getIdAt(x, y, z);
                    if (type == 0) {
                        // Don't generate any mesh for empty blocks
                        continue;
                    }
                    
                    //Front quad
                     v[0].set(x, y, z+Chunk.BLOCK_SIZE);
                     v[1].set(x+Chunk.BLOCK_SIZE, y, z+Chunk.BLOCK_SIZE);
                     v[2].set(x+Chunk.BLOCK_SIZE, y+Chunk.BLOCK_SIZE, z+Chunk.BLOCK_SIZE);
                     v[3].set(x, y+Chunk.BLOCK_SIZE, z+Chunk.BLOCK_SIZE);
                     //Back quad
                     v[4].set(x, y, z);
                     v[5].set(x+Chunk.BLOCK_SIZE, y, z);
                     v[6].set(x+Chunk.BLOCK_SIZE, y+Chunk.BLOCK_SIZE, z);
                     v[7].set(x, y+Chunk.BLOCK_SIZE, z);
                     
                     //Front triangles
                     if (!chunk.isPopulated(x, y, z+1)) {
                        addVertices(v[0],v[1],v[2],calcBlockMask(type, false));
                        addVertices(v[0],v[2],v[3],calcBlockMask(type, false));
                     }
                     
                     //Back triangles
                     if (!chunk.isPopulated(x, y, z-1)) {
                        addVertices(v[6],v[5],v[4],calcBlockMask(type, false));
                        addVertices(v[7],v[6],v[4],calcBlockMask(type, false));
                     }
                     
                     //Left triangles
                     if (!chunk.isPopulated(x-1, y, z)) {
                        addVertices(v[0],v[7],v[4],calcBlockMask(type, false));
                        addVertices(v[0],v[3],v[7],calcBlockMask(type, false));
                     }
                     
                     //Right triangles
                     if (!chunk.isPopulated(x+1, y, z)) {
                        addVertices(v[1],v[5],v[6],calcBlockMask(type, false));
                        addVertices(v[1],v[6],v[2],calcBlockMask(type, false));
                     }
                     
                     //Top triangles
                     if (!chunk.isPopulated(x, y+1, z)) {
                        addVertices(v[2],v[7],v[3],calcBlockMask(type, true));
                        addVertices(v[2],v[6],v[7],calcBlockMask(type, true));
                     }
                     
                     //Bottom triangles
                     if (!chunk.isPopulated(x, y-1, z)) {
                        addVertices(v[4],v[1],v[0],calcBlockMask(type, true));
                        addVertices(v[4],v[5],v[1],calcBlockMask(type, true));
                     }
                    
                }
            }
        }
        
        vertices.flip();
        blockInfo.flip();
        mesh.setBuffer(VertexBuffer.Type.Position, 3, vertices);
        mesh.setBuffer(VertexBuffer.Type.Normal,1,blockInfo);
        mesh.updateBound();
    }
    
    private int calcBlockMask(int blockType, boolean yFace) {
        int res = blockType;
        if (yFace) {
            res = res | UP_NORMAL_BITMASK;
        }
        
        return res;
    }
    
    public void addVertices(Vector3f v1, Vector3f v2, Vector3f v3, int blockMask) {
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
            blockInfo.put(blockMask);
        }
    }
    
    public Mesh getMesh() {
        return mesh;
    }

}
