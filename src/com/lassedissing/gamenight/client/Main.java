/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ClasspathLocator;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import com.jme3.network.*;
import com.jme3.network.serializing.Serializer;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.lassedissing.gamenight.events.BlockChangeEvent;
import com.lassedissing.gamenight.events.Event;
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
import com.lassedissing.gamenight.messages.ActivateWeaponMessage;
import com.lassedissing.gamenight.messages.BlockChangeMessage;
import com.lassedissing.gamenight.world.Chunk;
import com.lassedissing.gamenight.messages.ChunkMessage;
import com.lassedissing.gamenight.messages.UpdateMessage;
import com.lassedissing.gamenight.messages.PlayerMovementMessage;
import com.lassedissing.gamenight.messages.WelcomeMessage;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends SimpleApplication {

    Client client;

    ChunkManager chunkManager = new ChunkManager();

    private int clientId = -1;
    private Map<Integer,PlayerView> players = new HashMap<>();

    private Map<Integer,BulletView> bullets = new HashMap<>();

    public String serverIp;

    public static boolean MIPMAP = false;
    public static int ANISOTROPIC = 0;

    private boolean leftAction = false;
    private boolean rightAction = false;
    private boolean forwardAction = false;
    private boolean backAction = false;
    private boolean jumpAction = false;
    private boolean leftClick = false;
    private boolean rightClick = false;

    private boolean mouseTrapped = false;
    private boolean esdf = true;

    private boolean buildMode = false;

    private PlayerController player = new PlayerController();
    private Vector3f walkDirection = new Vector3f();

    private BitmapText healthBar;

    private boolean isSpawned = false;

    private Geometry weaponGeo;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("You must specify IP");
        }

        AppSettings settings = new AppSettings(true);
        settings.setResolution(1280, 720);
        settings.setSamples(4);
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

        rootNode.attachChild(chunkManager.init(assetManager));

        initInput();
        initNetwork();

        cam.setFrustumNear(0.4f);
        cam.setFrustumPerspective(70f, 1.6f, 0.1f, 200f);
        initCrosshair();

        player.setEyeLocation(new Vector3f(16f,50f,16f));

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
        Serializer.registerClass(PlayerSpawnedEvent.class);
        Serializer.registerClass(PlayerDiedEvent.class);
        Serializer.registerClass(BlockChangeEvent.class);

        client.addMessageListener(new ClientListener(this));

        client.start();
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

    private final static String INPUT_CAM_LEFT = "CamLeft";
    private final static String INPUT_CAM_RIGHT = "CamRight";
    private final static String INPUT_CAM_UP = "CamUp";
    private final static String INPUT_CAM_DOWN = "CamDown";
    private final static String INPUT_STRAFE_LEFT = "StrafeLeft";
    private final static String INPUT_STRAFE_RIGHT = "StrafeRight";
    private final static String INPUT_MOVE_FORWARD = "MoveForward";
    private final static String INPUT_MOVE_BACKWARD = "MoveBackward";
    private final static String INPUT_JUMP = "Jump";
    private final static String INPUT_TAB = "Tab";
    private final static String INPUT_LEFT_CLICK = "LeftClick";
    private final static String INPUT_RIGHT_CLICK = "RightClick";

    private static String[] keyMappings = new String[]{
        INPUT_CAM_LEFT,
        INPUT_CAM_RIGHT,
        INPUT_CAM_UP,
        INPUT_CAM_DOWN,
        INPUT_STRAFE_LEFT,
        INPUT_STRAFE_RIGHT,
        INPUT_MOVE_FORWARD,
        INPUT_MOVE_BACKWARD,
        INPUT_JUMP,
        INPUT_TAB,
        INPUT_LEFT_CLICK,
        INPUT_RIGHT_CLICK
    };

    private void initInput() {

        flyCam.setEnabled(false);
        inputManager.addMapping(INPUT_TAB, new KeyTrigger(KeyInput.KEY_TAB));
        if (esdf) {
            inputManager.addMapping(INPUT_STRAFE_LEFT, new KeyTrigger(KeyInput.KEY_S));
            inputManager.addMapping(INPUT_STRAFE_RIGHT, new KeyTrigger(KeyInput.KEY_F));
            inputManager.addMapping(INPUT_MOVE_FORWARD, new KeyTrigger(KeyInput.KEY_E));
            inputManager.addMapping(INPUT_MOVE_BACKWARD, new KeyTrigger(KeyInput.KEY_D));
        } else {
            inputManager.addMapping(INPUT_STRAFE_LEFT, new KeyTrigger(KeyInput.KEY_A));
            inputManager.addMapping(INPUT_STRAFE_RIGHT, new KeyTrigger(KeyInput.KEY_D));
            inputManager.addMapping(INPUT_MOVE_FORWARD, new KeyTrigger(KeyInput.KEY_W));
            inputManager.addMapping(INPUT_MOVE_BACKWARD, new KeyTrigger(KeyInput.KEY_S));
        }
        inputManager.addMapping(INPUT_JUMP, new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping(INPUT_CAM_LEFT, new MouseAxisTrigger(mouseInput.AXIS_X, true), new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping(INPUT_CAM_RIGHT, new MouseAxisTrigger(mouseInput.AXIS_X, false), new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping(INPUT_CAM_UP, new MouseAxisTrigger(mouseInput.AXIS_Y, false), new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping(INPUT_CAM_DOWN, new MouseAxisTrigger(mouseInput.AXIS_Y, true), new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping(INPUT_LEFT_CLICK, new MouseButtonTrigger(mouseInput.BUTTON_LEFT));
        inputManager.addMapping(INPUT_RIGHT_CLICK, new MouseButtonTrigger(mouseInput.BUTTON_RIGHT));


        inputManager.addListener(inputListener, keyMappings);
    }

    private void rotateCamera(float value, Vector3f axis) {

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

    private InputListener inputListener = new InputListener();

    private class InputListener implements AnalogListener, ActionListener {

        private final Vector3f CameraUp = new Vector3f(0.0f,1.0f,0.0f);

        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equalsIgnoreCase("Pause") && isPressed) {

            } else if (name.equalsIgnoreCase(INPUT_STRAFE_LEFT)) {
                leftAction = isPressed;
            } else if (name.equalsIgnoreCase(INPUT_STRAFE_RIGHT)) {
                rightAction = isPressed;
            } else if (name.equalsIgnoreCase(INPUT_MOVE_FORWARD)) {
                forwardAction = isPressed;
            } else if (name.equalsIgnoreCase(INPUT_MOVE_BACKWARD)) {
                backAction = isPressed;
            } else if (name.equalsIgnoreCase(INPUT_JUMP)) {
                jumpAction = isPressed;
            } else if (name.equalsIgnoreCase(INPUT_TAB) && isPressed) {
                //mouseTrapped = !mouseTrapped;
                buildMode = !buildMode;
            } else if (name.equalsIgnoreCase(INPUT_LEFT_CLICK)) {
                leftClick = isPressed;
            } else if (name.equalsIgnoreCase(INPUT_RIGHT_CLICK)) {
                rightClick = isPressed;
            }
        }

        @Override
        public void onAnalog(String name, float value, float tps) {

            if (name.equalsIgnoreCase(INPUT_CAM_LEFT)) {
                rotateCamera(value, CameraUp);
            } else if (name.equalsIgnoreCase(INPUT_CAM_RIGHT)) {
                rotateCamera(-value, CameraUp);
            } else if (name.equalsIgnoreCase(INPUT_CAM_UP)) {
                rotateCamera(-value, cam.getLeft());
            } else if (name.equalsIgnoreCase(INPUT_CAM_DOWN)) {
                rotateCamera(value, cam.getLeft());
            }
        }

    };

    @Override
    public void simpleUpdate(float tpf) {

        inputManager.setCursorVisible(mouseTrapped);

        if (isSpawned) {

            walkDirection.zero();

            if (leftAction) {
                walkDirection.addLocal(cam.getLeft());
            }

            if (rightAction) {
                walkDirection.addLocal(cam.getLeft().negate());
            }

            if (forwardAction) {
                walkDirection.addLocal(cam.getDirection().setY(0));
            }

            if (backAction) {
                walkDirection.addLocal(cam.getDirection().setY(0).negate());
            }

            if (jumpAction) {
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
                    if (leftClick) {
                        client.send(new BlockChangeMessage(0, selectedBlock));
                        leftClick = false;
                    }
                    if (rightClick) {
                        selectedBlock = chunkManager.getPickedBlock(cam.getLocation(), cam.getDirection(), 5f, true);
                        boolean blocked = false;
                        blocked = player.isColliding(player.getLocation().add(0, 0.1f, 0), selectedBlock);
                        for (PlayerView other : players.values()) {
                            if (blocked) break;
                            blocked = player.isColliding(other.getPosition().add(0, 0.1f, 0), selectedBlock);
                        }
                        if (!blocked) {
                            client.send(new BlockChangeMessage(1, selectedBlock));
                            rightClick = false;
                        }
                    }
                }
            } else {
                if (leftClick) {
                    client.send(new ActivateWeaponMessage(clientId, cam.getLocation(), cam.getDirection()));
                    leftClick = false;
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
                        bullets.get(event.getId()).setLocation(event.getLocation());

                    } else if (e instanceof EntitySpawnedEvent) {

                        EntitySpawnedEvent spawnEvent = (EntitySpawnedEvent) e;
                        bullets.put(spawnEvent.getId(), new BulletView(spawnEvent.getId(),spawnEvent.getLocation(),rootNode,this));

                    }  else if (e instanceof EntityDiedEvent) {

                        EntityDiedEvent event = (EntityDiedEvent) e;
                        bullets.get(event.getId()).destroy();
                        bullets.remove(event.getId());

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
                        } else {
                            PlayerView player = players.get(event.playerId);
                            player.setVisible(false);
                        }

                    }

                } else if (e instanceof BlockChangeEvent) {

                    BlockChangeEvent event = (BlockChangeEvent) e;
                    chunkManager.setBlockType(event.getBlockType(), event.getX(), event.getY(), event.getZ());

                }
            }
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
