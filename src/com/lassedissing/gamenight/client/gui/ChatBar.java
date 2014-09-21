/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client.gui;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import java.util.LinkedList;
import java.util.List;


public class ChatBar extends GuiElement {

    private static final int lines = 8;
    private Node node = new Node();
    private BitmapText[] lineNodes = new BitmapText[8];
    private List<String> strings = new LinkedList<>();
    private StringBuilder lineBuffer = new StringBuilder();
    private static final String PROMPT = "> ";
    private Quad background;

    public ChatBar(GuiContext context) {
        super(context);

        for (int i = 0; i < 8; i++) {
            setupLine(i);
        }
        node.setLocalTranslation(10, 300, 0);

        context.getNode().attachChild(node);
    }

    private void setupLine(int i) {
        lineNodes[i] = new BitmapText(context.getFont(),false);
        lineNodes[i].setSize(context.getFont().getCharSet().getRenderedSize()*2);
        lineNodes[i].setColor(ColorRGBA.White);
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
            lineBuffer.deleteCharAt(lineBuffer.length()-1);
            showLineBuffer();
        }
    }

    public void enterLine() {
        pushLine("Line: " + lineBuffer.toString());
        lineBuffer.delete(0, lineBuffer.length()-1);
        showLineBuffer();
    }

    public void pushLine(String line) {
        for (int i = lines; i > 1; i--) {
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
