/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import com.lassedissing.gamenight.world.Chunk;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

@Serializable
public class ChunkMessage extends AbstractMessage {

    private byte[] compressedData;
    private int x;
    private int z;

    public ChunkMessage() {

    }

    public ChunkMessage(Chunk chunk) {
        compressedData = compress(chunk.getRawArray());
        x = chunk.getX();
        z = chunk.getZ();
    }

    public Chunk getChunk() {
        return new Chunk(x, z, decompress(compressedData));
    }

    private byte[] compress(int[] data) {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(16384);
        DeflaterOutputStream deflater = new DeflaterOutputStream(byteOutputStream);
        DataOutputStream out = new DataOutputStream(deflater);
        int count  = 0;
        try {
            for (int integer : data) {
                out.writeInt(integer);
                count++;
            }
            out.flush();
            deflater.finish();
            out.close();
            deflater.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteOutputStream.toByteArray();
    }

    private int[] decompress(byte[] data) {
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(data);
        InflaterInputStream inflater = new InflaterInputStream(byteInputStream);
        DataInputStream in = new DataInputStream(inflater);
        int[] result = new int[Chunk.CHUNK_VOLUME];
        int count = 0;
        try {
            for (int i = 0; i < Chunk.CHUNK_VOLUME; i++) {
                result[i] = in.readInt();
                count++;
            }

            in.close();
            inflater.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Partial chunk message!");
        }
        return result;
    }
}
