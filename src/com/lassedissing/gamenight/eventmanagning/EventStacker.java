/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.eventmanagning;

import com.lassedissing.gamenight.events.player.PlayerEvent;
import com.lassedissing.gamenight.events.*;
import com.lassedissing.gamenight.events.entity.*;
import com.lassedissing.gamenight.messages.UpdateMessage;
import java.util.ArrayList;
import java.util.List;


public class EventStacker implements EventListener {


    private List<Event> events = new ArrayList<>();

    @EventHandler
    public void onEntityEvent(EntityEvent event) {
        events.add(event);
    }

    @EventHandler
    public void onPlayerEvent(PlayerEvent event) {
        events.add(event);
    }

    @EventHandler
    public void onFlagEvent(FlagEvent event) {
        events.add(event);
    }

    @EventHandler
    public void onInfoSyncEvent(InfoSyncEvent event) {
        events.add(event);
    }

    @EventHandler
    public void onBlockChange(BlockChangeEvent event) {
        events.add(event);
    }

    public UpdateMessage bakeUpdateMessages() {

        if (events.size() < 1000) {
            UpdateMessage msg = new UpdateMessage(events);
            events.clear();
            return msg;
        } else {
            UpdateMessage msg = new UpdateMessage(events.subList(0, 1000));
            events = events.subList(1000, events.size());
            return msg;
        }


    }
}
