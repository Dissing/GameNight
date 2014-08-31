package com.lassedissing.gamenight.networking;


import com.lassedissing.gamenight.networking.messages.PlayerMovementMessage;
import com.lassedissing.gamenight.networking.messages.BlockChangeMessage;
import com.lassedissing.gamenight.networking.messages.NewUserMessage;
import com.lassedissing.gamenight.networking.messages.ChunkMessage;
import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.network.*;
import com.jme3.network.serializing.Serializer;
import com.jme3.system.JmeContext;
import com.lassedissing.gamenight.Log;
import com.lassedissing.gamenight.eventmanagning.EventManager;
import com.lassedissing.gamenight.events.PlayerMovedEvent;
import com.lassedissing.gamenight.networking.messages.ActivateWeaponMessage;
import com.lassedissing.gamenight.networking.messages.EntityUpdateMessage;
import com.lassedissing.gamenight.world.Bullet;
import com.lassedissing.gamenight.world.Chunk;
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


public class ServerMain extends SimpleApplication{

    Server server;

    World world;

    Console console;

    EventManager eventManager;

    private int nextId = 0;

    private Map<Integer,HostedConnection> connections = new HashMap<>();
    private int port = 1337;

    private List<Bullet> bullets = new LinkedList<>();

    @Override
    public void simpleInitApp() {

        console = System.console();
        Log.setConsole(console);
        Log.INFO("Starting server");

        eventManager = new EventManager();
        eventManager.registerListener(new MovementListener());

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
        Serializer.registerClass(NewUserMessage.class);
        Serializer.registerClass(BlockChangeMessage.class);
        Serializer.registerClass(ActivateWeaponMessage.class);
        Serializer.registerClass(EntityUpdateMessage.class);
        Serializer.registerClass(Bullet.class);

        server.start();

        server.addConnectionListener(new ConnectionListener() {

            @Override
            public void connectionAdded(Server server, HostedConnection conn) {
                Log.INFO("Player name %d conncted from %s", conn.getId(), conn.getAddress());
                for (int id : connections.keySet()) {
                    conn.send(new NewUserMessage(id));
                }
                sendWorldToConn(conn);
                server.broadcast(Filters.notEqualTo(conn), new NewUserMessage(conn.getId()));
                connections.put(conn.getId(),conn);
            }

            @Override
            public void connectionRemoved(Server server, HostedConnection conn) {
                connections.remove(conn);
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
                for (HostedConnection conn : connections.values()) {
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
                for (HostedConnection conn : connections.values()) {
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
                bullet.tick(world, tpf);
            }
        }
        server.broadcast(new EntityUpdateMessage(bullets));
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
                PlayerMovementMessage msg = (PlayerMovementMessage) m;
                for (PlayerMovedEvent event : msg.events) {
                    eventManager.sendEvent(event);
                    server.broadcast(Filters.notEqualTo( source ),new PlayerMovementMessage(new PlayerMovedEvent(source.getId(), event.position, event.rotation)));
                }
            } else if (m instanceof BlockChangeMessage) {
                BlockChangeMessage msg = (BlockChangeMessage) m;
                world.getBlockAt(msg.location).setType(msg.blockType);
                server.broadcast(m);
            } else if (m instanceof ActivateWeaponMessage) {
                ActivateWeaponMessage msg = (ActivateWeaponMessage) m;
                bullets.add(new Bullet(nextId++,msg.location,msg.direction.normalize(),15f));
            }
        }

    }

}
