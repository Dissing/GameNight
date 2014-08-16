/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.lassedissing.gamenight.world.Chunk;
import java.io.IOException;

public class Main extends SimpleApplication {
    
    Client client;

    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1280, 720);
        //settings.setRenderer(AppSettings.LWJGL_OPENGL3);
        Main app = new Main();
        app.setSettings(settings);
        app.setShowSettings(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {
       
        
        Chunk chunk = new Chunk();
        chunk.create(true);
        chunk.buildMesh();
        Geometry geom = new Geometry("Chunk",chunk);

        Material mat = new Material(assetManager, "MatDefs/Block.j3md");
        mat.getAdditionalRenderState().setWireframe(false);
        
        Texture texAtlas = assetManager.loadTexture("Textures/TextureAtlas.png");
        mat.setTexture("Atlas", texAtlas);
        texAtlas.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        texAtlas.setMagFilter(Texture.MagFilter.Nearest);
        
        geom.setMaterial(mat);
        
        rootNode.attachChild(geom);
    }

    @Override
    public void simpleUpdate(float tpf) {
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
