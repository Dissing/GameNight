/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.lassedissing.gamenight.world.weapons.Weapon;


public class PlayerView {

    private int id;
    private Node node = new Node();
    private Geometry weaponGeo;
    private boolean visible = false;

    public PlayerView(int id, Node parent, Main app) {
        this.id = id;

        Box headBox = new Box(0.19f,0.19f,0.19f);
        Box torsoBox = new Box(0.29f,0.38f,0.19f);
        Box armBox = new Box(0.1f,0.28f,0.1f);
        Box legBox = new Box(0.1f,0.33f,0.1f);

        Geometry headGeo = new Geometry("Player" + id + "head", headBox);
        Geometry torsoGeo = new Geometry("Player" + id + "torso", torsoBox);
        Geometry arm1Geo = new Geometry("Player" + id + "arm1", armBox);
        Geometry arm2Geo = new Geometry("Player" + id + "arm2", armBox);
        Geometry leg1Geo = new Geometry("Player" + id + "leg1", legBox);
        Geometry leg2Geo = new Geometry("Player" + id + "leg2", legBox);
        WeaponView weaponView = app.getWeaponMeshCache().get(Weapon.Type.AK47);
        weaponGeo = new Geometry("Player" + id + "weapon", weaponView.mesh);
        weaponGeo.setMaterial(weaponView.weaponMaterial);

        Material headMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        Material torsoMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        Material armMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        Material legMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");

        headMat.setColor("Color", new ColorRGBA(0.827f, 0.525f, 0.278f, 1.0f));
        torsoMat.setColor("Color", new ColorRGBA(0.169f, 0.494f, 0.494f, 1.0f));
        armMat.setColor("Color", new ColorRGBA(0.827f, 0.278f, 0.278f, 1.0f));
        legMat.setColor("Color", new ColorRGBA(0.224f, 0.663f, 0.224f, 1.0f));

        node.attachChild(headGeo);
        headGeo.setLocalTranslation(0, headBox.yExtent+(torsoBox.yExtent + legBox.yExtent)*2, 0);
        headGeo.setMaterial(headMat);

        node.attachChild(torsoGeo);
        torsoGeo.setLocalTranslation(0, torsoBox.yExtent + legBox.yExtent*2, 0);
        torsoGeo.setMaterial(torsoMat);

        node.attachChild(arm1Geo);
        arm1Geo.setLocalTranslation(-0.4f, 0.1f + armBox.yExtent + legBox.yExtent*2, 0);
        arm1Geo.setMaterial(armMat);

        node.attachChild(arm2Geo);
        arm2Geo.setLocalTranslation(0.4f, 0.1f + armBox.yExtent + legBox.yExtent*2, 0);
        arm2Geo.setMaterial(armMat);

        node.attachChild(leg1Geo);
        leg1Geo.setLocalTranslation(-0.15f, legBox.yExtent, 0);
        leg1Geo.setMaterial(legMat);

        node.attachChild(leg2Geo);
        leg2Geo.setLocalTranslation(0.15f, legBox.yExtent, 0);
        leg2Geo.setMaterial(legMat);

        node.attachChild(weaponGeo);
        weaponGeo.setLocalTranslation(0.3f, 0.8f, 0);
        weaponGeo.rotate(0, 3.14f, 0);
        weaponGeo.scale(0.03f);


        parent.attachChild(node);
    }

    public void setPosition(Vector3f pos) {
        node.setLocalTranslation(pos.subtractLocal(0, 1.6f, 0));
    }

    public Vector3f getPosition() {
        return node.getLocalTranslation();
    }

    public void setRotation(Vector3f rot) {
        Vector3f nodeRot = new Vector3f(rot);
        nodeRot.setX(0);
        Vector3f weaponRot = new Vector3f(rot);
        weaponRot.setY(0);
        node.lookAt(nodeRot.add(getPosition()), Vector3f.UNIT_Y);
        weaponGeo.lookAt(weaponRot.add(getPosition()), Vector3f.UNIT_Y);

    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (visible) {
            node.setCullHint(Spatial.CullHint.Dynamic);
        } else {
            node.setCullHint(Spatial.CullHint.Never);
        }
    }

}
