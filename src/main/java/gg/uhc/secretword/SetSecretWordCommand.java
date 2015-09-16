package gg.uhc.secretword;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SetSecretWordCommand implements CommandExecutor {

    protected final PluginConfigurationProvider configuration;

    public SetSecretWordCommand(PluginConfigurationProvider configuration) {
        this.configuration = configuration;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String old = configuration.getCurrentWord();
        configuration.setCurrentWord(args[0]);

        sender.sendMessage(ChatColor.GOLD + "Changed secret word from " + old + " to " + args[0]);
        return true;
    }
}
