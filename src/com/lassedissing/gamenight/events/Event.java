/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.events;

import com.lassedissing.gamenight.eventmanagning.EventClosure;
import java.util.ArrayList;
import java.util.List;


public class Event {

    private static List<EventClosure> closures = new ArrayList<>();

    public String getEventName() {
        return this.getClass().getSimpleName();
    }

    public List<EventClosure> getClosures() {
        return closures;
    }

}
