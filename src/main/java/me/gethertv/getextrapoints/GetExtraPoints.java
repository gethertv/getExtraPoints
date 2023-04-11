package me.gethertv.getextrapoints;

import me.gethertv.getextrapoints.cmd.EShopCmd;
import me.gethertv.getextrapoints.cmd.ExtraPointsCmd;
import me.gethertv.getextrapoints.data.ItemData;
import me.gethertv.getextrapoints.data.TypeExecute;
import me.gethertv.getextrapoints.data.User;
import me.gethertv.getextrapoints.file.ItemsFile;
import me.gethertv.getextrapoints.listeners.ConnectListener;
import me.gethertv.getextrapoints.listeners.InvClickListener;
import me.gethertv.getextrapoints.placeholder.ExtraPlaceholder;
import me.gethertv.getextrapoints.storage.Mysql;
import me.gethertv.getextrapoints.threads.AutoSave;
import me.gethertv.getextrapoints.threads.MoneyAuto;
import me.gethertv.getextrapoints.utils.ColorFixer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public final class GetExtraPoints extends JavaPlugin {

    private static GetExtraPoints instance;
    private Mysql sql;
    private HashMap<UUID, User> userData = new HashMap<>();
    private Inventory invShop;

    private HashMap<Integer, ItemData> itemData = new HashMap<>();

    public static int COOLDOWN_MONEY;
    public static int COOLDOWN_KILL;

    public static double VALUE_MONEY;
    public static double VALUE_KILL;

    public static boolean ENABLE_KILL = false;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();
        ItemsFile.loadFile();

        setupSql();
        if (!sql.isConnected()) {
            getLogger().log (Level.WARNING, "Nie można połączyć sie z baza danych!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            (new ExtraPlaceholder()).register();
        }

        COOLDOWN_MONEY = getConfig().getInt("auto-money.time");
        COOLDOWN_KILL = getConfig().getInt("kill-player.time");

        VALUE_MONEY = getConfig().getInt("kill-player.value");
        VALUE_KILL = getConfig().getInt("kill-player.value");

        ENABLE_KILL = getConfig().getBoolean("kill-player.enable");

        implementsInvShop();
        implementsItemShop();

        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers())
                {
                    sql.loadPlayer(player);
                }
            }
        }.runTaskAsynchronously(this);


        getServer().getPluginManager().registerEvents(new ConnectListener(), this);
        getServer().getPluginManager().registerEvents(new InvClickListener(), this);

        getCommand("eshop").setExecutor(new EShopCmd());

        getCommand("getextrapoints").setExecutor(new ExtraPointsCmd());
        getCommand("getextrapoints").setTabCompleter(new ExtraPointsCmd());

        if(getConfig().getBoolean("auto-money.enable"))
            new MoneyAuto().runTaskTimer(this, 20L, 20L);

        new AutoSave().runTaskTimer(this, 20L*120, 20L*120);

    }



    @Override
    public void onDisable() {

        for(Player player : Bukkit.getOnlinePlayers())
            sql.updatePlayer(player);

        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);

        sql.closeConnection();
        instance = null;
    }

    private void implementsItemShop() {
        FileConfiguration config = ItemsFile.getConfig();
        for(String key : config.getConfigurationSection("items").getKeys(false))
        {
            TypeExecute typeExecute = TypeExecute.valueOf(config.getString("items."+key+".type").toUpperCase());
            double price = config.getDouble("items."+key+".price");
            int slot = config.getInt("items."+key+".slot");
            String name = config.getString("items."+key+".name");

            ItemStack buyItem = null;
            List<String> commands = new ArrayList<>();
            if(typeExecute==TypeExecute.ITEM)
            {
                buyItem = new ItemStack(Material.valueOf(config.getString("items."+key+".item.material").toUpperCase()));
                if(config.isSet("items."+key+".item.amount"))
                    buyItem.setAmount(config.getInt("items."+key+".item.amount"));
            }
            if(typeExecute==TypeExecute.COMMAND)
                commands.addAll(config.getStringList("items."+key+".commands"));

            // gui item
            ItemStack itemStack = new ItemStack(Material.valueOf(config.getString("items."+key+".gui-item.material").toUpperCase()));
            if(config.isSet("items."+key+".gui-item.amount"))
                itemStack.setAmount(config.getInt("items."+key+".gui-item.amount"));

            if(config.getBoolean("items."+key+".gui-item.glow"))
                itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            itemMeta.setDisplayName(ColorFixer.addColor(config.getString("items."+key+".gui-item.displayname")));
            List<String> lore = new ArrayList<>();
            lore.addAll(config.getStringList("items."+key+".gui-item.lore"));
            itemMeta.setLore(ColorFixer.addBalance(lore, String.valueOf(price)));

            itemStack.setItemMeta(itemMeta);

            itemData.put(slot, new ItemData(name, typeExecute, price, buyItem, commands));
            invShop.setItem(slot, itemStack);
        }
    }


    private void implementsInvShop()
    {
        invShop = Bukkit.createInventory(null, getConfig().getInt("inv.size"), ColorFixer.addColor(getConfig().getString("inv.title")));

        addBackground();

    }

    private void addBackground() {
        for(String key : getConfig().getConfigurationSection("inv.background").getKeys(false))
        {
            ItemStack itemStack = new ItemStack(Material.valueOf(getConfig().getString("inv.background."+key+".material").toUpperCase()));
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ColorFixer.addColor(getConfig().getString("inv.background."+key+".displayname")));
            List<String> lore = new ArrayList<>();
            lore.addAll(getConfig().getStringList("inv.background."+key+".lore"));
            itemMeta.setLore(ColorFixer.addColor(lore));

            itemStack.setItemMeta(itemMeta);

            for(Integer slot : getConfig().getIntegerList("inv.background."+key+".slots"))
                invShop.setItem(slot, itemStack);

        }
    }

    private void setupSql() {
        String host = getConfig().getString("mysql.host");
        String username = getConfig().getString("mysql.username");
        String password = getConfig().getString("mysql.password");
        String database = getConfig().getString("mysql.database");
        String port = getConfig().getString("mysql.port");

        boolean ssl = false;
        if (getConfig().get("mysql.ssl") != null) {
            ssl = getConfig().getBoolean("mysql.ssl");
        }
        this.sql = new Mysql(host, username, password, database, port, ssl);
    }

    public static GetExtraPoints getInstance() {
        return instance;
    }

    public HashMap<UUID, User> getUserData() {
        return userData;
    }

    public Mysql getSql() {
        return sql;
    }

    public Inventory getInvShop() {
        return invShop;
    }

    public HashMap<Integer, ItemData> getItemData() {
        return itemData;
    }
}
