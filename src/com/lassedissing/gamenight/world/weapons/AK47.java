/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.world.weapons;

import com.jme3.math.Vector3f;

public class AK47 extends RangedWeapon {


    private static int maxMags = 3;

    private static int maxRounds = 30;

    private static float reloadTime = 2.0f;
    private static float rateOfFireTime = 0.1f;
    private static float bulletSpeed = 60f;

    @Override
    public boolean getDefaultMode() {
        return true;
    }

    @Override
    public int getMaxMags() {
        return maxMags;
    }

    @Override
    public int getMaxRoundsInMag() {
        return maxRounds;
    }

    @Override
    public float getReloadTime() {
        return reloadTime;
    }

    @Override
    public Type getType() {
        return Weapon.Type.AK47;
    }

    @Override
    public float getRateOfFireTime() {
        return rateOfFireTime;
    }

    @Override
    public float getBulletSpeed() {
        return bulletSpeed;
    }

    @Override
    public Vector3f getTranslation() {
        return new Vector3f(-0.2f, -0.7f, 1f);
    }

    @Override
    public float getRotationX() {
        return -0.5f;
    }

    @Override
    public float getRotationY() {
        return -2.8f;
    }

    @Override
    public float getRotationZ() {
        return 0f;
    }

    @Override
    public float getScale() {
        return 0.03f;
    }



}
