/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;


public class PlayerController {

    private Vector3f location = new Vector3f();
    private Vector3f velocityXZ = new Vector3f();
    private Vector3f velocityY = new Vector3f();
    private Vector3f eye = new Vector3f(0.4f,1.6f,0.4f);
    private Vector3f width = new Vector3f(0.6f,1.8f,0.6f);
    private Vector3f crawlWidth = new Vector3f(0.8f,1.8f,0.8f);
    private Vector3f center = new Vector3f(0.4f,0, 0.4f);
    private float walkSpeed = 9f;
    private float friction = 15f;
    private float jumpVelocity = 12f;
    private boolean isOnGround = true;
    private Vector3f gravity = new Vector3f(0,-36f,0);
    private Vector3f prevLocation = new Vector3f();

    private boolean isCrawling;

    public PlayerController() {

    }

    public void setLocation(Vector3f location) {
        this.location = location;
    }

    public void setEyeLocation(Vector3f location) {
        this.location = location.subtract(eye);
    }

    public Vector3f getLocation() {
        return location;
    }

    public Vector3f getEyeLocation() {
        return location.add(eye);
    }


    public boolean isColliding(Vector3f playerPos, Vector3f blockPos) {
        return isColliding(playerPos, width, (int)blockPos.x, (int)blockPos.y, (int)blockPos.z);
    }

    public boolean isColliding(Vector3f pos, Vector3f width, int blockX, int blockY, int blockZ) {
        if (pos.x+width.x < blockX || pos.x > blockX+1) return false;
        if (pos.y+width.y < blockY || pos.y > blockY+1) return false;
        if (pos.z+width.z < blockZ || pos.z > blockZ+1) return false;

        return true;
    }

    public void jump() {
        if (isOnGround) {
            isOnGround = false;
            velocityY.y = jumpVelocity;
        }
    }

    public boolean hasMoved() {
        return !prevLocation.equals(location);
    }

