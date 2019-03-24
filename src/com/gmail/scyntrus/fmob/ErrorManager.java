package com.gmail.scyntrus.fmob;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ErrorManager {

    private static PrintWriter errorStream;

    public static void handleError(String message, Exception e) {
        handleError(message);
        handleError(e);
    }

    public static void initErrorStream() {
        if (errorStream == null) {
            try {
                errorStream = new PrintWriter(new BufferedWriter(new FileWriter(new File(FactionMobs.instance.getDataFolder(), "error.log"), true)));
            } catch (IOException e1) {
                FactionMobs.instance.getServer().getConsoleSender().sendMessage(colorChar + "c[FactionMobs] Could not write to error.log file. " +
                        "Defaulting to spamming errors in the console.");
                FactionMobs.silentErrors = false;
            }
        }
    }

    public static DateFormat dateFormat = new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss] ");
    private static final char colorChar = Character.toChars(167)[0];

    public static void handleError(String message) {
        if (message == null) {
            return;
        }
        FactionMobs.instance.getServer().getConsoleSender().sendMessage(colorChar + "c[FactionMobs] " + message);
        if (errorStream != null) {
            errorStream.print(dateFormat.format(new Date()));
            errorStream.println(message);
            errorStream.flush();
        }
    }

    public static void handleError(Throwable e) {
        if (e == null) {
            return;
        }
        if (!FactionMobs.silentErrors) {
            e.printStackTrace();
        }
        if (errorStream != null) {
            errorStream.print(dateFormat.format(new Date()));
            e.printStackTrace(errorStream);
            errorStream.flush();
        }
    }

    public static void closeErrorStream() {
        if (errorStream != null) {
            errorStream.close();
        }
        errorStream = null;
    }
}
