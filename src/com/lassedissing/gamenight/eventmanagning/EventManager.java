/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.eventmanagning;

import com.lassedissing.gamenight.events.Event;
import java.lang.reflect.*;

public class EventManager {

    public void sendEvent(Event event) {
        for (EventClosure closure : Event.getListeners()) {
            if (closure != null) {
                closure.fireEvent(event);
            }
        }
    }

    public void registerListener(final EventListener listener) {
        for (final Method method : listener.getClass().getMethods()) {
            if (method.isAnnotationPresent(EventHandler.class)) {
                Class[] event = method.getParameterTypes();
                if (event.length == 1 && Event.class.isAssignableFrom(event[0])) {

                    Event.getListeners()[0] = new EventClosure() {

                        @Override
                        public void fireEvent(Event event) {
                            try {
                                method.invoke(listener, event);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                }
            }
        }

    }

}
