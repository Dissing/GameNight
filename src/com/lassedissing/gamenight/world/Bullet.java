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
public class Bullet {

    private Vector3f location;
    private Vector3f velocity;
    private float speed;
    private boolean dying;
    private int id;


    public Bullet(int id, Vector3f source, Vector3f direction, float speed) {
        location = source;
        velocity = direction.mult(speed);
        dying = false;
        this.id = id;
    }

    public void tick(World world, EventManager eventManager, float tpf) {
        location.addLocal(velocity.mult(tpf));
        eventManager.sendEvent(new EntityMovedEvent(id, location));

        if (location.x < 0 || location.x > world.getWidth() ||
                location.y < 0 || location.y > world.getHeight() ||
                location.z < 0 || location.z > world.getLength()) {

            dying = true;
            eventManager.sendEvent(new EntityDiedEvent(id));
            return;
        }

        if (world.getBlockAt(location).isBulletCollidable()) {
            dying = true;
            eventManager.sendEvent(new EntityDiedEvent(id));
        }
    }

    public boolean isDying() {
        return dying;
    }

    public int getId() {
        return id;
    }

    public Vector3f getLocation() {
        return location;
    }

    /**
     * Serialization
     */
    public Bullet() {

    }

}
