/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.eventmanagning;

import com.lassedissing.gamenight.events.Event;
import java.lang.reflect.*;
import java.util.List;

public class EventManager {

    public void sendEvent(Event event) {

        int level = event.getClosureLevel();

        for (int i = level; i > 0; i--) {

            for (EventClosure closure : event.getClosures(i)) {
                if (closure != null) {
                    closure.fireEvent(event);
                }
            }
        }
    }

    public void registerListener(final EventListener listener) {
        for (final Method method : listener.getClass().getMethods()) {
            if (method.isAnnotationPresent(EventHandler.class)) {
                Class[] parameters = method.getParameterTypes();
                if (parameters.length == 1 && Event.class.isAssignableFrom(parameters[0])) {

                    Class<? extends Event> type = parameters[0];

                    List<EventClosure> list = null;
                    try {
                        Event instance = (Event) type.newInstance();
                        list = (List<EventClosure>) type.getDeclaredMethod("getClosures", Integer.TYPE).invoke(instance,instance.getClosureLevel());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    list.add( new EventClosure() {

                        @Override
                        public void fireEvent(Event event) {
                            try {
                                method.invoke(listener, event);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            }
        }

    }


}
