/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client.gui;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.lassedissing.gamenight.client.WeaponView;
import com.lassedissing.gamenight.world.weapons.Weapon.Type;
import java.util.HashMap;
import java.util.Map;


public class WeaponViewElement extends GuiElement {

    private Geometry weaponGeo;
    private ViewPort weaponView;

    private Map<Type, WeaponView> weaponMesh = new HashMap<>();

    public WeaponViewElement(GuiContext context, Camera cam, RenderManager renderManager) {
        super(context);

        Camera weaponCam = cam.clone();
        weaponCam.setLocation(new Vector3f(0, 0, 0));
        weaponCam.lookAt(new Vector3f(0, 0, 1), Vector3f.UNIT_Y);
        weaponCam.setLocation(new Vector3f(0, 0, 0));
        weaponView = renderManager.createMainView("weapon view", weaponCam);
        weaponView.setClearFlags(false, true, false);
        weaponView.setEnabled(false);

        for (Type type : Type.values()) {
            weaponMesh.put(type, new WeaponView(type.toString(), context.getAssetManager()));
        }

        weaponGeo = new Geometry("Weapon");
        weaponView.attachScene(weaponGeo);

        weaponGeo.setLocalTranslation(-0.2f, -0.7f, 1f);
        weaponGeo.rotate(-0.2f, -2.8f, 0);
        weaponGeo.scale(0.03f);

    }

    @Override
    public void tick(float tpf) {
        //weaponGeo.updateGeometricState();
    }

    @Override
    public void hide(boolean enable) {
        weaponView.setEnabled(false);
    }

    public void setType(Type type) {
        WeaponView view = weaponMesh.get(type);
        weaponGeo.setMesh(view.mesh);
        weaponGeo.setMaterial(view.weaponMaterial);
        weaponGeo.updateGeometricState();
        weaponView.setEnabled(true);

    }



}
