/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.texture.Texture;
import com.jme3.texture.image.ImageRaster;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;


public class WeaponView {

    public Material weaponMaterial;

    public Mesh mesh;
    private FloatBuffer vertices = BufferUtils.createFloatBuffer(32*32*3*2*6*3);
    private FloatBuffer uv = BufferUtils.createFloatBuffer(32*32*3*2*6*2);;
    private float scalingFactor = (1f/32f);

    public WeaponView(String name, AssetManager assetManager) {
        weaponMaterial = new Material(assetManager, "MatDefs/Weapon.j3md");

        Texture texture = assetManager.loadTexture("Textures/"+name+".png");
        texture.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        texture.setMagFilter(Texture.MagFilter.Nearest);
        weaponMaterial.setTexture("Texture", texture);
        weaponMaterial.setColor("Color", ColorRGBA.Black);
        mesh = new Mesh();

        generateMesh(texture);

        vertices.flip();
        uv.flip();
        mesh.setBuffer(VertexBuffer.Type.Position, 3, vertices);
        mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, uv);
        mesh.updateBound();
    }

    private void generateMesh(Texture texture) {
        ImageRaster raster = ImageRaster.create(texture.getImage());

        for (int y = 0; y < raster.getHeight(); y++) {
            for (int x = 0; x < raster.getWidth(); x++) {

                if (!isPixelEmpty(raster, x, y) && isPixelEmpty(raster, x-1, y)) {
                    addQuadBack(x, y);
                }

                if (!isPixelEmpty(raster, x, y) && isPixelEmpty(raster, x+1, y)) {
                    addQuadFront(x, y);
                }

                if (!isPixelEmpty(raster, x, y) && isPixelEmpty(raster, x, y-1)) {
                    addQuadBottom(x, y);
                }

                if (!isPixelEmpty(raster, x, y) && isPixelEmpty(raster, x, y+1)) {
                    addQuadTop(x, y);
                }

                if (!isPixelEmpty(raster, x, y)) {
                    addSides(x,y);
                }

            }
        }
    }

    private void addQuadFront(int x, int y) {

        putVertex(0, y, x+1, x, y);
        putVertex(1, y, x+1, x, y);
        putVertex(1, y+1, x+1, x, y);

        putVertex(0, y, x+1, x, y);
        putVertex(1, y+1, x+1, x, y);
        putVertex(0, y+1, x+1, x, y);
    }

    private void addQuadBack(int x, int y) {

        putVertex(1, y+1, x, x, y);
        putVertex(1, y, x, x, y);
        putVertex(0, y, x, x, y);

        putVertex(0, y+1, x, x, y);
        putVertex(1, y+1, x, x, y);
        putVertex(0, y, x, x, y);
    }

    private void addQuadBottom(int x, int y) {

        putVertex(1, y, x, x, y);
        putVertex(0, y, x+1, x, y);
        putVertex(0, y, x, x, y);

        putVertex(1, y, x, x, y);
        putVertex(1, y, x+1, x, y);
        putVertex(0, y, x+1, x, y);
    }

    private void addQuadTop(int x, int y) {
        putVertex(0, y+1, x, x, y);
        putVertex(0, y+1, x+1, x, y);
        putVertex(1, y+1, x, x, y);

        putVertex(0, y+1, x+1, x, y);
        putVertex(1, y+1, x+1, x, y);
        putVertex(1, y+1, x, x, y);
    }

    private void addSides(int x, int y) {
        putVertex(0, y, x, x, y);
        putVertex(0, y, x+1, x+1, y);
        putVertex(0, y+1, x+1, x+1, y+1);

        putVertex(0, y, x, x, y);
        putVertex(0, y+1, x+1, x+1, y+1);
        putVertex(0, y+1, x, x, y+1);

        putVertex(1, y+1, x+1, x, y);
        putVertex(1, y, x+1, x, y);
        putVertex(1, y, x, x, y);

        putVertex(1, y+1, x, x, y);
        putVertex(1, y+1, x+1, x, y);
        putVertex(1, y, x, x, y);
    }

    private void putVertex(int x, int y, int z, float u, float v) {
        vertices.put(x);
        vertices.put(y);
        vertices.put(z);
        uv.put(u*scalingFactor);
        uv.put(v*scalingFactor);
    }

    private boolean isPixelEmpty(ImageRaster raster, int x, int y) {
        if (x < 0 || y < 0 || x >= raster.getWidth() || y >= raster.getHeight())
            return true;

        if (raster.getPixel(x, y).a == 0)
            return true;

        return false;
    }

}
