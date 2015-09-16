package gg.uhc.secretword;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Entry extends JavaPlugin {

    public void onEnable() {
        // copy defaults to save the defaults to the file if they are missing
        getConfig().options().copyDefaults(true);
        saveConfig();

        PluginConfigurationProvider configurationProvider = new PluginConfigurationProvider(this);
        SecretWordListener listener = new SecretWordListener(this, configurationProvider);

        Bukkit.getPluginManager().registerEvents(listener, this);
        getCommand("secretword").setExecutor(new SetSecretWordCommand(configurationProvider));
    }
}
