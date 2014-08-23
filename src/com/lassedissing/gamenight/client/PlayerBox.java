/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;


public class PlayerBox {
    
    private Vector3f location = new Vector3f();
    private Vector3f velocityXZ = new Vector3f();
    private Vector3f velocityY = new Vector3f();
    private Vector3f prevLocation = new Vector3f();
    private Vector3f moveDirection = new Vector3f();
    private Vector3f eye = new Vector3f(0.45f,1.6f,0.45f);
    private Vector3f width = new Vector3f(0.9f,1.8f,0.9f);
    private Vector3f center = new Vector3f(0.45f,0, 0.45f);
    private float walkSpeed = 0.2f;
    private float friction = 0.1f;
    private boolean isOnGround = true;
    
    
    public PlayerBox() {
        
    }

    public void setLocation(Vector3f location) {
        this.location = location;
    }
    
    
    private boolean isColliding(Vector3f pos, Vector3f width, int blockX, int blockY, int blockZ) {
        if (pos.x+width.x < blockX || pos.x > blockX+1) return false;
        if (pos.y+width.y < blockY || pos.y > blockY+1) return false;
        if (pos.z+width.z < blockZ || pos.z > blockZ+1) return false;
        
        return true;
    }
    
    public void tick(Camera cam, Vector3f desiredDirection, ChunkManager manager) {
        
        //Step XZ
        
        if (isOnGround) {
            
            desiredDirection.y = 0;
            desiredDirection.normalizeLocal();
            desiredDirection.multLocal(walkSpeed);
            
            Vector3f velDiff = new Vector3f(desiredDirection);
            velDiff.subtractLocal(velocityXZ);
            velDiff.multLocal(friction);
            
            velocityXZ.x += velDiff.x;
            velocityXZ.z += velDiff.z;
            
            Vector3f newPos = new Vector3f(location);
            
            int cenX = (int) (newPos.x + center.x);
            int cenY = (int) (newPos.y + center.y);
            int cenZ = (int) (newPos.z + center.z);
            
            //Step X
            newPos.x += velocityXZ.x;
            int side = velocityXZ.x > 0 ? 1 : -1;
            
            int x1 = manager.getId(cenX+side, cenY, cenZ+1);
            int x2 = manager.getId(cenX+side, cenY, cenZ);
            int x3 = manager.getId(cenX+side, cenY, cenZ-1);
            
            boolean colX = 
                    ((x1 != 0 && isColliding(newPos, width, cenX+side, cenY, cenZ+1)) ||
                     (x2 != 0 && isColliding(newPos, width, cenX+side, cenY, cenZ)) ||
                     (x3 != 0 && isColliding(newPos, width, cenX+side, cenY, cenZ-1)));
            
            //Step Z
            newPos.z += velocityXZ.z;
            side = velocityXZ.z > 0 ? 1 : -1;
            
            int z1 = manager.getId(cenX+1, cenY, cenZ+side);
            int z2 = manager.getId(cenX, cenY, cenZ+side);
            int z3 = manager.getId(cenX-1, cenY, cenZ+side);
            
            boolean colZ = 
                    ((z1 != 0 && isColliding(newPos, width, cenX+1, cenY, cenZ+side)) ||
                     (z2 != 0 && isColliding(newPos, width, cenX, cenY, cenZ+side)) ||
                     (z3 != 0 && isColliding(newPos, width, cenX-1, cenY, cenZ+side)));
            
            if (colX && colZ) {
                if (Math.abs(cam.getDirection().getX()) > Math.abs(cam.getDirection().getZ())) {
                    colX = false;
                } else {
                    colZ = false;
                }
            }
            
            if (colX) {
                Vector3f collidingNormal = new Vector3f(-side,0,0);
                collidingNormal.multLocal(collidingNormal.dot(velocityXZ));
                velocityXZ.subtractLocal(collidingNormal);
            } else if (colZ) {
                Vector3f collidingNormal = new Vector3f(0,0,-side);
                collidingNormal.multLocal(collidingNormal.dot(velocityXZ));
                velocityXZ.subtractLocal(collidingNormal);
            } 
            
        }
        
        location.addLocal(velocityXZ);
        
        //Step Y
        
        cam.setLocation(location.add(eye));
    }
    
}
