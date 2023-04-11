package me.gethertv.getextrapoints.cmd;

import me.gethertv.getextrapoints.GetExtraPoints;
import me.gethertv.getextrapoints.data.FindOneCallback;
import me.gethertv.getextrapoints.data.User;
import me.gethertv.getextrapoints.utils.ColorFixer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class ExtraPointsCmd implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender.hasPermission("getextrapoints.admin"))
        {
            if(args.length==3)
            {
                if(args[0].equalsIgnoreCase("give"))
                {
                    if(!isDouble(args[2]))
                    {
                        sender.sendMessage(ColorFixer.addColor("&cMusisz podac liczbe typu double!"));
                        return true;
                    }
                    String username = args[1];
                    double balance = Double.parseDouble(args[2]);
                    Player player = Bukkit.getPlayer(username);
                    if(player==null)
                    {
                        GetExtraPoints.getInstance().getSql().addOfflineUser(username, balance, new FindOneCallback() {
                            @Override
                            public void onQueryDone(boolean exists) {
                                if(exists)
                                {
                                    sender.sendMessage(ColorFixer.addColor("&aPomyslnie dodano walute!"));
                                } else {
                                    sender.sendMessage(ColorFixer.addColor("&cNie znaleziono podanego gracza!"));
                                }
                            }
                        });
                    } else {
                        User user = GetExtraPoints.getInstance().getUserData().get(player.getUniqueId());
                        user.setBalance(user.getBalance()+balance);
                        player.sendMessage(ColorFixer.addBalance(GetExtraPoints.getInstance().getConfig().getString("lang.new-balance"), String.format("%.2f", user.getBalance())));
                        sender.sendMessage(ColorFixer.addColor("&aPomyslnie dodano walute!"));
                        return true;
                    }
                    return false;
                }

                if(args[0].equalsIgnoreCase("set"))
                {
                    if(!isDouble(args[2]))
                    {
                        sender.sendMessage(ColorFixer.addColor("&cMusisz podac liczbe typu double!"));
                        return true;
                    }
                    String username = args[1];
                    double balance = Double.parseDouble(args[2]);
                    Player player = Bukkit.getPlayer(username);
                    if(player==null)
                    {
                        GetExtraPoints.getInstance().getSql().setOfflineUser(username, balance, new FindOneCallback() {
                            @Override
                            public void onQueryDone(boolean exists) {
                                if(exists)
                                {
                                    sender.sendMessage(ColorFixer.addColor("&aPomyslnie ustawiono walute!"));
                                } else {
                                    sender.sendMessage(ColorFixer.addColor("&cNie znaleziono podanego gracza!"));
                                }
                            }
                        });
                    } else {
                        User user = GetExtraPoints.getInstance().getUserData().get(player.getUniqueId());
                        user.setBalance(balance);
                        player.sendMessage(ColorFixer.addBalance(GetExtraPoints.getInstance().getConfig().getString("lang.new-balance"), String.format("%.2f", user.getBalance())));
                        sender.sendMessage(ColorFixer.addColor("&aPomyslnie ustawiono walute!"));
                        return true;
                    }
                    return false;
                }
            }
        }
        if(sender instanceof Player)
        {
            Player player = (Player) sender;
            User user = GetExtraPoints.getInstance().getUserData().get(player.getUniqueId());
            player.sendMessage(ColorFixer.addBalance(GetExtraPoints.getInstance().getConfig().getString("lang.balance"), String.format("%.2f", user.getBalance())));
            return true;
        }
        return false;
    }

    private boolean isDouble(String input)
    {
        try {
            double x = Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {}

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length==1)
        {
            return Arrays.asList("give", "set");
        }
        return null;
    }
}
