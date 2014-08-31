/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.networking.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import com.lassedissing.gamenight.world.Bullet;
import java.util.List;

@Serializable
public class EntityUpdateMessage extends AbstractMessage {

    public Bullet[] bullets;

    public EntityUpdateMessage(List<Bullet> bullets) {
        this.bullets = (Bullet[]) bullets.toArray(new Bullet[bullets.size()]);
    }

    /**
     * Serialization
     */
    public EntityUpdateMessage() {

    }
}
