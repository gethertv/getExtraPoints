package me.gethertv.getextrapoints.data;

import me.gethertv.getextrapoints.GetExtraPoints;
import me.gethertv.getextrapoints.utils.ColorFixer;
import me.gethertv.getextrapoints.utils.Timer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class User {
    private Player player;
    private double balance;

    private long nextMoney;
    private HashMap<UUID, Long> lastKill;

    public User(Player player, double balance) {
        this.player = player;
        this.balance = balance;
        nextMoney = System.currentTimeMillis()+(1000L* GetExtraPoints.COOLDOWN_MONEY);
        lastKill = new HashMap<>();
    }

    public void killUser(Player death)
    {
        Long time = lastKill.get(death.getUniqueId());
        if(time==null)
        {
            addMoney(death);
            return;
        }
        if(time<=System.currentTimeMillis())
        {
            addMoney(death);
            return;
        }
        player.sendMessage(ColorFixer.addColor(GetExtraPoints.getInstance().getConfig().getString("lang.kill-cooldown").replace("{cooldown}", Timer.getTime(time))));

    }
    public void addMoney(Player death)
    {
        lastKill.put(death.getUniqueId(), System.currentTimeMillis()+(1000L*GetExtraPoints.COOLDOWN_KILL));
        balance+=GetExtraPoints.VALUE_KILL;
        player.sendMessage(ColorFixer.addBalance(GetExtraPoints.getInstance().getConfig().getString("lang.kill-money").replace("{player}", death.getName()), String.valueOf(GetExtraPoints.VALUE_KILL)));
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public long getNextMoney() {
        return nextMoney;
    }

    public void setNextMoney(long nextMoney) {
        this.nextMoney = nextMoney;
    }
}
