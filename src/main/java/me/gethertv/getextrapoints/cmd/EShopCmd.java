package me.gethertv.getextrapoints.cmd;

import me.gethertv.getextrapoints.GetExtraPoints;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EShopCmd implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player))
            return false;

        Player player = (Player) sender;
        if(player.hasPermission("eshop.use"))
        {
            player.openInventory(GetExtraPoints.getInstance().getInvShop());
        }
        return false;
    }
}
