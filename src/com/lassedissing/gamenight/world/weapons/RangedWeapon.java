/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.world.weapons;

import com.lassedissing.gamenight.client.Main;
import com.lassedissing.gamenight.messages.ActivateWeaponMessage;


public abstract class RangedWeapon implements Weapon {

    private int mags;
    private int rounds;
    private boolean isAutomatic;
    private float timer;

    public abstract int getMaxMags();

    public abstract int getMaxRoundsInMag();

    public abstract float getReloadTime();

    public abstract float getBulletSpeed();

    public abstract boolean getDefaultMode();


    public RangedWeapon() {
        mags = getMaxMags();
        rounds = getMaxRoundsInMag();
        isAutomatic = getDefaultMode();
    }


    @Override
    public void fireEvent(Main app) {
        if (timer <= 0) {
            if (getMaxRoundsInMag() != -1) {
                if (rounds > 0) {
                    app.getClient().send(new ActivateWeaponMessage(app.getClientId(), app.getCamera().getLocation(), app.getCamera().getDirection(),getBulletSpeed()));
                    rounds--;
                    timer = getRateOfFireTime();
                } else {
                    reloadEvent();
                }
            } else {

            }
            if (!isAutomatic) {
                app.getInputProcessor().eatLeftClick();
            }
        }
    }

    @Override
    public void reloadEvent() {
        if (mags > 0) {
            timer = getReloadTime();
            if (getMaxMags() != -1) {
                mags--;
                rounds = getMaxRoundsInMag();
            }
        } else {
            // Play 'empty mag' sound
        }
    }

    @Override
    public void tick(float tpf) {
        if (timer > 0) {
            timer -= tpf;
        }
    }

    public int getMags() {
        return mags;
    }

    public int getRoundsInMag() {
        return rounds;
    }

    public boolean isAutomatic() {
        return isAutomatic;
    }

    @Override
    public boolean canDig() {
        return false;
    }
}
