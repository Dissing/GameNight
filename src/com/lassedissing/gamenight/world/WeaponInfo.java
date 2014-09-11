/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.world;


public class WeaponInfo {

    public enum Type {
        AK47,
        Multitool,
        Sword
    }

    public static int getMags(Type type) {
        switch(type) {
            case AK47: return 4;
            default: return 0;
        }
    }

    public static boolean usesMags(Type type) {
        switch(type) {
            case AK47: return true;
            default: return false;
        }
    }

    public static boolean usesAmmo(Type type) {
        switch(type) {
            case AK47: return true;
            default: return false;
        }
    }

    public static int getMagCapacity(Type type) {
        switch(type) {
            case AK47: return 20;
            default: return 0;
        }
    }

    public static float getRateOfFire(Type type) {
        switch(type) {
            case AK47: return 0.05f;
            default: return 0;
        }
    }

    public static boolean isAutomatic(Type type) {
        switch(type) {
            case AK47: return true;
            default: return false;
        }
    }

}
