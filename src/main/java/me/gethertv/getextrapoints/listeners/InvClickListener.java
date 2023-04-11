package me.gethertv.getextrapoints.listeners;

import me.gethertv.getextrapoints.GetExtraPoints;
import me.gethertv.getextrapoints.data.ItemData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InvClickListener implements Listener {

    @EventHandler
    public void onClickInv(InventoryClickEvent event)
    {
        if(event.getClickedInventory()==null)
            return;

        Player player = (Player) event.getWhoClicked();
        if(event.getInventory().equals(GetExtraPoints.getInstance().getInvShop()))
        {
            event.setCancelled(true);
            if(event.getClickedInventory().equals(GetExtraPoints.getInstance().getInvShop()))
            {
                ItemData itemData = GetExtraPoints.getInstance().getItemData().get(event.getSlot());
                if(itemData==null)
                    return;

                itemData.execute(player);
            }
        }
    }
}
