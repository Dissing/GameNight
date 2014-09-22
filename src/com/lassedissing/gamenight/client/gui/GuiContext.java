/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client.gui;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.scene.Node;
import com.lassedissing.gamenight.client.Main;


public class GuiContext {

    private Node node;
    private BitmapFont font;
    private int width;
    private int height;
    private Main main;

    public GuiContext(Main main, Node node, BitmapFont font, int width, int height) {
        this.main = main;
        this.node = node;
        this.font = font;
        this.width = width;
        this.height = height;
    }

    public Node getNode() {
        return node;
    }

    public BitmapFont getFont() {
        return font;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public AssetManager getAssetManager() {
        return main.getAssetManager();
    }

    public Main getMain() {
        return main;
    }

}
