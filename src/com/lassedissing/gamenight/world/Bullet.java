/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.world;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;
import com.lassedissing.gamenight.eventmanagning.EventManager;
import com.lassedissing.gamenight.events.entity.EntityDiedEvent;
import com.lassedissing.gamenight.events.entity.EntityMovedEvent;

@Serializable
public class Bullet extends Entity {

    private Vector3f velocity;
    private float speed;
    private boolean dying;
    private int ownerId;


    public Bullet(int id, int ownerId, Vector3f source, Vector3f direction, float speed) {
        super(id,source);
        velocity = direction.mult(speed);
        dying = false;
        this.ownerId = ownerId;
        this.speed = speed;
    }

    public void tick(World world, float tpf) {
        location.addLocal(velocity.mult(tpf));
        EventManager.sendEvent(new EntityMovedEvent(this));

        if (location.x < 0 || location.x > world.getBlockWidth() ||
                location.y < 0 || location.y > world.getBlockHeight() ||
                location.z < 0 || location.z > world.getBlockLength()) {
            kill();
            return;
        }

        if (world.getBlockAt(location).isBulletCollidable()) {
            kill();
        }
    }

    public void kill() {
        dying = true;
        EventManager.sendEvent(new EntityDiedEvent(this));
    }

    public boolean isDying() {
        return dying;
    }

    public int getOwnerId() {
        return ownerId;
    }

    @Override
    public EntityType getType() {
        return EntityType.Bullet;
    }

    /**
     * Serialization
     */
    public Bullet() {

    }

}
