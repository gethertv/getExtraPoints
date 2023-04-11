package me.gethertv.getextrapoints.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.gethertv.getextrapoints.GetExtraPoints;
import me.gethertv.getextrapoints.utils.Timer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class ExtraPlaceholder extends PlaceholderExpansion {
    DecimalFormat formatter = new DecimalFormat("00");
    @Override
    public @NotNull String getIdentifier() {
        return "getextrapoints";
    }

    @Override
    public @NotNull String getAuthor() {
        return "gethertv";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    public String onRequest(OfflinePlayer offlinePlayer, String identifier) {
        if (offlinePlayer.getPlayer() == null) return null;
        Player player = offlinePlayer.getPlayer();
        if (identifier.equals("balance"))
        {
            return String.valueOf(GetExtraPoints.getInstance().getUserData().get(player.getUniqueId()).getBalance());
        }
        return null;
    }
}
