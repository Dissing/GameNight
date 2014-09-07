/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.game;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class DelayedActionManager {

    private List<DelayedAction> actions = new LinkedList<>();

    public void add(DelayedAction action, float seconds) {
        action.timeRemaining = seconds;
        actions.add(action);
    }

    public void tick(float tpf) {
        Iterator<DelayedAction> iterator = actions.iterator();
        while (iterator.hasNext()) {
            DelayedAction action = iterator.next();
            action.timeRemaining -= tpf;
            if (action.timeRemaining < 0) {
                action.execute();
                iterator.remove();
            }
        }
    }

}
