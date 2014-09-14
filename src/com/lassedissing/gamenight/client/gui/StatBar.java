/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client.gui;

import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.lassedissing.gamenight.world.weapons.RangedWeapon;
import com.lassedissing.gamenight.world.weapons.Weapon;


public class StatBar extends GuiElement {

    private Node node;
    private BitmapText healthBar;
    private BitmapText roundsBar;
    private int health;

    public StatBar(GuiContext context, int initialHealth) {
        super(context);
        healthBar = new BitmapText(context.getFont(),false);
        healthBar.setSize(context.getFont().getCharSet().getRenderedSize()*2);
        healthBar.setColor(ColorRGBA.White);

        roundsBar = new BitmapText(context.getFont(),false);
        roundsBar.setSize(context.getFont().getCharSet().getRenderedSize()*2);
        roundsBar.setColor(ColorRGBA.White);
        roundsBar.setLocalTranslation(0, 30, 0);

        node = new Node();
        node.attachChild(healthBar);
        node.attachChild(roundsBar);
        node.setLocalTranslation(1050,60,0);

        context.getNode().attachChild(node);
    }

    @Override
    public void tick(float tpf) {

    }

    public void setHealth(int health) {
        this.health = health;
        healthBar.setText("Health: " + health);
    }

    public void updateWeapon(Weapon weapon) {
        if (weapon instanceof RangedWeapon) {
            RangedWeapon ranged = (RangedWeapon) weapon;
            roundsBar.setText("Ammo: " + ranged.getRoundsInMag() + "/" + ranged.getMaxRoundsInMag() + "-" + ranged.getMags());
        }
    }

    @Override
    public void hide(boolean enable) {
        node.setCullHint(enable ? Spatial.CullHint.Always : Spatial.CullHint.Never);
    }
}
