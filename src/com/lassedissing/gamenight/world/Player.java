/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.world;

import com.jme3.math.Vector3f;


public class Player {

    private int id;
    private Vector3f location;
    private Vector3f width = new Vector3f(0.4f,0.9f,0.4f);
    private float eyeOffset = 0.8f;
    private int health = 10;

    private boolean spawned = false;


    public Player(int id, Vector3f location) {
        this.id = id;
        this.location = location;
    }

    public void spawn(Vector3f location) {
        this.location = location;
        spawned = true;
    }

    public void damage(int damage) {
        health -= damage;

        if (health <= 0) {
            spawned = false;
        }
    }

    public boolean collideWithPoint(Vector3f point) {
        if (point.x > location.x+width.x || point.x < location.x-width.x) return false;
        if (point.y > location.y+width.y-eyeOffset || point.y < location.y-width.y-eyeOffset) return false;
        if (point.z > location.z+width.z || point.z < location.z-width.z) return false;
        return true;
    }

    public int getId() {
        return id;
    }

    public int getHealth() {
        return health;
    }

    public void setLocation(Vector3f location) {
        this.location = location;
    }

    public Vector3f getLocation() {
        return location;
    }

}
