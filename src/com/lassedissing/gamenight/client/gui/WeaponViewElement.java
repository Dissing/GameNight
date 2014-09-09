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
import com.jme3.scene.Spatial;
import com.lassedissing.gamenight.client.WeaponView;


public class WeaponViewElement extends GuiElement {

    private Geometry weaponGeo;

    public WeaponViewElement(GuiContext context, Camera cam, RenderManager renderManager) {
        super(context);

        Camera weaponCam = cam.clone();
        weaponCam.setLocation(new Vector3f(0, 0, 0));
        weaponCam.lookAt(new Vector3f(0, 0, 1), Vector3f.UNIT_Y);
        weaponCam.setLocation(new Vector3f(0, 0, 0));
        ViewPort weaponView = renderManager.createMainView("weapon view", weaponCam);
        weaponView.setClearFlags(false, true, false);

        WeaponView ak47 = new WeaponView("AK47", context.getAssetManager());

        weaponGeo = new Geometry("Weapon",ak47.mesh);
        weaponView.attachScene(weaponGeo);
        weaponView.setEnabled(true);

        weaponGeo.setLocalTranslation(-0.2f, -0.7f, 1f);
        weaponGeo.rotate(-0.2f, -2.8f, 0);
        weaponGeo.setMaterial(ak47.weaponMaterial);
        weaponGeo.scale(0.03f);

        weaponGeo.updateGeometricState();
    }

    @Override
    public void tick(float tpf) {
        //weaponGeo.updateGeometricState();
    }

    @Override
    public void hide(boolean enable) {
        weaponGeo.setCullHint(enable ? Spatial.CullHint.Always : Spatial.CullHint.Never);
    }



}
