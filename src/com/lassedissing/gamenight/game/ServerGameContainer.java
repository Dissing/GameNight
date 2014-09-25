/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.game;

import com.jme3.math.Vector3f;
import com.jme3.network.Message;
import com.lassedissing.gamenight.Log;
import com.lassedissing.gamenight.eventmanagning.EventHandler;
import com.lassedissing.gamenight.eventmanagning.EventListener;
import com.lassedissing.gamenight.eventmanagning.EventManager;
import com.lassedissing.gamenight.events.BlockChangeEvent;
import com.lassedissing.gamenight.events.entity.EntitySpawnedEvent;
import com.lassedissing.gamenight.events.FlagEvent;
import com.lassedissing.gamenight.events.InfoSyncEvent;
import com.lassedissing.gamenight.events.player.PlayerDiedEvent;
import com.lassedissing.gamenight.events.player.PlayerMovedEvent;
import com.lassedissing.gamenight.events.player.PlayerNewEvent;
import com.lassedissing.gamenight.events.player.PlayerSpawnedEvent;
import com.lassedissing.gamenight.events.player.PlayerTeleportEvent;
import com.lassedissing.gamenight.messages.ActivateWeaponMessage;
import com.lassedissing.gamenight.messages.BlockChangeMessage;
import com.lassedissing.gamenight.messages.ChatMessage;
import com.lassedissing.gamenight.messages.PlayerMovementMessage;
import com.lassedissing.gamenight.world.Bullet;
import com.lassedissing.gamenight.world.Flag;
import com.lassedissing.gamenight.world.Player;
import com.lassedissing.gamenight.world.World;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;


public class ServerGameContainer implements GameContainer, EventListener {

    private World world;
    private EventManager eventManager;
    private DelayedActionManager delayedActionManager;

    private Map<Integer,Player> players = new HashMap<>();
    private List<Bullet> bullets = new LinkedList<>();
    private Map<Integer,Flag> flags = new HashMap<>();

    private int nextEntityId = 0;

    private int buildTime = 30;

    public void init() {

        eventManager = new EventManager();
        delayedActionManager = new DelayedActionManager();

        eventManager.registerListener(this);

        Log.INFO("Loading world..");
        world = new World("Test",2);
        world.generate(2, 6);

    }

    public void destroy() {

    }

    private boolean alternateTeam;

    public void playerConnected(int id, String name) {
        players.put(id, new Player(id, name, (alternateTeam ? 0 : 1), Vector3f.ZERO));
        alternateTeam = !alternateTeam;
        EventManager.sendEvent(new PlayerNewEvent(id));
        EventManager.sendEvent(new PlayerTeleportEvent(id,world.getSpectate(players.get(id).getTeam())));
    }

    public void playerDisconnected(int id) {
        players.remove(id);
    }

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

    @Override
    public World getWorld() {
        return world;
    }

    public void replaceWorld(World world) {
        this.world = world;
    }

    @Override
    public Collection<Player> getPlayers() {
        return players.values();
    }

    @Override
    public Player getPlayer(int id) {
        return players.get(id);
    }


    @Override
    public Collection<Bullet> getBullets() {
        return bullets;
    }

    @Override
    public void spawnPlayer(int id) {
        Player player = players.get(id);
        player.spawn(world.getSpawn(player.getTeam()));
        EventManager.sendEvent(new PlayerSpawnedEvent(id,player.getLocation()));
    }

    public void tick(float tpf) {

        delayedActionManager.tick(tpf);

        Iterator<Bullet> iter = bullets.iterator();
        while (iter.hasNext()) {
            Bullet bullet = iter.next();
            if (bullet.isDying()) {
                iter.remove();
            } else {
                bullet.tick(world, tpf);
                for (Player player : players.values()) {
                    player.testCollideWithBullet(bullet);
                }
            }
        }
        for (Player player : players.values()) {
            for (Flag flag : flags.values()) {
                if (!flag.isPickedUp() && player.collideWithPoint(flag.getLocation())) {
                    if (player.getTeam() != flag.getTeam()) {
                        Log.DEBUG("Player %s pickedup team %d flag", player.getName(),flag.getTeam());
                        player.setHasFlag(flag,true);
                        EventManager.sendEvent(new FlagEvent(player.getId(), flag.getId()));
                    } else if (player.hasFlag()) {
                        Log.DEBUG("Team %d scored a point", player.getTeam());
                        player.setHasFlag(flags.get(player.getPickedUpFlagTeam()),false);
                        EventManager.sendEvent(new FlagEvent(player.getPickedUpFlagTeam(), true));
                    }
                }
            }
        }
    }

    public void startGame() {
        EventManager.sendEvent(new InfoSyncEvent(true, buildTime,true));
        Log.INFO("Game started: BUILD MODE");

        flags.put(0,new Flag(nextEntityId++,0, world.getFlagLocation(0)));
        flags.put(1,new Flag(nextEntityId++,1, world.getFlagLocation(1)));

        for (Player player : players.values()) {
            spawnPlayer(player.getId());
        }
        delayedActionManager.add(new DelayedAction() {

            @Override
            public void execute() {
                world.setWall(false);
                EventManager.sendEvent(new InfoSyncEvent(true, 900,false));
                Log.INFO("ATTACK MODE");
            }
        }, buildTime);
    }

    @EventHandler
    public void onPlayerMovement(PlayerMovedEvent event) {
        Player player = players.get(event.playerId);
        player.setLocation(event.position);
        if (player.hasFlag()) {
            EventManager.sendEvent(new FlagEvent(flags.get(player.getPickedUpFlagTeam()).getId(), event.position));
        }
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDiedEvent event) {
        Player player = players.get(event.playerId);
        EventManager.sendEvent(new FlagEvent(player.getPickedUpFlagTeam(), true));
        if (player.hasFlag()) {
            player.setHasFlag(flags.get(player.getPickedUpFlagTeam()), false);
        }
        EventManager.sendEvent(new PlayerTeleportEvent(event.playerId, world.getSpectate(players.get(event.playerId).getTeam())));
        delayedActionManager.add(new DelayedAction() {

            @Override
            public void execute() {
                spawnPlayer(event.playerId);
            }
        }, 10);
    }

    public void processMessages(ConcurrentLinkedQueue<Message> messages) {
        for (int i = messages.size(); i > 0; i--) {
            Message m = messages.remove();
            if (m instanceof PlayerMovementMessage) {
                EventManager.sendEvent(((PlayerMovementMessage) m).event);
            } else if (m instanceof BlockChangeMessage) {
                BlockChangeMessage msg = (BlockChangeMessage) m;
                world.getBlockAt(msg.getX(),msg.getY(),msg.getZ()).setType(msg.getBlockType());
                EventManager.sendEvent(new BlockChangeEvent(msg.getBlockType(), msg.getX(), msg.getY(), msg.getZ()));
            } else if (m instanceof ActivateWeaponMessage) {
                ActivateWeaponMessage msg = (ActivateWeaponMessage) m;
                Bullet newBullet = new Bullet(nextEntityId++,msg.getSourceId(),msg.getLocation(),msg.getDirection().normalize(),msg.getSpeed());
                bullets.add(newBullet);
                EventManager.sendEvent(new EntitySpawnedEvent(newBullet));
            }
        }
    }

}
