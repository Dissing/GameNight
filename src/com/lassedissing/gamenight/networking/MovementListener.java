/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.networking;

import com.lassedissing.gamenight.Log;
import com.lassedissing.gamenight.eventmanagning.EventHandler;
import com.lassedissing.gamenight.eventmanagning.EventListener;
import com.lassedissing.gamenight.events.PlayerMovedEvent;


public class MovementListener implements EventListener {

    @EventHandler
    public void onMovement(PlayerMovedEvent event) {
        Log.DEBUG("Player %s moved!", event.playerId);
    }
}
