package gg.uhc.secretword;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Calendar;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

public class SecretWordListener implements Listener {

    protected static final String IMMUNE_PERMISSION = "uhc.secretword.immune";

    protected final Plugin plugin;
    protected final PluginConfigurationProvider configuration;

    // both are potentially used in async areas
    protected Set<UUID> waiting = new ConcurrentSkipListSet<UUID>();
    protected Set<UUID> provided = new ConcurrentSkipListSet<UUID>();

    // keep track of kick counts, only ran on main thread
    protected Multiset<UUID> violations = HashMultiset.create();

    public SecretWordListener(Plugin plugin, PluginConfigurationProvider configuration) {
        this.plugin = plugin;
        this.configuration = configuration;
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        UUID joiningUUID = event.getPlayer().getUniqueId();

        // skip immunes
        if (event.getPlayer().hasPermission(IMMUNE_PERMISSION)) return;

        // skip players who have already provided the word
        if (provided.contains(joiningUUID)) return;

        // add this player to the waiting list and fire task
        waiting.add(joiningUUID);

        event.getPlayer().sendMessage(ChatColor.AQUA + "Please enter the secret word for this game in chat within " + configuration.getSecondsBeforeKick() + " otherwise you will be kicked");

        // start a task that will add violations/kick as needed
        WaitForMessageTask task = new WaitForMessageTask(joiningUUID);
        task.runTaskLater(plugin, configuration.getSecondsBeforeKick() * 20);
    }

    @EventHandler
    public void on(AsyncPlayerChatEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();

        // allow any regular message through
        if (!waiting.contains(event.getPlayer().getUniqueId())) return;

        // cancel chats of waiting players
        event.setCancelled(true);

        // check word
        if (event.getMessage().equalsIgnoreCase(configuration.getCurrentWord())) {
            // sent a correct name, remove from waiting list and and to provided list
            waiting.remove(playerUUID);
            provided.add(playerUUID);
            event.getPlayer().sendMessage(ChatColor.GOLD + "Thank you for providing the correct word, welcome to the server");
        } else {
            event.getPlayer().sendMessage(ChatColor.RED + "That word is not correct, please try again");
        }
    }

    protected class WaitForMessageTask extends BukkitRunnable {

        protected final UUID uuid;

        public WaitForMessageTask(UUID uuid) {
            this.uuid = uuid;
        }

        public void run() {
            // if they have not provided the word by the time this runs
            if (!provided.contains(uuid)) {
                violations.add(uuid);

                // check if they've reached a cap of kicks
                int kicksToBan = configuration.getKicksBeforeBan();

                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                String message = "You failed to provide the secret word in time";

                if (kicksToBan > 0 && violations.count(uuid) >= kicksToBan) {
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.HOUR, 24);

                    // add a ban
                    String banMessage = "Not providing the correct secret word " + kicksToBan + " times.";
                    message = "You were banned for 24h for: " + banMessage;
                    Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), banMessage, c.getTime(), "SecretWord Plugin");

                    // clear their violations
                    violations.setCount(uuid, 0);
                }

                // if they are online kick them
                if (player.isOnline()) {
                    player.getPlayer().kickPlayer(message);
                }
            }
        }
    }
}
