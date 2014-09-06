/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.game;

import com.lassedissing.gamenight.eventmanagning.EventManager;
import com.lassedissing.gamenight.events.Event;
import com.lassedissing.gamenight.world.Bullet;
import com.lassedissing.gamenight.world.Player;
import com.lassedissing.gamenight.world.World;
import java.util.Collection;


public interface GameContainer {

    public EventManager getEventManager();

    public World getWorld();

    public Collection<Player> getPlayers();

    public Player getPlayer(int id);

    public Collection<Bullet> getBullets();

    public void spawnPlayer(int id);

}
