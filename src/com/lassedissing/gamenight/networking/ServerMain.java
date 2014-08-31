package com.lassedissing.gamenight.networking;


import com.lassedissing.gamenight.networking.messages.PlayerMovementMessage;
import com.lassedissing.gamenight.networking.messages.BlockChangeMessage;
import com.lassedissing.gamenight.networking.messages.ChunkMessage;
import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.network.*;
import com.jme3.network.serializing.Serializer;
import com.jme3.system.JmeContext;
import com.lassedissing.gamenight.Log;
import com.lassedissing.gamenight.eventmanagning.EventHandler;
import com.lassedissing.gamenight.eventmanagning.EventListener;
import com.lassedissing.gamenight.eventmanagning.EventManager;
import com.lassedissing.gamenight.eventmanagning.EventStacker;
import com.lassedissing.gamenight.events.PlayerMovedEvent;
import com.lassedissing.gamenight.events.PlayerNewEvent;
import com.lassedissing.gamenight.events.PlayerStatEvent;
import com.lassedissing.gamenight.events.entity.EntityDiedEvent;
import com.lassedissing.gamenight.events.entity.EntityMovedEvent;
import com.lassedissing.gamenight.events.entity.EntitySpawnedEvent;
import com.lassedissing.gamenight.networking.messages.ActivateWeaponMessage;
import com.lassedissing.gamenight.networking.messages.UpdateMessage;
import com.lassedissing.gamenight.networking.messages.WelcomeMessage;
import com.lassedissing.gamenight.world.Bullet;
import com.lassedissing.gamenight.world.Chunk;
import com.lassedissing.gamenight.world.Player;
import com.lassedissing.gamenight.world.World;
import java.io.Console;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */


public class ServerMain extends SimpleApplication implements EventListener {

    Server server;

    World world;

    Console console;

    EventManager eventManager;
    EventStacker eventStacker;

    private int nextId = 0;

    private Map<Integer,Player> players = new HashMap<>();
    private int port = 1337;

    private List<Bullet> bullets = new LinkedList<>();

    @Override
    public void simpleInitApp() {

        console = System.console();
        Log.setConsole(console);
        Log.INFO("Starting server");

        eventManager = new EventManager();
        eventStacker = new EventStacker();
        eventManager.registerListener(eventStacker);
        eventManager.registerListener(this);

        initNetwork();

        Log.INFO("Loading world..");
        world = new World("Test");
        world.generate(4, 12);



    }

    public void initNetwork() {

        try {
            server = Network.createServer(port);
        } catch (IOException e) {
            e.printStackTrace();
            stop();
            return;
        }

        Serializer.registerClass(PlayerMovementMessage.class);
        Serializer.registerClass(PlayerMovedEvent.class);
        Serializer.registerClass(ChunkMessage.class);
        Serializer.registerClass(Chunk.class);
        Serializer.registerClass(BlockChangeMessage.class);
        Serializer.registerClass(ActivateWeaponMessage.class);
        Serializer.registerClass(UpdateMessage.class);
        Serializer.registerClass(WelcomeMessage.class);
        Serializer.registerClass(EntityMovedEvent.class);
        Serializer.registerClass(EntityDiedEvent.class);
        Serializer.registerClass(EntitySpawnedEvent.class);
        Serializer.registerClass(PlayerStatEvent.class);
        Serializer.registerClass(PlayerNewEvent.class);

        server.start();

        server.addConnectionListener(new ConnectionListener() {

            @Override
            public void connectionAdded(Server server, HostedConnection conn) {
                Log.INFO("Player name %d connected from %s", conn.getId(), conn.getAddress());
                conn.send(new WelcomeMessage(conn.getId(), players.values()));
                players.put(conn.getId(), new Player(conn.getId(), new Vector3f(17f,1f,16f)));

                eventManager.sendEvent(new PlayerNewEvent(conn.getId()));
                sendWorldToConn(conn);
            }

            @Override
            public void connectionRemoved(Server server, HostedConnection conn) {

                players.remove(conn.getId());
            }
        });

        server.addMessageListener(new ServerListener());

        Log.INFO("Server initialised on %d",port);
    }

    public static void main(String[] args) {
        ServerMain app = new ServerMain();
        Logger.getLogger("").setLevel(Level.SEVERE);
        app.start(JmeContext.Type.Headless);
    }

    private void sendWorldToConn(HostedConnection conn) {
        for (Chunk chunk : world.getAllChunks()) {
            conn.send(new ChunkMessage(chunk));
        }
    }

    private void processConsoleInput(String line) {
        String[] parts = line.split(" ");

        if (parts[0].equalsIgnoreCase("load")) {

            if (parts.length == 2) {
                world = World.load(parts[1]);
                for (HostedConnection conn : server.getConnections()) {
                    sendWorldToConn(conn);
                }
                Log.INFO("Loaded world %s", parts[1]);
            } else {
                Log.ERROR("Invalid amount of arguments: load map");
            }

        } else if (parts[0].equalsIgnoreCase("save")) {

            if (parts.length == 1) {
                world.save();
            } else if (parts.length == 2) {
                world.save(parts[1]);
            } else {
                Log.ERROR("Invalid amount of arguments: save [map]");
            }
            Log.INFO("Saved world %s", world.getName());

        } else if (parts[0].equalsIgnoreCase("new")) {

            if (parts.length == 4) {
                world = new World(parts[1]);
                world.generate(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
                for (HostedConnection conn : server.getConnections()) {
                    sendWorldToConn(conn);
                }
                Log.INFO("Generated new world with size: %d x %d", parts[2], parts[3]);
            } else {
                Log.ERROR("Invalid amount of arguments: new map width length");
            }

        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        try {
            if(console.reader().ready()) {
                String input = console.readLine();
                processConsoleInput(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        server.broadcast(eventStacker.bakeUpdateMessage());
    }

    @EventHandler
    public void onPlayerMovement(PlayerMovedEvent event) {
        players.get(event.playerId).setLocation(event.position);
    }

    @Override
    public void destroy() {
        super.destroy();
        server.close();
    }

    public class ServerListener implements MessageListener<HostedConnection> {

        @Override
        public void messageReceived(HostedConnection source, Message m) {
            if (m instanceof PlayerMovementMessage) {
                eventManager.sendEvent(((PlayerMovementMessage) m).event);
            } else if (m instanceof BlockChangeMessage) {
                BlockChangeMessage msg = (BlockChangeMessage) m;
                world.getBlockAt(msg.location).setType(msg.blockType);
                server.broadcast(m);
            } else if (m instanceof ActivateWeaponMessage) {
                ActivateWeaponMessage msg = (ActivateWeaponMessage) m;
                Bullet newBullet = new Bullet(nextId++,source.getId(),msg.location,msg.direction.normalize(),15f);
                bullets.add(newBullet);
                eventManager.sendEvent(new EntitySpawnedEvent(newBullet.getId(), newBullet.getLocation()));
            }
        }

    }

}
