/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import com.lassedissing.gamenight.world.Chunk;
import static com.lassedissing.gamenight.world.Chunk.CHUNK_SIZE;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ChunkManager {
    
    private final static int UP_NORMAL_BITMASK = 0x0F000000;
    
    private Map<Long,ChunkView> chunks = new HashMap<>();
    
    private Material blockMaterial;
    private Node sceneNode;
    
    public ChunkManager() {
    }
    
    public Node init(AssetManager assetManager) {
        blockMaterial = new Material(assetManager, "MatDefs/Block.j3md");
        blockMaterial.getAdditionalRenderState().setWireframe(false);

        Texture texAtlas = assetManager.loadTexture("Textures/TextureAtlas.png");
        blockMaterial.setTexture("Atlas", texAtlas);
        texAtlas.setMinFilter(Texture.MinFilter.NearestLinearMipMap);
        texAtlas.setMagFilter(Texture.MagFilter.Nearest);
        texAtlas.setAnisotropicFilter(16);
        sceneNode = new Node();
        return sceneNode;
    }
    
    /*public boolean getCollision(Vector3f location, Vector3f width) {
        int x = (int)location.x;
        int y = (int)location.y;
        int z = (int)location.z;
        Chunk chunk = getChunk((int)location.x / CHUNK_SIZE, (int)location.y / CHUNK_SIZE).chunk.getIdAt(CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE);
    }
    
    public int getId(int x, int y, int z) {
        getChunk(x / 16, z / 16).chunk.getIdAt(x % 16, y, z % 16);
    }*/
    
    public void addChunk(Chunk chunk) {
        
        int x = chunk.getX();
        int y = chunk.getY();
        int z = chunk.getZ();
        
        System.out.println("New chunk with location: " + x + " " + y + " " + z);
        
        
        ChunkView view = new ChunkView(chunk);
        long pos = x;
        pos += z << 16;
        chunks.put(pos, view);
        buildChunkMesh(view);
        addChunkToSceneGraph(view);
        rebuildChunk(x-1,z);
        rebuildChunk(x,  z-1);
        rebuildChunk(x+1,z);
        rebuildChunk(x,  z+1);
    }
    
    private ChunkView getChunk(int x, int z) {
        long pos = x;
        pos += z << 16;
        return chunks.get(pos);
    }
    
    private void rebuildChunk(int x, int z) {
        ChunkView chunk = getChunk(x,z);
        if (chunk != null) {
            buildChunkMesh(chunk);
            chunk.mesh.updateBound();
        }
    }
    
    private void buildChunkMesh(ChunkView view) {
        view.vertices.clear();
        view.blockInfo.clear();
        Vector3f v[] = new Vector3f[8];
        for (int i = 0; i < 8; i++) {
            v[i] = new Vector3f();
        }
        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int y = 0; y < Chunk.CHUNK_SIZE; y++) {
                for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                    int type = view.chunk.getIdAt(x, y, z);
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
                     if (!isPopulated(view,x, y, z+1)) {
                        view.addVertices(v[0],v[1],v[2],calcBlockMask(type, false));
                        view.addVertices(v[0],v[2],v[3],calcBlockMask(type, false));
                     }
                     
                     //Back triangles
                     if (!isPopulated(view,x, y, z-1)) {
                        view.addVertices(v[6],v[5],v[4],calcBlockMask(type, false));
                        view.addVertices(v[7],v[6],v[4],calcBlockMask(type, false));
                     }
                     
                     //Left triangles
                     if (!isPopulated(view,x-1, y, z)) {
                        view.addVertices(v[0],v[7],v[4],calcBlockMask(type, false));
                        view.addVertices(v[0],v[3],v[7],calcBlockMask(type, false));
                     }
                     
                     //Right triangles
                     if (!isPopulated(view,x+1, y, z)) {
                        view.addVertices(v[1],v[5],v[6],calcBlockMask(type, false));
                        view.addVertices(v[1],v[6],v[2],calcBlockMask(type, false));
                     }
                     
                     //Top triangles
                     if (!isPopulated(view,x, y+1, z)) {
                        view.addVertices(v[2],v[7],v[3],calcBlockMask(type, true));
                        view.addVertices(v[2],v[6],v[7],calcBlockMask(type, true));
                     }
                     
                     //Bottom triangles
                     if (!isPopulated(view,x, y-1, z)) {
                        view.addVertices(v[4],v[1],v[0],calcBlockMask(type, true));
                        view.addVertices(v[4],v[5],v[1],calcBlockMask(type, true));
                     }
                    
                }
            }
        }
        
        view.updateBuffers();
        
    }
    
    private void addChunkToSceneGraph(ChunkView view) {
        view.geo = new Geometry("Chunk"+view.chunk.getLocation().toString(),view.mesh);
        
        view.geo.setMaterial(blockMaterial);
        view.geo.setLocalTranslation(view.chunk.getLocation().mult(Chunk.CHUNK_SIZE));

        sceneNode.attachChild(view.geo);
    }
    
    private boolean isPopulated(ChunkView view, int x, int y, int z) {
        
        //Check if chunk exists
        if (view == null) {
            return false;
        }
        
        //Check if block is outside chunk
        if (y >= CHUNK_SIZE || y < 0) {
            return false;
        }
        if (x >= CHUNK_SIZE) {
            return isPopulated(getChunk(view.chunk.getX()+1, view.chunk.getZ()),1,y,z);
        }
        
        if (z >= CHUNK_SIZE) {
            return isPopulated(getChunk(view.chunk.getX(), view.chunk.getZ()+1),x,y,1);
        }
        
        if (x < 0) {
            return isPopulated(getChunk(view.chunk.getX()-1, view.chunk.getZ()),15,y,z);
        }
        
        if (z < 0) {
            return isPopulated(getChunk(view.chunk.getX(), view.chunk.getZ()-1),x,y,15);
        }
        
        //Check if block is not empty
        boolean res = view.chunk.getIdAt(x, y, z) != 0;
        return res;
    }
    
    private int calcBlockMask(int blockType, boolean yFace) {
        int res = blockType;
        if (yFace) {
            res = res | UP_NORMAL_BITMASK;
        }
        
        return res;
    }
    
    private class ChunkView {
        
        public ChunkView(Chunk chunk) {
            this.chunk = chunk;
            mesh = new Mesh();
        }
        
        public Chunk chunk;
        
        public Mesh mesh;
        public Geometry geo;
    
        public FloatBuffer vertices = BufferUtils.createFloatBuffer(Chunk.CHUNK_VOLUME * 6 * 2 * 3 * 3); //Six faces each of 2 triangles of 3 vertices consisting of 3 floats 
        public IntBuffer blockInfo = BufferUtils.createIntBuffer(Chunk.CHUNK_VOLUME * 6 * 2 * 3); //Six faces each of 2 triangles of 3 vertices consisting of 1 int
        
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
        
        public void updateBuffers() {
            vertices.flip();
            blockInfo.flip();
            mesh.setBuffer(VertexBuffer.Type.Position, 3, vertices);
            mesh.setBuffer(VertexBuffer.Type.Normal,1,blockInfo);
            mesh.updateBound();
        }
        
    }
    
}
