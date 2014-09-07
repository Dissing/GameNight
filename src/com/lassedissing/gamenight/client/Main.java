/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client;

import com.lassedissing.gamenight.client.views.BulletView;
import com.lassedissing.gamenight.client.views.EntityView;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ClasspathLocator;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import com.jme3.network.*;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.lassedissing.gamenight.NetworkRegistrar;
import com.lassedissing.gamenight.client.views.FlagView;
import com.lassedissing.gamenight.events.BlockChangeEvent;
import com.lassedissing.gamenight.events.Event;
import com.lassedissing.gamenight.events.FlagEvent;
import com.lassedissing.gamenight.events.player.PlayerEvent;
import com.lassedissing.gamenight.events.player.PlayerMovedEvent;
import com.lassedissing.gamenight.events.player.PlayerNewEvent;
import com.lassedissing.gamenight.events.player.PlayerStatEvent;
import com.lassedissing.gamenight.events.entity.EntityDiedEvent;
import com.lassedissing.gamenight.events.entity.EntityEvent;
import com.lassedissing.gamenight.events.entity.EntityMovedEvent;
import com.lassedissing.gamenight.events.entity.EntitySpawnedEvent;
import com.lassedissing.gamenight.events.player.PlayerDiedEvent;
import com.lassedissing.gamenight.events.player.PlayerSpawnedEvent;
import com.lassedissing.gamenight.events.player.PlayerTeleportEvent;
import com.lassedissing.gamenight.messages.ActivateWeaponMessage;
import com.lassedissing.gamenight.messages.BlockChangeMessage;
import com.lassedissing.gamenight.messages.ChunkMessage;
import com.lassedissing.gamenight.messages.JoinMessage;
import com.lassedissing.gamenight.messages.UpdateMessage;
import com.lassedissing.gamenight.messages.PlayerMovementMessage;
import com.lassedissing.gamenight.messages.WelcomeMessage;
import com.lassedissing.gamenight.world.Bullet;
import com.lassedissing.gamenight.world.EntityType;
import com.lassedissing.gamenight.world.Flag;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends SimpleApplication {

    Client client;

    ChunkManager chunkManager = new ChunkManager();
    InputProcessor inputProcessor = new InputProcessor(this);

    private int clientId = -1;
    private Map<Integer,PlayerView> players = new HashMap<>();

    private Map<Integer,EntityView> entities = new HashMap<>();

    public String serverIp;
    public String name;

    public static boolean MIPMAP = false;
    public static int ANISOTROPIC = 0;

    public boolean buildMode = false;

    private PlayerController player = new PlayerController();
    private Vector3f walkDirection = new Vector3f();

    private BitmapText healthBar;

    private boolean isSpawned = false;

    private Geometry weaponGeo;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("You must specify IP and name");
        }

        AppSettings settings = new AppSettings(true);
        settings.setResolution(1280, 720);
        settings.setSamples(4);
        Main app = new Main();
        app.setSettings(settings);
        app.setShowSettings(false);
        Logger.getLogger("").setLevel(Level.WARNING);
        app.serverIp = args[0];
        app.name = args[1];
        app.start();
    }

    @Override
    public void simpleInitApp() {

        ClientSettings.init(new File("config.ini"));

        assetManager.registerLocator("lib/assets.jar", ClasspathLocator.class);

        rootNode.attachChild(chunkManager.init(assetManager));

        flyCam.setEnabled(false);

        inputProcessor.init(inputManager,mouseInput);
        initNetwork();

        cam.setFrustumNear(0.4f);
        cam.setFrustumPerspective(70f, 1.6f, 0.1f, 200f);
        initCrosshair();

        healthBar = new BitmapText(guiFont,false);
        healthBar.setSize(guiFont.getCharSet().getRenderedSize());
        healthBar.setColor(ColorRGBA.White);
        healthBar.setLocalTranslation(10, 700, 0);
        healthBar.setText("Health: 10");
        guiNode.attachChild(healthBar);

        Camera weaponCam = cam.clone();
        weaponCam.setLocation(new Vector3f(0, 0, 0));
        weaponCam.lookAt(new Vector3f(0, 0, 1), Vector3f.UNIT_Y);
        weaponCam.setLocation(new Vector3f(0, 0, 0));
        ViewPort weaponView = renderManager.createMainView("weapon view", weaponCam);
        weaponView.setClearFlags(false, true, false);

        WeaponView ak47 = new WeaponView("AK47", assetManager);

        weaponGeo = new Geometry("Weapon",ak47.mesh);
        weaponView.attachScene(weaponGeo);
        weaponView.setEnabled(true);

        weaponGeo.setLocalTranslation(-0.2f, -0.7f, 1f);
        weaponGeo.rotate(-0.2f, -2.8f, 0);
        weaponGeo.setMaterial(ak47.weaponMaterial);
        weaponGeo.scale(0.03f);

        weaponGeo.updateGeometricState();
    }

    private void initNetwork() {
        try {
            client = Network.connectToServer(serverIp, 1337);
        } catch (IOException e) {
            e.printStackTrace();
            stop();
            return;
        }

        NetworkRegistrar.register();

        client.addMessageListener(new ClientListener(this));

        client.start();

        client.send(new JoinMessage(name));
    }

    private void initCrosshair() {
        BitmapText crosshair = new BitmapText(guiFont, false);
        crosshair.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        crosshair.setText("+");
        crosshair.setLocalTranslation(
                settings.getWidth() / 2 - crosshair.getLineWidth()/2,
                settings.getHeight() / 2 + crosshair.getLineHeight()/2, 0);
        guiNode.attachChild(crosshair);
    }

    private void initInput() {

    }

    public void rotateCamera(float value, Vector3f axis) {

        Matrix3f mat = new Matrix3f();
        mat.fromAngleNormalAxis(value, axis);

        Vector3f up = cam.getUp();
        Vector3f left = cam.getLeft();
        Vector3f dir = cam.getDirection();

        mat.mult(up,up);

        if (up.y < 0)
            return;

        mat.mult(left,left);
        mat.mult(dir,dir);

        Quaternion quaternion = new Quaternion();
        quaternion.fromAxes(left, up, dir);
        quaternion.normalizeLocal();

        cam.setAxes(quaternion);
    }

    public PlayerController getPlayer() {
        return player;
    }

    @Override
    public void simpleUpdate(float tpf) {

        inputManager.setCursorVisible(inputProcessor.isMouseTrapped());

        if (isSpawned) {

            walkDirection.zero();

            if (inputProcessor.leftAction()) {
                walkDirection.addLocal(cam.getLeft());
            }

            if (inputProcessor.rightAction()) {
                walkDirection.addLocal(cam.getLeft().negate());
            }

            if (inputProcessor.forwardAction()) {
                walkDirection.addLocal(cam.getDirection().setY(0));
            }

            if (inputProcessor.backAction()) {
                walkDirection.addLocal(cam.getDirection().setY(0).negate());
            }

            if (inputProcessor.jumpAction()) {
                player.jump();
            }

            player.tick(cam,walkDirection,chunkManager,Math.min(tpf,0.03333f));

            if (!player.hasMoved() && clientId != -1) {
                client.send(new PlayerMovementMessage( new PlayerMovedEvent(clientId, player.getEyeLocation(), cam.getDirection()) ));
            }

            if (buildMode) {
                Vector3f selectedBlock = chunkManager.getPickedBlock(cam.getLocation(), cam.getDirection(), 5f, false);
                if (selectedBlock != null) {
                    chunkManager.showSelectBlock((int)selectedBlock.x, (int)selectedBlock.y, (int)selectedBlock.z);
                    if (inputProcessor.leftClick()) {
                        client.send(new BlockChangeMessage(0, selectedBlock));
                        inputProcessor.eatLeftClick();
                    }
                    if (inputProcessor.rightClick()) {
                        selectedBlock = chunkManager.getPickedBlock(cam.getLocation(), cam.getDirection(), 5f, true);
                        boolean blocked = false;
                        blocked = player.isColliding(player.getLocation().add(0, 0.1f, 0), selectedBlock);
                        for (PlayerView other : players.values()) {
                            if (blocked) break;
                            blocked = player.isColliding(other.getPosition().add(0, 0.1f, 0), selectedBlock);
                        }
                        if (!blocked) {
                            client.send(new BlockChangeMessage(1, selectedBlock));
                            inputProcessor.eatRightClick();
                        }
                    }
                }
            } else {
                if (inputProcessor.leftClick()) {
                    client.send(new ActivateWeaponMessage(clientId, cam.getLocation(), cam.getDirection()));
                    inputProcessor.eatLeftClick();
                }
            }
        }

        cam.setLocation(player.getEyeLocation());


    }

    @Override
    public void simpleRender(RenderManager rm) {
        weaponGeo.updateGeometricState();
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
            chunkManager.addChunk(chunkMsg.getChunk());

        } else if (m instanceof WelcomeMessage) {

            WelcomeMessage msg = (WelcomeMessage) m;
            clientId = msg.playerId;

            System.out.println("Joined server and got id: " + clientId);
            for (PlayerNewEvent other : msg.otherPlayers) {
                players.put(other.playerId, new PlayerView(other.playerId, rootNode, this));
            }

        } else if (m instanceof UpdateMessage) {

            for (Event e : ((UpdateMessage) m).events) {

                if (e instanceof EntityEvent) {

                    if (e instanceof EntityMovedEvent) {

                        EntityMovedEvent event = (EntityMovedEvent) e;
                        entities.get(event.getId()).setLocation(event.getLocation());

                    } else if (e instanceof EntitySpawnedEvent) {

                        EntitySpawnedEvent spawnEvent = (EntitySpawnedEvent) e;
                        if (spawnEvent.getType() == EntityType.Flag) {
                            Flag flag = (Flag)spawnEvent.getEntity();
                            entities.put(spawnEvent.getId(), new FlagView(spawnEvent.getId(),flag.getTeam(),flag.getLocation(),rootNode,this));

                        } else if (spawnEvent.getType() == EntityType.Bullet) {
                            Bullet bullet = (Bullet)spawnEvent.getEntity();
                            entities.put(spawnEvent.getId(), new BulletView(spawnEvent.getId(),bullet.getLocation(),rootNode,this));
                        }

                    }  else if (e instanceof EntityDiedEvent) {

                        EntityDiedEvent event = (EntityDiedEvent) e;
                        entities.get(event.getId()).destroy();
                        entities.remove(event.getId());

                    }

                } else if (e instanceof PlayerEvent) {

                    if (e instanceof PlayerMovedEvent) {

                        PlayerMovedEvent event = (PlayerMovedEvent) e;
                        if (event.playerId != clientId) {
                            PlayerView playerView = players.get(event.playerId);
                            playerView.setPosition(event.position);
                            playerView.setRotation(event.rotation);
                        }

                    } else if (e instanceof PlayerStatEvent) {

                        PlayerStatEvent event = (PlayerStatEvent) e;
                        if (event.playerId == clientId) {
                            healthBar.setText("Health: " + event.getHealth());
                        }

                    } else if (e instanceof PlayerNewEvent) {

                        PlayerNewEvent event = (PlayerNewEvent) e;
                        if (event.playerId != clientId) {
                            players.put(event.playerId, new PlayerView(event.playerId, rootNode, this));
                        }

                    } else if (e instanceof PlayerSpawnedEvent) {

                        PlayerSpawnedEvent event = (PlayerSpawnedEvent) e;
                        if (event.playerId == clientId) {
                            player.setEyeLocation(event.getLocation());
                            isSpawned = true;
                            System.out.println("Spawning");
                        } else {
                            PlayerView player = players.get(event.playerId);
                            player.setVisible(true);
                            player.setPosition(event.getLocation());
                        }

                    } else if (e instanceof PlayerDiedEvent) {

                        PlayerDiedEvent event = (PlayerDiedEvent) e;
                        if (event.playerId == clientId) {
                            cam.setLocation(new Vector3f(16.5f,32,16.5f));
                            isSpawned = false;
                            System.out.println("Died");
                        } else {
                            PlayerView player = players.get(event.playerId);
                            player.setVisible(false);
                        }

                    } else if (e instanceof PlayerTeleportEvent) {
                        PlayerTeleportEvent event = (PlayerTeleportEvent) e;
                        if (event.playerId != clientId) {
                            PlayerView playerView = players.get(event.playerId);
                            playerView.setPosition(event.position);
                        } else {
                            player.setEyeLocation(event.position);
                        }
                    }

                } else if (e instanceof BlockChangeEvent) {

                    BlockChangeEvent event = (BlockChangeEvent) e;
                    chunkManager.setBlockType(event.getBlockType(), event.getX(), event.getY(), event.getZ());

                } else if (e instanceof FlagEvent) {

                    FlagEvent event = (FlagEvent) e;
                    if (event.isReset()) {
                        ((FlagView)entities.get(event.getFlagId())).hide(false);
                    } else {
                        ((FlagView)entities.get(event.getFlagId())).hide(true);
                    }

                }
            }
            chunkManager.rebuildChunks();

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
