package com.lassedissing.gamenight.networking;


import com.jme3.app.SimpleApplication;
import com.jme3.network.*;
import com.jme3.network.serializing.Serializer;
import com.jme3.system.JmeContext;
import com.lassedissing.gamenight.Log;
import com.lassedissing.gamenight.world.Chunk;
import com.lassedissing.gamenight.world.World;
import java.io.Console;
import java.io.IOException;
import java.util.HashMap;
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

    private Map<Integer,HostedConnection> connections = new HashMap<>();
    private int port = 1337;

    @Override
    public void simpleInitApp() {

        console = System.console();
        Log.setConsole(console);
        Log.INFO("Starting server");

        initNetwork();

        Log.INFO("Loading world..");
        world = new World();
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

        Serializer.registerClass(PositionMessage.class);
        Serializer.registerClass(ChunkMessage.class);
        Serializer.registerClass(Chunk.class);
        Serializer.registerClass(NewUserMessage.class);
        Serializer.registerClass(BlockChangeMessage.class);

        server.start();

        server.addConnectionListener(new ConnectionListener() {

            @Override
            public void connectionAdded(Server server, HostedConnection conn) {
                Log.INFO("Player name@%d conncted from %s", conn.getId(), conn.getAddress() );
                for (int id : connections.keySet()) {
                    conn.send(new NewUserMessage(id));
                }
                for (Chunk chunk : world.getAllChunks()) {
                    conn.send(new ChunkMessage(chunk));
                }
                server.broadcast(Filters.notEqualTo(conn), new NewUserMessage(conn.getId()));
                connections.put(conn.getId(),conn);
            }

            @Override
            public void connectionRemoved(Server server, HostedConnection conn) {
                connections.remove(conn);
            }
        });

        server.addMessageListener(new ServerListener());

        Log.INFO("Server initialised on %d");
    }

    public static void main(String[] args) {
        ServerMain app = new ServerMain();
        Logger.getLogger("").setLevel(Level.SEVERE);
        app.start(JmeContext.Type.Headless);
    }

    @Override
    public void simpleUpdate(float tpf) {
        try {
            if(console.reader().ready()) {
                String input = console.readLine();
                System.out.println(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void destroy() {
        super.destroy();
        server.close();
    }

    public class ServerListener implements MessageListener<HostedConnection> {

        @Override
        public void messageReceived(HostedConnection source, Message m) {
            if (m instanceof PositionMessage) {
                PositionMessage posMsg = (PositionMessage) m;
                server.broadcast(Filters.notEqualTo( source ),new PositionMessage(source.getId(), posMsg.playerPos));
            } else if (m instanceof BlockChangeMessage) {
                BlockChangeMessage msg = (BlockChangeMessage) m;
                world.getBlockAt(msg.location).setType(msg.blockType);
                server.broadcast(m);
            }
        }

    }

}
