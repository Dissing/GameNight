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
import com.lassedissing.gamenight.networking.BlockChangeMessage;
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
    
    ChunkManager chunkManager = new ChunkManager();

    private int playerId;
    private Map<Integer,PlayerView> players = new HashMap<>();
    
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
    
    private PlayerBox player = new PlayerBox();
    private Vector3f walkDirection = new Vector3f();
    
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
        
        player.setLocation(new Vector3f(17,1,16));
        cam.setFrustumNear(0.4f);
        cam.setFrustumPerspective(60f, 1.6f, 0.1f, 50f);
        initCrosshair();
    }
    
    private void initNetwork() {
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
        Serializer.registerClass(BlockChangeMessage.class);

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
        inputManager.addMapping(INPUT_STRAFE_LEFT, new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping(INPUT_STRAFE_RIGHT, new KeyTrigger(KeyInput.KEY_F));
        inputManager.addMapping(INPUT_MOVE_FORWARD, new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping(INPUT_MOVE_BACKWARD, new KeyTrigger(KeyInput.KEY_D));
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
                mouseTrapped = !mouseTrapped;
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
        
        if (leftAction || rightAction || forwardAction || backAction || jumpAction) {
            client.send(new PositionMessage(-1,cam.getLocation()));
        }
        
        
        player.tick(cam,walkDirection,chunkManager,Math.min(tpf,0.03333f));

        Vector3f selectedBlock = chunkManager.getPickedBlock(cam.getLocation(), cam.getDirection(), 5f, false);
        if (selectedBlock != null) {
            chunkManager.showSelectBlock((int)selectedBlock.x, (int)selectedBlock.y, (int)selectedBlock.z);
            if (leftClick) {
                client.send(new BlockChangeMessage(0, selectedBlock));
                leftClick = false;
            }
            if (rightClick) {
                selectedBlock = chunkManager.getPickedBlock(cam.getLocation(), cam.getDirection(), 5f, true);
                client.send(new BlockChangeMessage(1, selectedBlock));
                rightClick = false;
            }
        }
        
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
                chunkManager.addChunk(chunkMsg.chunk);
            } else if (m instanceof NewUserMessage) {
                NewUserMessage newUserMsg = (NewUserMessage) m;
                players.put(newUserMsg.playerId, new PlayerView(newUserMsg.playerId, rootNode, this));
            } else if (m instanceof PositionMessage) {
                PositionMessage posMsg = (PositionMessage) m;
                players.get(posMsg.playerId).setPosition(posMsg.playerPos);
            } else if (m instanceof BlockChangeMessage) {
                BlockChangeMessage blcMsg = (BlockChangeMessage) m;
                chunkManager.setBlockType(blcMsg.blockType, (int)blcMsg.location.x, (int)blcMsg.location.y, (int)blcMsg.location.z);
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
