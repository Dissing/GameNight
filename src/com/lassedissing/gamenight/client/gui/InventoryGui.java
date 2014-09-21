/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client.gui;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;


public class InventoryGui extends GuiElement {

    private final int quadSize = 64;
    private final int spacing = 3;
    private int[] grid = new int[64];
    private int currentSlot = 0;
    private Geometry[] quads = new Geometry[64];
    private Geometry selectBox;

    private static final int xOffset = 372;
    private static final int yOffset = 150;
    private static final int size = 536;

    private Node node;

    public InventoryGui(GuiContext context) {
        super(context);

        node = new Node();

        setupGrid();

        Material mat = new Material(context.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = context.getAssetManager().loadTexture("Textures/TextureAtlas.png");
        tex.setMagFilter(Texture.MagFilter.Nearest);
        mat.setTexture("ColorMap", tex);

        for (int u = 0; u < 8; u++) {
            for (int v = 0; v < 8; v++) {
                int idx = v*8+u;
                Mesh mesh = new Quad(quadSize, quadSize);
                quads[idx] = new Geometry("GridSlot " + idx,mesh);
                quads[idx].setMaterial(mat);
                quads[idx].setLocalTranslation(quadSize*u+spacing*u, 0, quadSize*v+spacing*v);
                quads[idx].setMaterial(mat);
                if (grid[idx] != 0) {
                    mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, calcTexCoord(grid[idx]));
                } else {
                    quads[idx].setCullHint(Spatial.CullHint.Always);
                }
                node.attachChild(quads[idx]);
            }
        }

        Material selectMat = new Material(context.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
        selectMat.setColor("Color", ColorRGBA.Black);
        selectMat.getAdditionalRenderState().setWireframe(true);
        Mesh selectMesh = new WireBox(quadSize/2, quadSize/2,0.1f);
        selectMesh.setLineWidth(spacing);
        selectBox = new Geometry("SlotSelectBox", selectMesh);
        selectBox.setLocalTranslation(quadSize/2, quadSize/2, 0);
        selectBox.setMaterial(selectMat);

        node.attachChild(selectBox);


        Geometry backplane = new Geometry("InventoryBackplane", new Quad(size,size));
        Material backplaneMat = new Material(context.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
        backplaneMat.setColor("Color", new ColorRGBA(0, 0, 0, 0.5f));
        backplaneMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        backplane.setMaterial(backplaneMat);
        backplane.setLocalTranslation(0, 0, -1);

        node.attachChild(backplane);


        node.setLocalTranslation(xOffset, yOffset,0);
        context.getNode().attachChild(node);
        node.setCullHint(Spatial.CullHint.Always);
    }

    private FloatBuffer calcTexCoord(int id) {
        id--;
        return BufferUtils.createFloatBuffer(new float[]{
            (id % 32) / 32f,(id / 32) / 32f,
            (id+1 % 32) / 32f,(id / 32) / 32f,
            (id+1 % 32) / 32f,(id / 32)+1 / 32f,
            (id % 32) / 32f,(id / 32)+1 / 32f
        });
    }

    @Override
    public void tick(float tpf) {
    }

    @Override
    public void hide(boolean enable) {
        node.setCullHint(enable ? Spatial.CullHint.Always : Spatial.CullHint.Never);
    }

    public void mouseMove(int x, int y) {
        if (xOffset < x && size+xOffset > x && yOffset < y && size+yOffset > y) {
            int u = (x-xOffset)/(quadSize+spacing);
            int v = (y-yOffset)/(quadSize+spacing);
            selectBox.setLocalTranslation(quadSize/2 + u*quadSize + u*spacing, quadSize/2+ v*quadSize + v*spacing, 0);
        }
    }

    public void mouseClick(int x, int y) {

    }

    private void setupGrid() {
        grid[0] = 1;
        grid[1] = 3;
        grid[2] = 4;
    }



}
