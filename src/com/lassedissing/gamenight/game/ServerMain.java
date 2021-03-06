package com.lassedissing.gamenight.game;


import com.lassedissing.gamenight.messages.ChunkMessage;
import com.jme3.app.SimpleApplication;
import com.jme3.network.*;
import com.jme3.system.JmeContext;
import com.lassedissing.gamenight.Log;
import com.lassedissing.gamenight.NetworkRegistrar;
import com.lassedissing.gamenight.eventmanagning.EventStacker;
import com.lassedissing.gamenight.messages.ChatMessage;
import com.lassedissing.gamenight.messages.JoinMessage;
import com.lassedissing.gamenight.messages.UpdateMessage;
import com.lassedissing.gamenight.messages.WelcomeMessage;
import com.lassedissing.gamenight.world.Chunk;
import com.lassedissing.gamenight.world.World;
import java.io.Console;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */


public class ServerMain extends SimpleApplication {

    Server server;

    Console console;

    private ServerGameContainer gameContainer;

    EventStacker eventStacker;


    private int port = 1337;

    private ConcurrentLinkedQueue<Message> messageQueue = new ConcurrentLinkedQueue<>();


    @Override
    public void simpleInitApp() {

        console = System.console();
        Log.setConsole(console);
        Log.INFO("Starting server");

        gameContainer = new ServerGameContainer();
        gameContainer.init();

        eventStacker = new EventStacker();
        gameContainer.getEventManager().registerListener(eventStacker);

        initNetwork();


    }

    public void initNetwork() {

        try {
            server = Network.createServer(port);
        } catch (IOException e) {
            e.printStackTrace();
            stop();
            return;
        }

        NetworkRegistrar.register();

        server.start();

        server.addConnectionListener(new ConnectionListener() {

            @Override
            public void connectionAdded(Server server, HostedConnection conn) {

            }

            @Override
            public void connectionRemoved(Server server, HostedConnection conn) {
                gameContainer.playerDisconnected(conn.getId());
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
        for (Chunk chunk : gameContainer.getWorld().getAllChunks()) {
            conn.send(new ChunkMessage(chunk));
        }
    }

    private void processConsoleInput(String line) {

        String[] parts = line.split(" ");

        if (parts[0].equalsIgnoreCase("load")) {

            if (parts.length == 2) {
                gameContainer.replaceWorld(World.load(parts[1]));
                for (HostedConnection conn : server.getConnections()) {
                    sendWorldToConn(conn);
                }
                Log.INFO("Loaded world %s", parts[1]);
            } else {
                Log.ERROR("Invalid amount of arguments: load map");
            }

        } else if (parts[0].equalsIgnoreCase("save")) {

            if (parts.length == 1) {
                gameContainer.getWorld().save();
            } else if (parts.length == 2) {
                gameContainer.getWorld().save(parts[1]);
            } else {
                Log.ERROR("Invalid amount of arguments: save [map]");
            }
            Log.INFO("Saved world %s", gameContainer.getWorld().getName());

        } else if (parts[0].equalsIgnoreCase("new")) {

            if (parts.length == 4) {
                gameContainer.replaceWorld(new World(parts[1],2));
                gameContainer.getWorld().generate(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
                for (HostedConnection conn : server.getConnections()) {
                    sendWorldToConn(conn);
                }
                Log.INFO("Generated new world with size: %d x %d", parts[2], parts[3]);
            } else {
                Log.ERROR("Invalid amount of arguments: new map width length");
            }

        } else if (parts[0].equalsIgnoreCase("start")) {

            gameContainer.startGame();

        } else if (parts[0].equalsIgnoreCase("wall")) {

            if (parts.length == 2) {
                gameContainer.getWorld().setWall(parts[1].equalsIgnoreCase("up"));
            }

        } else if (parts[0].equalsIgnoreCase("hurt")) {

            if (parts.length == 3) {
                gameContainer.getPlayer(Integer.parseInt(parts[1])).damage(Integer.parseInt(parts[2]));
            }

        } else {
            Log.DEBUG("Did not reconize command %s", parts[0]);
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


        gameContainer.processMessages(messageQueue);
        gameContainer.tick(tpf);

        server.broadcast(eventStacker.bakeUpdateMessages());

    }

    @Override
    public void destroy() {
        super.destroy();
        server.close();
    }

    public class ServerListener implements MessageListener<HostedConnection> {

        @Override
        public void messageReceived(HostedConnection source, Message m) {

            if (!(m instanceof JoinMessage || m instanceof ChatMessage)) {
                messageQueue.add(m);
            } else if (m instanceof ChatMessage) {
                ChatMessage msg = (ChatMessage) m;
                if (msg.getMessage().charAt(0) == '/') {
                    processConsoleInput(msg.getMessage().substring(1).trim());
                } else {
                    server.broadcast(new ChatMessage(msg.getPlayerId(),gameContainer.getPlayer(msg.getPlayerId()).getName()+": "+msg.getMessage()));
                }

            } else {
                String name = ((JoinMessage)m).getName();
                Log.INFO("Player %s:%d connected from %s", name, source.getId(), source.getAddress());
                source.send(new WelcomeMessage(source.getId(), gameContainer.getPlayers()));
                gameContainer.playerConnected(source.getId(),name);
                sendWorldToConn(source);
            }

        }

    }

}
