package me.gethertv.getextrapoints.threads;

import me.gethertv.getextrapoints.GetExtraPoints;
import me.gethertv.getextrapoints.utils.ColorFixer;
import org.bukkit.scheduler.BukkitRunnable;

public class MoneyAuto extends BukkitRunnable {

    @Override
    public void run() {

        GetExtraPoints.getInstance().getUserData().forEach(((uuid, user) -> {
            if(user.getNextMoney()<=System.currentTimeMillis())
            {
                user.setNextMoney(System.currentTimeMillis()+(1000L*GetExtraPoints.COOLDOWN_MONEY));
                user.setBalance(user.getBalance()+GetExtraPoints.VALUE_MONEY);
                user.getPlayer().sendMessage(ColorFixer.addBalance(GetExtraPoints.getInstance().getConfig().getString("lang.get-money"), String.format("%.2f" ,GetExtraPoints.VALUE_KILL)));
            }
        }));
    }
}
