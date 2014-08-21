/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;


public class PlayerBox {
    
    private Vector3f location = new Vector3f();
    private Vector3f eye = new Vector3f(0.4f,1.6f,0.4f);
    private Vector3f width = new Vector3f(0.8f,1.8f,0.8f);
    private float speed = 0.1f;
    
    public PlayerBox() {
        
    }

    public void move(Vector3f moveDirection) {
        location.addLocal(moveDirection.mult(speed));
    }
    
    public void setLocation(Vector3f location) {
        this.location = location;
    }
    
    public void tick(Camera cam) {
        cam.setLocation(location.add(eye));
    }
    
}
