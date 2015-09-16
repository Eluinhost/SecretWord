package gg.uhc.secretword;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class PluginConfigurationProvider {

    protected final FileConfiguration configuration;
    protected final Plugin plugin;

    protected String currentWord;
    protected int secondsBeforeKick;
    protected int kicksBeforeBan;

    public PluginConfigurationProvider(Plugin plugin) {
        this.plugin = plugin;
        this.configuration = plugin.getConfig();

        this.currentWord = configuration.getString("secret word");
        this.secondsBeforeKick = configuration.getInt("seconds before kick");
        this.kicksBeforeBan = configuration.getInt("kicks before ban");
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public int getSecondsBeforeKick() {
        return secondsBeforeKick;
    }

    public int getKicksBeforeBan() {
        return kicksBeforeBan;
    }

    public void setCurrentWord(String word) {
        currentWord = word;
        configuration.set("secret word", currentWord);
        plugin.saveConfig();
    }
}
