/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.world;

import com.jme3.math.Vector3f;
import com.lassedissing.gamenight.eventmanagning.EventManager;
import com.lassedissing.gamenight.events.player.PlayerDiedEvent;
import com.lassedissing.gamenight.events.player.PlayerStatEvent;


public class Player {

    private int id;
    private String name;
    private Vector3f location;
    private Vector3f width = new Vector3f(0.4f,0.9f,0.4f);
    private float eyeOffset = 0.8f;
    private int health = 10;
    private int team;
    private int hasFlag = -1;


    public Player(int id, String name, int team, Vector3f location) {
        this.id = id;
        this.location = location;
        this.team = team;
        this.name = name;
    }

    public void spawn(Vector3f location) {
        this.location = location;
        health = 10;
        EventManager.sendEvent(new PlayerStatEvent(id, health));
    }

    public void damage(int damage) {
        health -= damage;
        EventManager.sendEvent(new PlayerStatEvent(id, health));

        if (health <= 0) {
            EventManager.sendEvent(new PlayerDiedEvent(id));
        }
    }

    public boolean collideWithPoint(Vector3f point) {
        if (point.x > location.x+width.x || point.x < location.x-width.x) return false;
        if (point.y > location.y+width.y-eyeOffset || point.y < location.y-width.y-eyeOffset) return false;
        if (point.z > location.z+width.z || point.z < location.z-width.z) return false;
        return true;
    }

    public void testCollideWithBullet(Bullet bullet) {
        if (id != bullet.getOwnerId() && collideWithPoint(bullet.getLocation())) {
            bullet.kill();
            damage(1);
        }
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

    public int getTeam() {
        return team;
    }

    public String getName() {
        return name;
    }

    public void setHasFlag(Flag flag, boolean pickedUp) {
            flag.setIsPickedUp(pickedUp);
            hasFlag = (pickedUp ? flag.getTeam() : -1);

    }

    public boolean hasFlag() {
        return hasFlag != -1;
    }

    public int getPickedUpFlagTeam() {
        return hasFlag;
    }

}
