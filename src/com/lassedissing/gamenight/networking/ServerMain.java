package com.lassedissing.gamenight.networking;


import com.jme3.app.SimpleApplication;
import com.jme3.network.*;
import com.jme3.network.serializing.Serializer;
import com.jme3.system.JmeContext;
import com.lassedissing.gamenight.world.Chunk;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */


public class ServerMain extends SimpleApplication{

    Server server;
    
    Chunk chunks[] = new Chunk[4];
    
    private Map<Integer,HostedConnection> connections = new HashMap<>();
    
    @Override
    public void simpleInitApp() {
        
        try {
            server = Network.createServer(1337);
        } catch (IOException e) {
            e.printStackTrace();
            stop();
            return;
        }
        
        Serializer.registerClass(PositionMessage.class);
        Serializer.registerClass(ChunkMessage.class);
        Serializer.registerClass(Chunk.class);
        Serializer.registerClass(NewUserMessage.class);
        
        for (int i = 0; i < chunks.length; i++) {
            chunks[i] = new Chunk(i / 2,0,i % 2);
        }
        
        server.start();
        
        server.addConnectionListener(new ConnectionListener() {

            @Override
            public void connectionAdded(Server server, HostedConnection conn) {
                System.out.println("Connection!");
                for (int id : connections.keySet()) {
                    conn.send(new NewUserMessage(id));
                }
                for (int i = 0; i < chunks.length; i++) {
                    conn.send(new ChunkMessage(chunks[i]));
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
        
        
    }
    
    public static void main(String[] args) {
        ServerMain app = new ServerMain();
        app.start(JmeContext.Type.Headless);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        
    }
    
    @Override
    public void destroy() {
        server.close();
        super.destroy(); //To change body of generated methods, choose Tools | Templates.
    }
    
    public class ServerListener implements MessageListener<HostedConnection> {

        @Override
        public void messageReceived(HostedConnection source, Message m) {
            if (m instanceof PositionMessage) {
                PositionMessage posMsg = (PositionMessage) m;
                server.broadcast(Filters.notEqualTo( source ),new PositionMessage(source.getId(), posMsg.playerPos));
            }
        }
        
    }

}
