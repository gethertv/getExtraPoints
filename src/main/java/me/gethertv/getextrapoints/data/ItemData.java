package me.gethertv.getextrapoints.data;

import me.gethertv.getextrapoints.GetExtraPoints;
import me.gethertv.getextrapoints.utils.ColorFixer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemData {

    private String name;
    private TypeExecute typeExecute;
    private double price;
    private ItemStack itemStack;
    private List<String> commands;


    public ItemData(String name, TypeExecute typeExecute, double price, ItemStack itemStack, List<String> commands) {
        this.name = name;
        this.typeExecute = typeExecute;
        this.price = price;
        this.itemStack = itemStack;
        this.commands = commands;
    }

    public void execute(Player player)
    {
        player.closeInventory();

        User user = GetExtraPoints.getInstance().getUserData().get(player.getUniqueId());
        if(user.getBalance()<price)
        {
            player.sendMessage(ColorFixer.addColor(GetExtraPoints.getInstance().getConfig().getString("lang.no-money")));
            return;
        }
        user.setBalance(user.getBalance()-price);
        if(typeExecute==TypeExecute.ITEM)
            player.getInventory().addItem(itemStack);

        if(typeExecute==TypeExecute.COMMAND)
            commands.forEach(cmd -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("{player}", player.getName()));
            });

        player.sendMessage(ColorFixer.addColor(GetExtraPoints.getInstance().getConfig().getString("lang.success-buy").replace("{name}", name)));

    }
}
