/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.world;

import com.lassedissing.gamenight.world.weapons.AK47;
import com.lassedissing.gamenight.world.weapons.Pistol;
import com.lassedissing.gamenight.world.weapons.Shotgun;
import com.lassedissing.gamenight.world.weapons.Weapon;
import java.security.InvalidParameterException;


public class ClassInfo {

    public enum Type {
        Light,
        Heavy,
        Engineer,
        Support
    }

    public static Weapon[] getWeapons(Type type) {
        switch (type) {
            case Light: return new Weapon[]{new Pistol(), new AK47()};
            case Heavy: return new Weapon[]{new Pistol(), new Shotgun()};
            case Engineer: return new Weapon[]{new Pistol()};
            case Support: return new Weapon[]{new Pistol()};
            default: throw new InvalidParameterException("Unknown class");
        }
    }

    public static Weapon.Type getDefault(Type type) {
        switch (type) {
            case Light: return Weapon.Type.AK47;
            case Heavy: return Weapon.Type.Shotgun;
            case Engineer: return Weapon.Type.Pistol;
            case Support: return Weapon.Type.Pistol;
            default: throw new InvalidParameterException("Unknown class");
        }
    }

}
