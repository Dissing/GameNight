/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client;

import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.lassedissing.gamenight.client.gui.GuiContext;
import com.lassedissing.gamenight.client.gui.StatBar;
import com.lassedissing.gamenight.client.gui.WeaponViewElement;
import com.lassedissing.gamenight.messages.ActivateWeaponMessage;
import com.lassedissing.gamenight.world.weapons.Weapon;
import java.util.HashMap;
import java.util.Map;


public class WeaponController {

    private Map<Weapon.Type,Weapon> weapons = new HashMap<>();
    private Weapon currentWeapon;

    private WeaponViewElement weaponElement;

    private Main app;


    public void setupElement(GuiContext guiContext, Camera cam, RenderManager renderManager) {
        weaponElement = new WeaponViewElement(guiContext,cam, renderManager);
        app = guiContext.getMain();
    }

    public void tick(float tpf) {
        currentWeapon.tick(tpf);
        if (app.getInputProcessor().leftClick()) {
            currentWeapon.fireEvent(app);
            app.getStatBar().updateWeapon(currentWeapon);
        }
        weaponElement.setIsMoving(app.getPlayer().hasMoved());
        weaponElement.tick(tpf);
    }

    public void setWeapon(Weapon.Type type) {
        currentWeapon = weapons.get(type);
        weaponElement.setType(currentWeapon);
        app.getStatBar().updateWeapon(currentWeapon);
    }

    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }

    public void addWeapon(Weapon weapon) {
        weapons.put(weapon.getType(), weapon);
    }

    void clearWeapons() {
        weapons.clear();
    }

}
