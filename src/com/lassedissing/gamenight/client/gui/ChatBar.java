/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client.gui;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.lassedissing.gamenight.messages.ChatMessage;
import java.util.LinkedList;
import java.util.List;


public class ChatBar extends GuiElement {

    private static final int LINES = 16;
    private Node node = new Node();
    private BitmapText[] lineNodes = new BitmapText[LINES];
    private List<String> strings = new LinkedList<>();
    private StringBuilder lineBuffer = new StringBuilder();
    private static final String PROMPT = "> ";
    private Quad background;

    public ChatBar(GuiContext context) {
        super(context);

        for (int i = 0; i < LINES; i++) {
            setupLine(i);
        }
        node.setLocalTranslation(10, 50, 0);

        Geometry backplane = new Geometry("ChatBackplane", new Quad(400,250));
        Material backplaneMat = new Material(context.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
        backplaneMat.setColor("Color", new ColorRGBA(0, 0, 0, 0.5f));
        backplaneMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        backplane.setMaterial(backplaneMat);
        backplane.setLocalTranslation(0, -40, -1);
        node.attachChild(backplane);

        context.getNode().attachChild(node);
    }

    private void setupLine(int i) {
        lineNodes[i] = new BitmapText(context.getFont(),false);
        lineNodes[i].setSize(context.getFont().getCharSet().getRenderedSize());
        lineNodes[i].setColor(ColorRGBA.White);
        lineNodes[i].setLocalTranslation(0,15*i,0);
        if (i == 0) {
            lineNodes[0].setText(PROMPT);
        }
        node.attachChild(lineNodes[i]);
    }

    public void pushChar(char c) {
        lineBuffer.append(c);
        showLineBuffer();
    }

    public void deleteChar() {
        if (lineBuffer.length()> 0) {
            lineBuffer.deleteCharAt(lineBuffer.length());
            showLineBuffer();
        }
    }

    public void enterLine() {
        if (lineBuffer.length() > 0) {
            context.getMain().getClient().send(new ChatMessage(context.getMain().getClientId(), lineBuffer.toString().trim()));
            lineBuffer.delete(0, lineBuffer.length()-1);
            showLineBuffer();
        }
    }

    public void pushLine(String line) {
        for (int i = LINES; i > 1; i--) {
            lineNodes[i-1].setText(lineNodes[i-2].getText());
        }
        lineNodes[1].setText(line);
    }

    private void showLineBuffer() {
        lineNodes[0].setText(PROMPT + lineBuffer.toString());
    }

    @Override
    public void tick(float tpf) {
    }

    @Override
    public void hide(boolean enable) {
        node.setCullHint(enable ? Spatial.CullHint.Always : Spatial.CullHint.Never);
    }


}
