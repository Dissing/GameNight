/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import com.lassedissing.gamenight.events.Event;
import java.util.List;

@Serializable
public class UpdateMessage extends AbstractMessage {

    public Event[] events;

    public UpdateMessage(List<Event> events) {
        this.events = (Event[]) events.toArray(new Event[events.size()]);
    }

    /**
     * Serialization
     */
    public UpdateMessage() {

    }
}