    public void tick(Camera cam, Vector3f desiredDirection, ChunkManager manager, float tpf) {

        //Step XZ

        Vector3f newPos = new Vector3f(location);

        int cenX = (int) (newPos.x + center.x);
        int cenY = (int) (newPos.y + center.y);
        int cenZ = (int) (newPos.z + center.z);

        //Step Y

        velocityY.addLocal(gravity.mult(tpf));
        newPos.addLocal(velocityY.mult(tpf));

        int y0 = manager.getId(cenX, (int)newPos.y, cenZ);
        int y1 = manager.getId(cenX+1, (int)newPos.y, cenZ);
        int y2 = manager.getId(cenX, (int)newPos.y, cenZ+1);
        int y3 = manager.getId(cenX-1, (int)newPos.y, cenZ);
        int y4 = manager.getId(cenX, (int)newPos.y, cenZ-1);
        int y5 = manager.getId(cenX+1, (int)newPos.y, cenZ+1);
        int y6 = manager.getId(cenX+1, (int)newPos.y, cenZ-1);
        int y7 = manager.getId(cenX-1, (int)newPos.y, cenZ+1);
        int y8 = manager.getId(cenX-1, (int)newPos.y, cenZ-1);

        int y10 = manager.getId(cenX, (int)newPos.y+2, cenZ);
        int y11 = manager.getId(cenX+1, (int)newPos.y+2, cenZ);
        int y12 = manager.getId(cenX, (int)newPos.y+2, cenZ+1);
        int y13 = manager.getId(cenX-1, (int)newPos.y+2, cenZ);
        int y14 = manager.getId(cenX, (int)newPos.y+2, cenZ-1);

        boolean colGround =
                     ((y0 != 0 && isColliding(newPos, width, cenX, cenY, cenZ)) ||
                      (y1 != 0 && isColliding(newPos, width, cenX+1, cenY, cenZ)) ||
                      (y2 != 0 && isColliding(newPos, width, cenX, cenY, cenZ+1)) ||
                      (y3 != 0 && isColliding(newPos, width, cenX-1, cenY, cenZ)) ||
                      (y4 != 0 && isColliding(newPos, width, cenX, cenY, cenZ-1)) ||

                      (y5 != 0 && isColliding(newPos, width, cenX+1, cenY, cenZ+1)) ||
                      (y6 != 0 && isColliding(newPos, width, cenX+1, cenY, cenZ-1)) ||
                      (y7 != 0 && isColliding(newPos, width, cenX-1, cenY, cenZ+1)) ||
                      (y8 != 0 && isColliding(newPos, width, cenX-1, cenY, cenZ-1)));

        boolean colRoof =
                    ((y10 != 0 && isColliding(newPos, width, cenX, cenY+2, cenZ)) ||
                     (y11 != 0 && isColliding(newPos, width, cenX+1, cenY+2, cenZ)) ||
                     (y12 != 0 && isColliding(newPos, width, cenX, cenY+2, cenZ+1)) ||
                     (y13 != 0 && isColliding(newPos, width, cenX-1, cenY+2, cenZ)) ||
                     (y14 != 0 && isColliding(newPos, width, cenX, cenY+2, cenZ-1)));

        if (colGround) {
            isOnGround = true;
            velocityY.set(0, 0, 0);
            location.y = (float)Math.ceil(newPos.y);
        } else if (colRoof) {
            velocityY.set(0, 0, 0);
        } else {
            isOnGround = false;
        }
        location.addLocal(velocityY.mult(tpf));

        desiredDirection.y = 0;
        desiredDirection.normalizeLocal();
        desiredDirection.multLocal(walkSpeed * (isCrawling ? 0.5f : 1f));
        Vector3f velDiff = new Vector3f(desiredDirection);
        velDiff.subtractLocal(velocityXZ);

        if (isOnGround) {
            velDiff.multLocal(friction*tpf);
            velocityXZ.x += velDiff.x;
            velocityXZ.z += velDiff.z;

        } else if (desiredDirection.length() != 0) {
            velDiff.multLocal(friction*tpf/8f);
            velocityXZ.x += velDiff.x;
            velocityXZ.z += velDiff.z;
        }


        //Step X
        newPos.set(location);
        newPos.x += velocityXZ.x * tpf;
        int sideX = velocityXZ.x > 0 ? 1 : -1;

        int x1 = manager.getId(cenX+sideX, cenY, cenZ+1);
        int x2 = manager.getId(cenX+sideX, cenY, cenZ);
        int x3 = manager.getId(cenX+sideX, cenY, cenZ-1);
        int x4 = manager.getId(cenX+sideX, cenY+1, cenZ+1);
        int x5 = manager.getId(cenX+sideX, cenY+1, cenZ);
        int x6 = manager.getId(cenX+sideX, cenY+1, cenZ-1);
        int x7 = manager.getId(cenX+sideX, cenY+2, cenZ+1);
        int x8 = manager.getId(cenX+sideX, cenY+2, cenZ);
        int x9 = manager.getId(cenX+sideX, cenY+2, cenZ-1);


        boolean colX =
                ((x1 != 0 && isColliding(newPos, width, cenX+sideX, cenY, cenZ+1)) ||
                 (x2 != 0 && isColliding(newPos, width, cenX+sideX, cenY, cenZ)) ||
                 (x3 != 0 && isColliding(newPos, width, cenX+sideX, cenY, cenZ-1)) ||
                 (x4 != 0 && isColliding(newPos, width, cenX+sideX, cenY+1, cenZ+1)) ||
                 (x5 != 0 && isColliding(newPos, width, cenX+sideX, cenY+1, cenZ)) ||
                 (x6 != 0 && isColliding(newPos, width, cenX+sideX, cenY+1, cenZ-1)) ||
                 (x7 != 0 && isColliding(newPos, width, cenX+sideX, cenY+2, cenZ+1)) ||
                 (x8 != 0 && isColliding(newPos, width, cenX+sideX, cenY+2, cenZ)) ||
                 (x9 != 0 && isColliding(newPos, width, cenX+sideX, cenY+2, cenZ-1)));

        //Step Z
        newPos.set(location);
        newPos.z += velocityXZ.z * tpf;
        int sideZ = velocityXZ.z > 0 ? 1 : -1;

        int z1 = manager.getId(cenX+1, cenY, cenZ+sideZ);
        int z2 = manager.getId(cenX, cenY, cenZ+sideZ);
        int z3 = manager.getId(cenX-1, cenY, cenZ+sideZ);
        int z4 = manager.getId(cenX+1, cenY+1, cenZ+sideZ);
        int z5 = manager.getId(cenX, cenY+1, cenZ+sideZ);
        int z6 = manager.getId(cenX-1, cenY+1, cenZ+sideZ);
        int z7 = manager.getId(cenX+1, cenY+2, cenZ+sideZ);
        int z8 = manager.getId(cenX, cenY+2, cenZ+sideZ);
        int z9 = manager.getId(cenX-1, cenY+2, cenZ+sideZ);

        boolean colZ =
                ((z1 != 0 && isColliding(newPos, width, cenX+1, cenY, cenZ+sideZ)) ||
                 (z2 != 0 && isColliding(newPos, width, cenX, cenY, cenZ+sideZ)) ||
                 (z3 != 0 && isColliding(newPos, width, cenX-1, cenY, cenZ+sideZ)) ||
                 (z4 != 0 && isColliding(newPos, width, cenX+1, cenY+1, cenZ+sideZ)) ||
                 (z5 != 0 && isColliding(newPos, width, cenX, cenY+1, cenZ+sideZ)) ||
                 (z6 != 0 && isColliding(newPos, width, cenX-1, cenY+1, cenZ+sideZ))||
                 (z7 != 0 && isColliding(newPos, width, cenX+1, cenY+2, cenZ+sideZ)) ||
                 (z8 != 0 && isColliding(newPos, width, cenX, cenY+2, cenZ+sideZ)) ||
                 (z9 != 0 && isColliding(newPos, width, cenX-1, cenY+2, cenZ+sideZ)));

        if (colX && colZ) {
            if (Math.abs(cam.getDirection().getX()) > 0.97f && Math.abs(desiredDirection.x) > Math.abs(desiredDirection.z)) {
                colZ = false;
            } else if (Math.abs(cam.getDirection().getZ()) > 0.97f && Math.abs(desiredDirection.z) > Math.abs(desiredDirection.x)) {
                colX = false;
            } else {
            }
        }

        if (isCrawling && isOnGround) {
            if (manager.getId(cenX, cenY-1, cenZ) == 0) {
                boolean crawlX = manager.getId(cenX+sideX, cenY-1, cenZ) == 0 &&
                         (manager.getId(cenX-sideX, cenY-1, cenZ) != 0);
                boolean crawlZ = manager.getId(cenX, cenY-1, cenZ+sideZ) == 0 &&
                         (manager.getId(cenX, cenY-1, cenZ - sideZ) != 0);

                if (crawlX) {
                    colX = true;
                }
                if (crawlZ) {
                    colZ = true;
                }
                if (!(crawlX || crawlZ)) {
                    boolean crawlXZ = manager.getId(cenX, cenY-1, cenZ+sideZ) == 0 &&
                            ((manager.getId(cenX - sideX, cenY-1, cenZ - sideZ) != 0));
                    if (crawlXZ) {

                        colX = true;
                        colZ = true;
                    }
                }
            }
        }

        if (colX) {
            Vector3f collidingNormal = new Vector3f(-sideX,0,0);
            collidingNormal.multLocal(collidingNormal.dot(velocityXZ));
            velocityXZ.subtractLocal(collidingNormal);
        }
        if (colZ) {
            Vector3f collidingNormal = new Vector3f(0,0,-sideZ);
            collidingNormal.multLocal(collidingNormal.dot(velocityXZ));
            velocityXZ.subtractLocal(collidingNormal);
        }


        location.addLocal(velocityXZ.mult(tpf));
        prevLocation.set(location);
    }

    public void setCrawling(boolean isCrawling) {
        this.isCrawling = isCrawling;
    }

    public boolean isCrawling() {
        return isCrawling;
    }

}
