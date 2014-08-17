/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.networking;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class NewUserMessage extends AbstractMessage {
    
    public int playerId;
    
    public NewUserMessage() {
        
    }
    
    public NewUserMessage(int playerId) {
        this.playerId = playerId;
    }

}
