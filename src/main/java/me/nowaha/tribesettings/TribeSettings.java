package me.nowaha.tribesettings;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

public final class TribeSettings extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        //saveResource("config.yml", false);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public Logger getSLF4JLogger() {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (command.getLabel().equalsIgnoreCase("settings")) {
                Player player = (Player) sender;
                Inventory inventory = Bukkit.createInventory(null, 6 * 9, "Your Settings");

                for (int i = 0; i < 9; i++) {
                    inventory.setItem(i, new ItemStackBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§awww.tribewars.net").build());
                }
                for (int i = 45; i < 54; i++) {
                    inventory.setItem(i, new ItemStackBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§awww.tribewars.net").build());
                }
                inventory.setItem(49, new ItemStackBuilder(Material.RED_STAINED_GLASS_PANE).displayName("§cClose").build());



                inventory.setItem(19, createSetting(player, "Autosell Messages", Material.GOLD_INGOT, new String[] {"§7Toggles whether you will", "§7receive messages when your", "§7autosell chest(s) sell."}));
                inventory.setItem(21, createSetting(player, "Crate Messages", Material.CHEST, new String[] {"§7Toggles whether you will", "§7receive messages when someone", "§7unboxes a crate."}));
                inventory.setItem(23, createSetting(player, "Vote Messages", Material.MAP, new String[] {"§7Toggles whether you will", "§7receive messages when someone", "§7votes for the server."}));
                inventory.setItem(25, createSetting(player, "Alerts", Material.EXPERIENCE_BOTTLE, new String[] {"§7Toggles whether you will", "§7receive (optional) server alerts,", "§7like helpful information."}));

                inventory.setItem(19 + 9, createSettingValue(player, "Autosell Messages"));
                inventory.setItem(21 + 9, createSettingValue(player, "Crate Messages"));
                inventory.setItem(23 + 9, createSettingValue(player, "Vote Messages"));
                inventory.setItem(25 + 9, createSettingValue(player, "Alerts"));
                player.openInventory(inventory);
                return true;
            } else if (command.getLabel().equalsIgnoreCase("broadcastto")) {
                sender.sendMessage("§7What? No, don't use this!");
            }
        } else {
            if (command.getLabel().equalsIgnoreCase("broadcastto")) {
                for (Player player :
                        Bukkit.getOnlinePlayers()) {
                    if (getSetting(player, args[0])) {
                        String text = "";
                        Integer index = 0;
                        for (String arg :
                                args) {
                            if (index == 0) {
                                index++;
                                continue;
                            }

                            text += arg + " ";

                            index++;
                        }
                        text = text.substring(0, text.length() - 1);
                        
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', text));
                    }
                }
            }
        }
        return super.onCommand(sender, command, label, args);
    }

    ItemStack createSetting(Player player, String name, Material item, String[] lore) {
        return new ItemStackBuilder(item).displayName((getSetting(player, name) ? "§a" : "§c") + name).lore(lore).build();
    }

    ItemStack createSettingValue(Player player, String name) {
        if (getSetting(player, name)) {
            return new ItemStackBuilder(Material.LIME_DYE).displayName("§a" + name).lore(new String[] {"§7Click to disable."}).build();
        } else {
            return new ItemStackBuilder(Material.GRAY_DYE).displayName("§c" + name).lore(new String[] {"§7Click to enable."}).build();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView().getTitle().contains("Your Settings")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            if (!e.getCurrentItem().hasItemMeta()) return;
            if (e.getClickedInventory() == null) return;

            Player player = (Player) e.getWhoClicked();

            if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Close")) {
                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            } else {
                if (e.getCurrentItem().getItemMeta().getLore().get(0).contains("Click to disable.")) {
                    changeSetting(player, ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName().toLowerCase().replaceAll(" ", "")), false);
                } else if (e.getCurrentItem().getItemMeta().getLore().get(0).contains("Click to enable.")) {
                    changeSetting(player, ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName().toLowerCase().replaceAll(" ", "")), true);
                }

                player.performCommand("settings");
            }
        }
    }

    void changeSetting(Player player, String setting, Boolean val) {
        getConfig().set("settings." + player.getUniqueId().toString() + "." + setting.toLowerCase().replaceAll(" ", ""), val);
        saveConfig();

        if (val) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 10, 2);
        } else {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 10, 0);
        }
    }

    public Boolean getSetting(Player player, String setting) {
        return getConfig().getBoolean("settings." + player.getUniqueId().toString() + "." + setting.toLowerCase().replaceAll(" ", ""), true);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
