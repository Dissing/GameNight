/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client.gui;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;


public class BuildBar extends GuiElement {

    private int quadSize = 64;
    private int spacing = 3;
    private int[] slots = new int[8];
    private Geometry[] quads = new Geometry[8];
    private Geometry selectBox;

    private Node node;

    public BuildBar(GuiContext context) {
        super(context);
        slots[0] = 1;
        slots[1] = 3;
        slots[2] = 4;

        node = new Node();

        Material mat = new Material(context.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = context.getAssetManager().loadTexture("Textures/TextureAtlas.png");
        tex.setMagFilter(Texture.MagFilter.Nearest);
        mat.setTexture("ColorMap", tex);

        for (int i = 0; i < quads.length; i++) {
            Mesh mesh = new Quad(quadSize, quadSize);
            quads[i] = new Geometry("Slot " + i,mesh);
            quads[i].setMaterial(mat);
            quads[i].setLocalTranslation(quadSize*i+spacing*i, 0, 0);
            quads[i].setMaterial(mat);
            if (slots[i] != 0) {
                mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, calcTexCoord(slots[i]));
            } else {
                quads[i].setCullHint(Spatial.CullHint.Always);
            }
            node.attachChild(quads[i]);
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

        node.setLocalTranslation(context.getWidth()/2 - quadSize*slots.length/2, 50, 0);
        context.getNode().attachChild(node);

    }

    public FloatBuffer calcTexCoord(int id) {
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
        return;
    }

    public void selectSlot(int slot) {
        selectBox.setLocalTranslation(quadSize/2 + slot*quadSize+slot*spacing, quadSize/2, 0);
    }

}
