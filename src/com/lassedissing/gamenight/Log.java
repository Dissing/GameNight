/* Copyright 2014 Lasse Dissing
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.lassedissing.gamenight;

import java.io.Console;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Log {
    
    private static Console console;
    private static Calendar calendar;
    private static SimpleDateFormat sdf;
    
    public enum Level {
        DEBUG,
        INFO,
        WARNING,
        ERROR
    }
    
    public static void setConsole(Console console) {
        Log.console = console;
        calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("HH:mm:ss");
    }
    
    public static void DEBUG(String format, Object... arguments) {
        print(format,Level.DEBUG,arguments);
    }
    
    public static void INFO(String format, Object... arguments) {
        print(format,Level.INFO,arguments);
    }
            
    public static void WARNING(String format, Object... arguments) {
        print(format,Level.WARNING,arguments);
    }
    
    public static void ERROR(String format, Object... arguments) {
        print(format,Level.ERROR,arguments);
    }
    
    private static void print(String format, Level level, Object arguments) {
        format = format.replaceAll("%d", "%s");
        String fullClassName = Thread.currentThread().getStackTrace()[3].getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
        int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();
        String prefix = String.format("[%s] [%s:%d/%s]:", sdf.format(calendar.getTime()).toString(), className, lineNumber, level.name());
        console.printf(prefix + format +'\n', arguments);
    }

}
