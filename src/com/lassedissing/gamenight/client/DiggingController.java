/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client;

import com.jme3.math.Vector3f;
import com.lassedissing.gamenight.messages.BlockChangeMessage;
import com.lassedissing.gamenight.world.BlockInfo;


public class DiggingController {

    private float diggingTime;
    private float totalBlockDiggingTime;
    private Vector3f currentDiggingBlock = new Vector3f(-1, -1, -1);

    private void dig(Vector3f selectedBlock, Main app, float tpf) {
        if (selectedBlock.equals(currentDiggingBlock)) {
            diggingTime += tpf;
            if (diggingTime > totalBlockDiggingTime) {
                app.client.send(new BlockChangeMessage(0, selectedBlock));
            }
        } else {
            resetDigging();
            currentDiggingBlock.set(selectedBlock);
            totalBlockDiggingTime = BlockInfo.getDuration(app.chunkManager.getId(selectedBlock),0.5f);
        }
    }

    private void resetDigging() {
        diggingTime = 0;
    }

    private void placeBlock(Main app) {
        Vector3f selectedBlock = app.chunkManager.getPickedBlock(app.getCamera().getLocation(), app.getCamera().getDirection(), 5f, true);
        boolean blocked = false;
        blocked = app.player.isColliding(app.player.getLocation().add(0, 0.1f, 0), selectedBlock);
        for (PlayerView other : app.players.values()) {
            if (blocked) break;
            blocked = app.player.isColliding(other.getPosition().add(0, 0.1f, 0), selectedBlock);
        }
        int type = app.buildBar.getSelectedSlot();
        if (!blocked && app.chunkManager.getId(selectedBlock) == 0 && selectedBlock.y < 30 && type != 0) {
            app.client.send(new BlockChangeMessage(type, selectedBlock));
            app.inputProcessor.eatRightClick();
        }
    }

    public void tick(Main app, float tpf) {

        Vector3f selectedBlock = app.chunkManager.getPickedBlock(app.getCamera().getLocation(), app.getCamera().getDirection(), 5f, false);

        if (selectedBlock != null) {
            app.chunkManager.showSelectBlock((int)selectedBlock.x, (int)selectedBlock.y, (int)selectedBlock.z);


            if (app.inputProcessor.leftClick()) {
                dig(selectedBlock,app, tpf);
            } else {
                resetDigging();
            }

            if (app.inputProcessor.rightClick()) {
                placeBlock(app);
            }

        } else {
            resetDigging();
        }
    }
}
