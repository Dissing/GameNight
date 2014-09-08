/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client.gui;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.scene.Node;


public class GuiContext {

    private Node node;
    private BitmapFont font;
    private int width;
    private int height;
    private AssetManager assetManager;

    public GuiContext(Node node, BitmapFont font, AssetManager assetManager, int width, int height) {
        this.node = node;
        this.font = font;
        this.width = width;
        this.height = height;
        this.assetManager = assetManager;
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
        return assetManager;
    }


}
