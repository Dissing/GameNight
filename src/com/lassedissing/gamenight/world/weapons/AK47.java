/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.world.weapons;

public class AK47 extends RangedWeapon {


    private static int maxMags = 2;

    private static int maxRounds = 10;

    private static float reloadTime = 2.0f;
    private static float rateOfFireTime = 0.2f;
    private static float bulletSpeed = 30f;

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

}
