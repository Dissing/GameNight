/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Vector3f;
import static com.lassedissing.gamenight.client.InputProcessor.InputAction.*;


public class InputProcessor implements AnalogListener, ActionListener, RawInputListener {

    private boolean leftAction = false;
    private boolean rightAction = false;
    private boolean forwardAction = false;
    private boolean backAction = false;
    private boolean jumpAction = false;
    private boolean leftClick = false;
    private boolean rightClick = false;

    private boolean mouseTrapped = false;
    private boolean chatMode = false;
    private boolean inventoryMode = false;
    private boolean esdf = true;

    private Main main;

    InputProcessor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public enum InputAction {
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
        INPUT_CRAWL,
        INPUT_LEFT_CLICK,
        INPUT_RIGHT_CLICK,
        INPUT_SELECT_INC,
        INPUT_SELECT_DEC,
        INPUT_CHAT,
        INPUT_INVENTORY
    }

    private static String[] keyMappings = new String[InputAction.values().length];

    public InputProcessor(Main main) {
        this.main = main;
        int index = 0;
        for (InputAction action : InputAction.values()) {
            keyMappings[index] = action.name();
            index++;
        }
    }

    public void init(InputManager inputManager, MouseInput mouseInput) {

        inputManager.addMapping(INPUT_STRAFE_LEFT.name(), new KeyTrigger(ClientSettings.getKey("left", KeyInput.KEY_A)));
        inputManager.addMapping(INPUT_STRAFE_RIGHT.name(), new KeyTrigger(ClientSettings.getKey("right", KeyInput.KEY_D)));
        inputManager.addMapping(INPUT_MOVE_FORWARD.name(), new KeyTrigger(ClientSettings.getKey("forward", KeyInput.KEY_W)));
        inputManager.addMapping(INPUT_MOVE_BACKWARD.name(), new KeyTrigger(ClientSettings.getKey("backward", KeyInput.KEY_S)));
        inputManager.addMapping(INPUT_CHAT.name(), new KeyTrigger(ClientSettings.getKey("chat", KeyInput.KEY_T)));
        inputManager.addMapping(INPUT_JUMP.name(), new KeyTrigger(ClientSettings.getKey("jump", KeyInput.KEY_SPACE)));
        inputManager.addMapping(INPUT_TAB.name(), new KeyTrigger(ClientSettings.getKey("tab", KeyInput.KEY_TAB)));
        inputManager.addMapping(INPUT_INVENTORY.name(), new KeyTrigger(ClientSettings.getKey("inventory", KeyInput.KEY_I)));
        inputManager.addMapping(INPUT_CRAWL.name(), new KeyTrigger(ClientSettings.getKey("crawl", KeyInput.KEY_LCONTROL)));
        inputManager.addMapping(INPUT_CAM_LEFT.name(), new MouseAxisTrigger(mouseInput.AXIS_X, true), new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping(INPUT_CAM_RIGHT.name(), new MouseAxisTrigger(mouseInput.AXIS_X, false), new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping(INPUT_CAM_UP.name(), new MouseAxisTrigger(mouseInput.AXIS_Y, false), new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping(INPUT_CAM_DOWN.name(), new MouseAxisTrigger(mouseInput.AXIS_Y, true), new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping(INPUT_LEFT_CLICK.name(), new MouseButtonTrigger(mouseInput.BUTTON_LEFT));
        inputManager.addMapping(INPUT_RIGHT_CLICK.name(), new MouseButtonTrigger(mouseInput.BUTTON_RIGHT));
        inputManager.addMapping(INPUT_SELECT_INC.name(), new MouseAxisTrigger(MouseInput.AXIS_WHEEL,false));
        inputManager.addMapping(INPUT_SELECT_DEC.name(), new MouseAxisTrigger(MouseInput.AXIS_WHEEL,true));


        inputManager.addListener(this, keyMappings);
        inputManager.addRawInputListener(this);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equalsIgnoreCase("Pause") && isPressed) {

        } else if (name.equalsIgnoreCase(INPUT_STRAFE_LEFT.name())) {
            leftAction = isPressed;
        } else if (name.equalsIgnoreCase(INPUT_STRAFE_RIGHT.name())) {
            rightAction = isPressed;
        } else if (name.equalsIgnoreCase(INPUT_MOVE_FORWARD.name())) {
            forwardAction = isPressed;
        } else if (name.equalsIgnoreCase(INPUT_MOVE_BACKWARD.name())) {
            backAction = isPressed;
        } else if (name.equalsIgnoreCase(INPUT_JUMP.name())) {
            jumpAction = isPressed;
        } else if (name.equalsIgnoreCase(INPUT_TAB.name()) && isPressed) {
            main.switchMode();
        } else if (name.equalsIgnoreCase(INPUT_INVENTORY.name()) && isPressed) {
            inventoryMode = !inventoryMode;
            mouseTrapped = inventoryMode;
            main.getInventoryGui().hide(!inventoryMode);
        } else if (name.equalsIgnoreCase(INPUT_CRAWL.name())) {
            main.getPlayer().setCrawling(isPressed);
        } else if (name.equalsIgnoreCase(INPUT_LEFT_CLICK.name())) {
            leftClick = isPressed;
        } else if (name.equalsIgnoreCase(INPUT_RIGHT_CLICK.name())) {
            rightClick = isPressed;
        } else if (name.equalsIgnoreCase(INPUT_CHAT.name()) && isPressed) {
            chatMode = !chatMode;
        }
    }

    @Override
    public void onAnalog(String name, float value, float tps) {

        if (name.equalsIgnoreCase(INPUT_CAM_LEFT.name())) {
            main.rotateCamera(value, Vector3f.UNIT_Y);
        } else if (name.equalsIgnoreCase(INPUT_CAM_RIGHT.name())) {
            main.rotateCamera(-value, Vector3f.UNIT_Y);
        } else if (name.equalsIgnoreCase(INPUT_CAM_UP.name())) {
            main.rotateCamera(-value, main.getCamera().getLeft());
        } else if (name.equalsIgnoreCase(INPUT_CAM_DOWN.name())) {
            main.rotateCamera(value, main.getCamera().getLeft());
        } else if (name.equalsIgnoreCase(INPUT_SELECT_INC.name())) {
            main.wheelSelect(value);
        } else if (name.equalsIgnoreCase(INPUT_SELECT_DEC.name())) {
            main.wheelSelect(-value);
        }
    }

    boolean isMouseTrapped() {
        return mouseTrapped;
    }

    public boolean leftAction() {
        return leftAction;
    }

    public boolean rightAction() {
        return rightAction;
    }

    public boolean forwardAction() {
        return forwardAction;
    }

    public boolean backAction() {
        return backAction;
    }

    public boolean jumpAction() {
        return jumpAction;
    }

    public boolean leftClick() {
        return leftClick;
    }

    public boolean rightClick() {
        return rightClick;
    }

    public void eatLeftClick() {
        leftClick = false;
    }

    public void eatRightClick() {
        rightClick = false;
    }

    public boolean isInChatMode() {
        return chatMode;
    }

    public boolean isInInventoryMode() {
        return inventoryMode;
    }

    public boolean isInGameMode() {
        return !(chatMode || inventoryMode);
    }

    @Override
    public void beginInput() {
    }

    @Override
    public void endInput() {
    }

    @Override
    public void onJoyAxisEvent(JoyAxisEvent evt) {
    }

    @Override
    public void onJoyButtonEvent(JoyButtonEvent evt) {
    }

    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt) {
        if (inventoryMode) {
            main.getInventoryGui().mouseMove(evt.getX(), evt.getY());
        }
    }

    @Override
    public void onMouseButtonEvent(MouseButtonEvent evt) {
        if (inventoryMode) {
            main.getInventoryGui().mouseClick(evt.getX(), evt.getY());
        }
    }

    @Override
    public void onKeyEvent(KeyInputEvent event) {
        if (chatMode) {
            if (event.getKeyCode() == KeyInput.KEY_BACK) {
                main.getChatBar().deleteChar();
            } else if (event.getKeyCode() == KeyInput.KEY_RETURN) {
                main.getChatBar().enterLine();
            } else {
                main.getChatBar().pushChar(event.getKeyChar());
            }
        }
    }

    @Override
    public void onTouchEvent(TouchEvent evt) {
    }

}
