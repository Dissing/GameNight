/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight;

import com.jme3.network.serializing.Serializer;
import com.lassedissing.gamenight.events.BlockChangeEvent;
import com.lassedissing.gamenight.events.FlagEvent;
import com.lassedissing.gamenight.events.InfoSyncEvent;
import com.lassedissing.gamenight.events.entity.EntityDiedEvent;
import com.lassedissing.gamenight.events.entity.EntityMovedEvent;
import com.lassedissing.gamenight.events.entity.EntitySpawnedEvent;
import com.lassedissing.gamenight.events.player.PlayerDiedEvent;
import com.lassedissing.gamenight.events.player.PlayerMovedEvent;
import com.lassedissing.gamenight.events.player.PlayerNewEvent;
import com.lassedissing.gamenight.events.player.PlayerSpawnedEvent;
import com.lassedissing.gamenight.events.player.PlayerStatEvent;
import com.lassedissing.gamenight.events.player.PlayerTeleportEvent;
import com.lassedissing.gamenight.messages.ActivateWeaponMessage;
import com.lassedissing.gamenight.messages.BlockChangeMessage;
import com.lassedissing.gamenight.messages.ChunkMessage;
import com.lassedissing.gamenight.messages.JoinMessage;
import com.lassedissing.gamenight.messages.PlayerMovementMessage;
import com.lassedissing.gamenight.messages.UpdateMessage;
import com.lassedissing.gamenight.messages.WelcomeMessage;
import com.lassedissing.gamenight.world.Bullet;
import com.lassedissing.gamenight.world.Chunk;
import com.lassedissing.gamenight.world.Flag;


public class NetworkRegistrar {

    public static void register() {
        Serializer.registerClass(PlayerMovementMessage.class);
        Serializer.registerClass(PlayerMovedEvent.class);
        Serializer.registerClass(ChunkMessage.class);
        Serializer.registerClass(Chunk.class);
        Serializer.registerClass(BlockChangeMessage.class);
        Serializer.registerClass(ActivateWeaponMessage.class);
        Serializer.registerClass(UpdateMessage.class);
        Serializer.registerClass(WelcomeMessage.class);
        Serializer.registerClass(JoinMessage.class);
        Serializer.registerClass(EntityMovedEvent.class);
        Serializer.registerClass(EntityDiedEvent.class);
        Serializer.registerClass(EntitySpawnedEvent.class);
        Serializer.registerClass(PlayerStatEvent.class);
        Serializer.registerClass(PlayerNewEvent.class);
        Serializer.registerClass(PlayerSpawnedEvent.class);
        Serializer.registerClass(PlayerDiedEvent.class);
        Serializer.registerClass(PlayerTeleportEvent.class);
        Serializer.registerClass(BlockChangeEvent.class);
        Serializer.registerClass(FlagEvent.class);
        Serializer.registerClass(InfoSyncEvent.class);
        Serializer.registerClass(Flag.class);
        Serializer.registerClass(Bullet.class);

    }

}
