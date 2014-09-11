/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client;

import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.lassedissing.gamenight.client.gui.GuiContext;
import com.lassedissing.gamenight.client.gui.WeaponViewElement;
import com.lassedissing.gamenight.messages.ActivateWeaponMessage;
import com.lassedissing.gamenight.world.WeaponInfo;


public class WeaponController {

    WeaponViewElement weaponElement;

    private float rateOfFireTime = 0.2f;
    private boolean automatic = false;

    private float weaponTimer = 0;

    public void setupElement(GuiContext guiContext, Camera cam, RenderManager renderManager) {
        weaponElement = new WeaponViewElement(guiContext,cam, renderManager);
    }

    public void tick(Main app, float tpf) {
        if (weaponTimer > 0) {
            weaponTimer -= tpf;
        }
        if (app.inputProcessor.leftClick() && weaponTimer <= 0) {
            app.client.send(new ActivateWeaponMessage(app.clientId, app.getCamera().getLocation(), app.getCamera().getDirection()));
            weaponTimer = rateOfFireTime;
            if (!automatic) {
                app.inputProcessor.eatLeftClick();
            }
        }
    }

    public void setWeapon(WeaponInfo.Type type) {
        weaponElement.setType(type);
        automatic = WeaponInfo.isAutomatic(type);
        rateOfFireTime = WeaponInfo.getRateOfFire(type);

    }

}
