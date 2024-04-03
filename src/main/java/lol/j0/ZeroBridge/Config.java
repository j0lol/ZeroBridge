package lol.j0.ZeroBridge;

import blue.endless.jankson.Comment;
import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Config {
    @Comment("Your Discord token")
    String token = "CHANGEME";

    @Comment("Guild ID")
    String guild = "CHANGEME";

    @Comment("Channel ID")
    String channel = "CHANGEME";

    @Comment("Whether to use webhooks or not")
    boolean webhook = true;

    @Comment("Strings to use when displaying events.")
    StringsConfig strings = new StringsConfig();

    public static class StringsConfig {
        public String server_start = "Server Started";
        public String server_stop = "Server Stopped";
        public String discord_message_template = "D [{USER}] {MSG}";
        public String player_join =  "{USER} joined the game";
        public String player_leave = "{USER} left the game";
    }

    @Comment("Bot settings")
    BotConfig bot = new BotConfig();
    public static class BotConfig {
        public String avatar_url;
        public String nickname;
    }

    public Config loadConfig() {
        Jankson jankson = Jankson.builder()
                .build();

        try {
            File configFile = new File("config/zerobridge.json");
            JsonObject configJson = jankson.load(configFile);
            // Convert the raw object into your POJO type
            return jankson.fromJson(configJson, Config.class);
        } catch (IOException | SyntaxError e) {
            DiscordBridge.log.info("No config. Making config.");
            saveConfig();
            DiscordBridge.log.trace(e.toString());
            e.printStackTrace();
            throw new RuntimeException("No config file for ZeroBridge. See README.");
        }
    }

    public File saveConfig() {
        File configFile = new File("config/zerobridge.json");
        Jankson jankson = Jankson.builder().build();
        String result = jankson
                .toJson(this)        // The first call makes a JsonObject
                .toJson(true, true); // The second turns the JsonObject into a String
        try {
            boolean fileIsUsable = configFile.exists() || configFile.createNewFile();
            if (!fileIsUsable) return null;
            FileOutputStream out = new FileOutputStream(configFile, false);

            out.write(result.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return configFile;
    }
}