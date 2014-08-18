/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ClasspathLocator;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.InputListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.network.*;
import com.jme3.network.serializing.Serializer;
import com.jme3.renderer.lwjgl.LwjglRenderer;
import com.lassedissing.gamenight.world.Chunk;
import com.lassedissing.gamenight.networking.ChunkMessage;
import com.lassedissing.gamenight.networking.NewUserMessage;
import com.lassedissing.gamenight.networking.PositionMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends SimpleApplication {
    
    Client client;
    
    Chunk chunk = new Chunk();

    private int playerId;
    private Map<Integer,PlayerModel> players = new HashMap<>();
    
    public String serverIp;
    
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("You must specify IP");
        }
        
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1280, 720);
        Main app = new Main();
        app.setSettings(settings);
        app.setShowSettings(false);
        Logger.getLogger("").setLevel(Level.WARNING);
        app.serverIp = args[0];
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        assetManager.registerLocator("lib/assets.jar", ClasspathLocator.class);
        inputManager.setCursorVisible(true);
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(7);
        cam.setLocation(new Vector3f(7f,8f,40f));
        inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_TAB));
        
        inputManager.addListener(new ActionListener() {

            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
                if (name.equalsIgnoreCase("Pause") && isPressed) {
                    flyCam.setEnabled(!flyCam.isEnabled());
                }
            }
            
}, "Pause");
        
        try {
            client = Network.connectToServer(serverIp, 1337);
        } catch (IOException e) {
            e.printStackTrace();
            stop();
            return;
        }
        
        Serializer.registerClass(PositionMessage.class);
        Serializer.registerClass(ChunkMessage.class);
        Serializer.registerClass(Chunk.class);
        Serializer.registerClass(NewUserMessage.class);

        client.addMessageListener(new ClientListener(this));
        
        client.start();
        
    }

    @Override
    public void simpleUpdate(float tpf) {
        client.send(new PositionMessage(-1,cam.getLocation()));
    }

    @Override
    public void simpleRender(RenderManager rm) {
        viewPort.setBackgroundColor(new ColorRGBA(0.4f, 0.6f, 0.9f, 1.0f));
    }
    
    @Override
    public void destroy() {
        client.close();
        super.destroy();
    }
    
    private void processMessage(Message m) {
        if (m instanceof ChunkMessage) {
                ChunkMessage chunkMsg = (ChunkMessage) m;
                chunk = chunkMsg.chunk;
                chunk.buildMesh();
                Geometry geom = new Geometry("Chunk",chunk.getMesh());
                geom.scale(0.5f);

                Material mat = new Material(assetManager, "MatDefs/Block.j3md");
                mat.getAdditionalRenderState().setWireframe(false);

                Texture texAtlas = assetManager.loadTexture("Textures/TextureAtlas.png");
                mat.setTexture("Atlas", texAtlas);
                texAtlas.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
                texAtlas.setMagFilter(Texture.MagFilter.Nearest);

                geom.setMaterial(mat);

                rootNode.attachChild(geom);
            } else if (m instanceof NewUserMessage) {
                NewUserMessage newUserMsg = (NewUserMessage) m;
                players.put(newUserMsg.playerId, new PlayerModel(newUserMsg.playerId, rootNode, this));
            } else if (m instanceof PositionMessage) {
                PositionMessage posMsg = (PositionMessage) m;
                System.out.println("Player moved " + posMsg.playerId);
                players.get(posMsg.playerId).setPosition(posMsg.playerPos);
            }
    }

    public class ClientListener implements MessageListener<Client> {

        Main app;
        
        public ClientListener(Main main) {
            app = main;
        }
        
        @Override
        public void messageReceived(Client source, final Message m) {
            app.enqueue(new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                    processMessage(m);
                    return null;
                }
            });
        }
        
    }
    
}
