/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client.gui;

import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;


public class StatBar extends GuiElement {

     private BitmapText healthBar;
     private int health;
     private int loadedAmmo;
     private int reloadAmmo;

    public StatBar(GuiContext context, int initialHealth) {
        super(context);
        healthBar = new BitmapText(context.getFont(),false);
        healthBar.setSize(context.getFont().getCharSet().getRenderedSize());
        healthBar.setColor(ColorRGBA.White);
        healthBar.setLocalTranslation(10, 700, 0);
        healthBar.setText("Health: " + initialHealth);
        context.getNode().attachChild(healthBar);
    }

    @Override
    public void tick(float tpf) {

    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setLoadedAmmo(int loadedAmmo) {
        this.loadedAmmo = loadedAmmo;
    }

    public void setReloadAmmo(int reloadAmmo) {
        this.reloadAmmo = reloadAmmo;
    }

}