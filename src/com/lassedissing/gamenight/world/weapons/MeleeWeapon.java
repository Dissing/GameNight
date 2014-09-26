/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.world.weapons;

import com.lassedissing.gamenight.client.Main;


public abstract class MeleeWeapon implements Weapon {

    @Override
    public void fireEvent(Main app) {
    }

    @Override
    public void reloadEvent() {
    }

    @Override
    public void tick(float tpf) {
        
    }



}
