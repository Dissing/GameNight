/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client.gui;

import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;


public class InfoBar extends GuiElement {

    private Node node;
    private BitmapText timeLeft;
    private int prevTime;
    private float time;
    private boolean enabled;

    public InfoBar(GuiContext context) {
        super(context);
        timeLeft = new BitmapText(context.getFont(),false);
        timeLeft.setSize(context.getFont().getCharSet().getRenderedSize()*2);
        timeLeft.setColor(ColorRGBA.White);
        timeLeft.setText(getPrettyTime(time));
        prevTime = (int)time;
        node = new Node();
        node.attachChild(timeLeft);
        node.setLocalTranslation(context.getWidth()/2 - timeLeft.getLineWidth()/2, 700, 0);
        context.getNode().attachChild(node);
    }

    public String getPrettyTime(float time) {
        int seconds = (int)time;
        return "" + seconds / 60 + ":" + seconds % 60;
    }

    @Override
    public void tick(float tpf) {
        if (enabled) {
            time -= tpf;
            if (((int)time) > prevTime) {
                timeLeft.setText(getPrettyTime(time));
            }
        }
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void enable(boolean enable) {
        enabled = enable;
    }

    @Override
    public void hide(boolean enable) {
        node.setCullHint(enable ? Spatial.CullHint.Always : Spatial.CullHint.Never);
    }

}
