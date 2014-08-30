/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.networking.events;

import com.jme3.math.Vector3f;


public class PlayerMovedEvent {

        public int playerId;
        public Vector3f position;
        public Vector3f rotation;

        public PlayerMovedEvent(int playerId, Vector3f position, Vector3f lookingAt) {
            this.playerId = playerId;
            this.position = position;
            this.rotation = rotation;
        }
}
