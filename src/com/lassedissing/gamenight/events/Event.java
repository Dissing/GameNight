/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.events;

import com.lassedissing.gamenight.eventmanagning.EventClosure;


public abstract class Event {

    private static EventClosure listeners[] = new EventClosure[3];


    public String getEventName() {
        return this.getClass().getSimpleName();
    }

    public static EventClosure[] getListeners() {
        return listeners;
    }
}
