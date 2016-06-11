package com.gmail.scyntrus.fmob;

import com.google.common.base.Charsets;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Messages {

    public enum Message {

        FM_HELP("fm.help");

        private final String key;
        Message(String key) {
            this.key = key;
        }
        public String getKey() {
            return key;
        }
    }

    private static YamlConfiguration yml;
    private static boolean initialized = false;

    public static void init(Plugin plugin) {
        if (initialized)
            return;
        yml = new YamlConfiguration();
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (file.isFile()) {
            try {
                yml.load(file);
            } catch (Exception e) {
                ErrorManager.handleError(e);
            }
        }
        InputStream res = Messages.class.getResourceAsStream("/messages.yml");
        if(res != null) {
            yml.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(res, Charsets.UTF_8)));
        }
        yml.options().copyDefaults(true);
        try {
            yml.save(file);
        } catch (IOException e) {
            ErrorManager.handleError(e);
        }
        initialized = true;
    }

    public static String get(Message message, Object... args) {
        if (!initialized) {
            ErrorManager.handleError("Messages library accessed before initialization.", new Exception());
            return "";
        }
        String raw = yml.getString(message.getKey());
        if (raw == null) {
            ErrorManager.handleError("Message with no data.", new Exception());
            return "";
        }
        return String.format(raw, args);
    }
}
