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
import com.lassedissing.gamenight.events.Event;
import com.lassedissing.gamenight.events.entity.EntitySpawnedEvent;
import com.lassedissing.gamenight.events.player.PlayerMovedEvent;
import com.lassedissing.gamenight.events.player.PlayerNewEvent;
import com.lassedissing.gamenight.events.player.PlayerStatEvent;
import com.lassedissing.gamenight.messages.ActivateWeaponMessage;
import com.lassedissing.gamenight.messages.BlockChangeMessage;
import com.lassedissing.gamenight.messages.PlayerMovementMessage;
import com.lassedissing.gamenight.world.Bullet;
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

    private Map<Integer,Player> players = new HashMap<>();
    private List<Bullet> bullets = new LinkedList<>();

    private int nextEntityId = 0;

    public void init() {

        eventManager = new EventManager();

        eventManager.registerListener(this);

        Log.INFO("Loading world..");
        world = new World("Test");
        world.generate(2, 2);

    }

    public void destroy() {

    }

    public void playerConnected(int id) {
        players.put(id, new Player(id, Vector3f.ZERO));
        eventManager.sendEvent(new PlayerNewEvent(id));
    }

    public void playerDisconnected(int id) {
        players.remove(id);
    }

    @Override
    public void sendEvent(Event event) {
        eventManager.sendEvent(event);
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
    public Collection<Bullet> getBullets() {
        return bullets;
    }

    public void tick(float tpf) {
        Iterator<Bullet> iter = bullets.iterator();
        while (iter.hasNext()) {
            Bullet bullet = iter.next();
            if (bullet.isDying()) {
                iter.remove();
            } else {
                bullet.tick(world, eventManager, tpf);
                for (Player player : players.values()) {
                    if (player.getId() != bullet.getOwnerId() && player.collideWithPoint(bullet.getLocation())) {
                        bullet.kill(eventManager);
                        player.damage(1);
                        eventManager.sendEvent(new PlayerStatEvent(player.getId(), player.getHealth()));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMovement(PlayerMovedEvent event) {
        players.get(event.playerId).setLocation(event.position);
    }

    public void processMessages(ConcurrentLinkedQueue<Message> messages) {
        for (int i = messages.size(); i > 0; i--) {
            Message m = messages.remove();
            if (m instanceof PlayerMovementMessage) {
                eventManager.sendEvent(((PlayerMovementMessage) m).event);
            } else if (m instanceof BlockChangeMessage) {
                BlockChangeMessage msg = (BlockChangeMessage) m;
                world.getBlockAt(msg.getX(),msg.getY(),msg.getZ()).setType(msg.getBlockType());
                sendEvent(new BlockChangeEvent(msg.getBlockType(), msg.getX(), msg.getY(), msg.getZ()));
            } else if (m instanceof ActivateWeaponMessage) {
                ActivateWeaponMessage msg = (ActivateWeaponMessage) m;
                Bullet newBullet = new Bullet(nextEntityId++,msg.getSourceId(),msg.getLocation(),msg.getDirection().normalize(),15f);
                bullets.add(newBullet);
                eventManager.sendEvent(new EntitySpawnedEvent(newBullet.getId(), newBullet.getLocation()));
            }
        }
    }

}
