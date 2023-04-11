package me.gethertv.getextrapoints.listeners;

import me.gethertv.getextrapoints.GetExtraPoints;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KillPlayerListener implements Listener {

    @EventHandler
    public void onKillPlayer(PlayerDeathEvent event)
    {
        if(!GetExtraPoints.ENABLE_KILL)
            return;

        if(!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        if(player.getKiller() instanceof Player)
        {
            Player killer = (Player) player.getKiller();
            GetExtraPoints.getInstance().getUserData().get(killer.getUniqueId()).killUser(player);
        }
    }
}
