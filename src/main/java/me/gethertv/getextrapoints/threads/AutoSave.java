package me.gethertv.getextrapoints.threads;

import me.gethertv.getextrapoints.GetExtraPoints;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

public class AutoSave extends BukkitRunnable {

    @Override
    public void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers())
                {
                    GetExtraPoints.getInstance().getSql().updatePlayer(player);
                }
            }
        }.runTaskAsynchronously(GetExtraPoints.getInstance());
    }
}
