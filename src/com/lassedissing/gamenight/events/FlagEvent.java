/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.events;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;
import com.lassedissing.gamenight.eventmanagning.EventClosure;
import com.lassedissing.gamenight.world.Entity;
import java.util.ArrayList;
import java.util.List;


@Serializable
public class FlagEvent extends Event {

    private int flagId;
    private int playerId;
    private boolean reset;
    private boolean isMove;
    private Vector3f newLocation = new Vector3f();

    public FlagEvent(int playerId, int flagId) {
        this.playerId = playerId;
        this.flagId = flagId;
    }

    public FlagEvent(int flagId, boolean reset) {
        this.reset = reset;
    }

    public FlagEvent(int flagId, Vector3f newLocation) {
        this.newLocation.set(newLocation);
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public int getFlagId() {
        return this.flagId;
    }

    public boolean isReset() {
        return reset;
    }

    public boolean isMove() {
        return isMove;
    }

    public Vector3f getLocation() {
        return newLocation;
    }

    /**
     * Serialization
     */
    public FlagEvent() {

    }

    //ClosureHolder section

    private static List<EventClosure> closures = new ArrayList<>();

    @Override
    public int getClosureLevel() {
        return 1;
    }

    @Override
    public List<EventClosure> getClosures(int level) {
        switch (level) {
            case 0:     return super.getClosures(level);
            case 1:     return closures;
            default:    throw new UnsupportedOperationException("Level " + level + " is not supported by " + getEventName());
        }
    }

}

