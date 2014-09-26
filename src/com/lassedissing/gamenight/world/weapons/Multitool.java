/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.world.weapons;

import com.jme3.math.Vector3f;


public class Multitool extends MeleeWeapon {

    @Override
    public Type getType() {
        return Type.Multitool;
    }

    @Override
    public float getRateOfFireTime() {
        return 0.2f;
    }

    @Override
    public Vector3f getTranslation() {
        return new Vector3f(-0.2f, -0.7f, 1f);
    }

    @Override
    public float getRotationX() {
        return -0.1f;
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

    @Override
    public boolean canDig() {
        return true;
    }

}
