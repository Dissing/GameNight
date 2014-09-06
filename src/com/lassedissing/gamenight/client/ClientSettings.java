/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight.client;

import com.jme3.input.KeyInput;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;


public class ClientSettings {

    private static volatile Map<String, String> settingsMap = new HashMap<>();


    public static void init(File configFile) {
        if (!configFile.exists()) return;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replaceAll(" ", "");
                String parts[] = line.split(":");
                settingsMap.put(parts[0].trim().toLowerCase(), parts[1].trim().toLowerCase());
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean has(String key) {
        return settingsMap.containsKey(key);
    }

    public static int getInt(String key, int defaultValue) {
        if (!has(key)) return defaultValue;
        try {
            return Integer.parseInt(settingsMap.get(key));
        } catch (NumberFormatException e) {
            System.out.println("Invalid config value for " + key);
            return defaultValue;
        }
    }

    public static int getKey(String key, int defaultValue) {
        try {
            String trigger = get(key,"");
            if (!trigger.isEmpty()) {
                return KeyInput.class.getDeclaredField("KEY_" + trigger.toUpperCase()).getInt(null);
            } else {
                return defaultValue;
            }
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static String get(String key, String defaultValue) {
        if (!has(key)) return defaultValue;
        return settingsMap.get(key);
    }
}
