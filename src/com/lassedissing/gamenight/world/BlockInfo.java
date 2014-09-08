/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.world;


public class BlockInfo {

    public static float getDuration(int i, float factor) {
        switch(i) {
            case(1):    return 1f*factor; //Stone
            case(2):    return 1000f*factor; //Wall
            case(3):    return 0.5f*factor; //Dirt
            case(4):    return 0.5f*factor; //Grass
            default:    return 1f*factor;
        }
    }

}
