/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.world;

import com.jme3.math.Vector3f;
import com.lassedissing.gamenight.eventmanagning.EventManager;
import com.lassedissing.gamenight.events.entity.EntityDiedEvent;
import com.lassedissing.gamenight.events.entity.EntityMovedEvent;

public class Bullet {

    private Vector3f location;
    private Vector3f velocity;
    private float speed;
    private boolean dying;
    private int id;
    private int ownerId;


    public Bullet(int id, int ownerId, Vector3f source, Vector3f direction, float speed) {
        location = source;
        velocity = direction.mult(speed);
        dying = false;
        this.id = id;
        this.ownerId = ownerId;
    }

    public void tick(World world, EventManager eventManager, float tpf) {
        location.addLocal(velocity.mult(tpf));
        eventManager.sendEvent(new EntityMovedEvent(id, location));

        if (location.x < 0 || location.x > world.getBlockWidth() ||
                location.y < 0 || location.y > world.getBlockHeight() ||
                location.z < 0 || location.z > world.getBlockLength()) {
            kill(eventManager);
            return;
        }

        if (world.getBlockAt(location).isBulletCollidable()) {
            kill(eventManager);
        }
    }

    public void kill(EventManager eventManager) {
        dying = true;
        eventManager.sendEvent(new EntityDiedEvent(id));
    }

    public boolean isDying() {
        return dying;
    }

    public int getId() {
        return id;
    }

    public int getOwnerId() {
        return ownerId;
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
