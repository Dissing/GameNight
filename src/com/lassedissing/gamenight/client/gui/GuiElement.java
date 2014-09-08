/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client.gui;


public abstract class GuiElement {

    protected GuiContext context;

    public GuiElement(GuiContext context) {
        this.context = context;
    }

    public abstract void tick(float tpf);

}
