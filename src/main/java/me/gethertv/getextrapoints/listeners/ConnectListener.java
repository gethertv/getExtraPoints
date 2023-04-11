package me.gethertv.getextrapoints.listeners;

import me.gethertv.getextrapoints.GetExtraPoints;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ConnectListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        new BukkitRunnable() {

            @Override
            public void run() {
                GetExtraPoints.getInstance().getSql().loadPlayer(player);
            }
        }.runTaskAsynchronously(GetExtraPoints.getInstance());

    }

    @EventHandler
    public void onJoin(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                GetExtraPoints.getInstance().getSql().leavePlayer(player);
            }
        }.runTaskAsynchronously(GetExtraPoints.getInstance());

    }
}
