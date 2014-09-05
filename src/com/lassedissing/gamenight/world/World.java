/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.world;

import com.jme3.math.Vector3f;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

public class World implements Serializable {

    private String name;
    private HashMap<Long,Chunk> chunks = new HashMap<>();
    private int chunkWidth;
    private int chunkLength;
    private int chunkHeight;
    private int blockWidth;
    private int blockLength;
    private int blockHeight;

    public World(String name) {
        this.name = name;
    }

    public void generate(int width, int length) {
        chunkWidth = width;
        chunkHeight = 1;
        chunkLength = length;
        blockWidth = width * 16;
        blockLength = length * 16;
        blockHeight = 32;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < length; j++) {
                long pos = i;
                pos += j << 16;
                chunks.put(pos, new Chunk(i, j));
            }
        }
        for (int h = 0; h < blockHeight; h++) {
            for (int w = 0; w < blockWidth; w++) {
                getBlockAt(w, h, 0).setType(2);
                getBlockAt(w, h, blockLength-1).setType(2);
            }
            for (int l = 0; l < blockLength; l++) {
                getBlockAt(0, h, l).setType(2);
                getBlockAt(blockWidth-1, h, l).setType(2);
            }
        }
        setWall(true,31,64);
    }

    public void setWall(boolean enabled, int p1, int p2) {
        for (int h = 0; h < blockHeight; h++) {
            for (int w = 0; w < blockWidth; w++) {
                getBlockAt(w,h,p1).setType(enabled ? 2 : Chunk.getLayerAtHeight(h));
                getBlockAt(w,h,p2).setType(enabled ? 2 : Chunk.getLayerAtHeight(h));
            }
        }
    }

    public static World load(String filename) {
        FileInputStream in = null;
        ObjectInputStream stream = null;
        try {
            in = new FileInputStream("levels/"+filename+".map");
             stream = new ObjectInputStream(in);
            return (World) stream.readObject();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Block getBlockAt(int x, int y, int z) {
        return getChunkAt(x >> 4, z >> 4).getBlockAt(x & 0xF, y, z & 0xF);
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

    public String getName() {
        return name;
    }

    public int getChunkWidth() {
        return chunkWidth;
    }

    public int getChunkHeight() {
        return chunkHeight;
    }

    public int getChunkLength() {
        return chunkLength;
    }

    public int getBlockWidth() {
        return blockWidth;
    }

    public int getBlockHeight() {
        return blockHeight;
    }

    public int getBlockLength() {
        return blockLength;
    }

    public boolean save(String name) {
        this.name = name;
        return save();
    }

    public boolean save() {
        File file = new File("levels/"+name+".map");
        FileOutputStream out = null;
        ObjectOutputStream stream = null;
        try {
            file.createNewFile();
            out = new FileOutputStream(file);
            stream = new ObjectOutputStream(out);
            stream.writeObject(this);
            return true;
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
