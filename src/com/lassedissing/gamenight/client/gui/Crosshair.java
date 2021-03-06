/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client.gui;

import com.jme3.font.BitmapText;
import com.jme3.scene.Spatial;


public class Crosshair extends GuiElement {

    BitmapText crosshair;

    public Crosshair(GuiContext context) {
        super(context);
        crosshair = new BitmapText(context.getFont(), false);
        crosshair.setSize(context.getFont().getCharSet().getRenderedSize() * 2);
        crosshair.setText("+");
        crosshair.setLocalTranslation(
                context.getWidth() / 2 - crosshair.getLineWidth()/2,
                context.getHeight() / 2 + crosshair.getLineHeight()/2, 0);
        context.getNode().attachChild(crosshair);
    }

    @Override
    public void tick(float tpf) {
        return;
    }

    @Override
    public void hide(boolean enable) {
        crosshair.setCullHint(enable ? Spatial.CullHint.Always : Spatial.CullHint.Never);
    }

}
