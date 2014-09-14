/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.world.weapons;

import com.lassedissing.gamenight.client.Main;


public interface Weapon {

    public enum Type {
        AK47,
        Sword,
        Multitool
    }

    public Type getType();

    public float getRateOfFireTime();

    public void fireEvent(Main app);

    public void reloadEvent();

    public void tick(float tpf);

}
