/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.world;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;

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

    public void tick(World world, float tpf) {
        location.addLocal(velocity.mult(tpf));

        if (world.getBlockAt(location).isBulletCollidable()) {
            dying = true;
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
