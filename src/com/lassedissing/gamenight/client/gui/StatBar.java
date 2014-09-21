/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client.gui;

import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import com.lassedissing.gamenight.world.weapons.RangedWeapon;
import com.lassedissing.gamenight.world.weapons.Weapon;
import java.nio.FloatBuffer;


public class StatBar extends GuiElement {

    private Node node;

    private Geometry healthBar;
    private Mesh healthBarMesh;
    private BitmapText healthText;

    private Geometry ammoBar;
    private Mesh ammoBarMesh;
    private BitmapText ammoText;

    private int health;
    private int initialHealth;

    public StatBar(GuiContext context, int initialHealth) {
        super(context);

        this.initialHealth = initialHealth;

        Material mat = new Material(context.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = context.getAssetManager().loadTexture("Textures/UI.png");
        tex.setMagFilter(Texture.MagFilter.Nearest);
        mat.setTexture("ColorMap", tex);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

        healthBarMesh = new Quad(256,45);
        healthBarMesh.setBuffer(VertexBuffer.Type.TexCoord, 2, calcTexCoord(32,0,32,5));
        healthBar = new Geometry("HealthBar", healthBarMesh);
        healthBar.setMaterial(mat);

        Mesh healthBackplateMesh = new Quad(256,45);
        Geometry healthBackplate = new Geometry("HealthBackplate", healthBackplateMesh);
        healthBackplateMesh.setBuffer(VertexBuffer.Type.TexCoord, 2, calcTexCoord(0,0,32,5));
        healthBackplate.setMaterial(mat);
        healthBackplateMesh.setDynamic();

        healthText = new BitmapText(context.getFont(),false);
        healthText.setSize(context.getFont().getCharSet().getRenderedSize()*1.5f);
        healthText.setColor(ColorRGBA.White);
        healthText.setLocalTranslation(105,39,0);


        ammoBarMesh = new Quad(256,45);
        ammoBarMesh.setBuffer(VertexBuffer.Type.TexCoord, 2, calcTexCoord(64,0,32,5));
        ammoBar = new Geometry("AmmoBar", ammoBarMesh);
        ammoBar.setLocalTranslation(0, 52, 0);
        ammoBar.setMaterial(mat);

        Mesh ammoBackplateMesh = new Quad(256,45);
        Geometry ammoBackplate = new Geometry("AmmoBackplate", ammoBackplateMesh);
        ammoBackplateMesh.setBuffer(VertexBuffer.Type.TexCoord, 2, calcTexCoord(0,0,32,5));
        ammoBackplate.setMaterial(mat);
        ammoBackplate.setLocalTranslation(0, 52, 0);
        ammoBarMesh.setDynamic();

        ammoText = new BitmapText(context.getFont(),false);
        ammoText.setSize(context.getFont().getCharSet().getRenderedSize()*1.5f);
        ammoText.setColor(ColorRGBA.Black);
        ammoText.setLocalTranslation(80, 91, 0);

        node = new Node();
        node.attachChild(healthBar);
        node.attachChild(healthText);
        node.attachChild(healthBackplate);
        node.attachChild(ammoBar);
        node.attachChild(ammoText);
        node.attachChild(ammoBackplate);
        node.setLocalTranslation(950,30,0);

        context.getNode().attachChild(node);
    }

    @Override
    public void tick(float tpf) {

    }

    public void setHealth(int health) {
        this.health = health;
        healthText.setText(Integer.toString(health));
        healthBar.scale(health/10f);
        float cutoff = health/initialHealth;
        healthBar.setLocalScale(cutoff, 1f, 1f);
        healthBarMesh.setBuffer(VertexBuffer.Type.TexCoord, 2, calcTexCoord(32,0,(int)(32*cutoff),5));
    }

    public void updateWeapon(Weapon weapon) {
        if (weapon instanceof RangedWeapon) {
            RangedWeapon ranged = (RangedWeapon) weapon;
            ammoText.setText(ranged.getRoundsInMag() + "/" + ranged.getMaxRoundsInMag() + "-" + ranged.getMags());
            float cutoff = ((float)ranged.getRoundsInMag())/ranged.getMaxRoundsInMag();
            ammoBar.setLocalScale(cutoff, 1f, 1f);
            ammoBarMesh.setBuffer(VertexBuffer.Type.TexCoord, 2, calcTexCoord(64,0,(int)(32*cutoff),5));
        }
    }

    @Override
    public void hide(boolean enable) {
        node.setCullHint(enable ? Spatial.CullHint.Always : Spatial.CullHint.Never);
    }

    private FloatBuffer calcTexCoord(int x, int y, int width, int height) {
        return BufferUtils.createFloatBuffer(new float[]{
            x/128f,y/128f,
            (x+width)/128f,y/128f,
            (x+width)/128f,(y+height)/128f,
            x/128f,(y+height)/128f
        });
    }
}
