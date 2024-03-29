package me.Buckets.kits;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import net.md_5.bungee.api.ChatColor;

public class Kits implements CommandExecutor {
	
	
	public static Inventory kitSelection;
	
	
	public static Inventory getKitSelection() {
		return kitSelection;
	}
	
	public static Set<UUID> players = new HashSet<>();
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(label.equalsIgnoreCase("spawn")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if(combatTag.checkTagged(player)) {
					player.sendMessage(ChatColor.RED + "You can't teleport while in combat.");
					return true;
				}
				
				if(Main.ServerPlayers.get(player.getUniqueId()).toWarping != 0) {
					player.sendMessage(ChatColor.RED + "You are already teleporting somewhere.");
					return true;
				}
				
				int teleportDelay = 100;
				if(!kitItems.checkPvpRegion(player)) teleportDelay = 0;
				if(kitItems.checkPvpRegion(player)) player.sendMessage(ChatColor.GREEN + "You will be teleported in 5 seconds. Don't move.");
				Main.ServerPlayers.get(player.getUniqueId()).toWarping = Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(Main.getPlugin(), new Runnable() {
		            public void run() {
						System.out.println(player.getLocation());
						Location loc = new Location(Main.getPlugin().getServer().getWorld("Kit World"), 11.505743587904526, 156.0, -38.62288293331645);
						player.teleport(loc);
						if(!Kits.players.contains(player.getUniqueId())) {
							players.add(player.getUniqueId());
						}
						Main.ServerPlayers.get(player.getUniqueId()).toWarping = 0;
		            }
		          }, teleportDelay);

				
				//leaderboardStatues.addJoinPacket(player);
				return true;
			}
		}
		
		if(label.equalsIgnoreCase("soup")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if(Main.getPlugin().getConfig().getString("Players." + player.getUniqueId().toString() + ".Heals").equalsIgnoreCase("soup")) {
					player.sendMessage(ChatColor.RED + "You already have soup enabled.");
					return true;
				}
				Main.getPlugin().getConfig().set("Players." + player.getUniqueId().toString() + ".Heals", "soup");
				Main.getPlugin().saveConfig();
				player.sendMessage(ChatColor.GREEN + "Switched to soup.");
				return true;
			}
		}
		
		if(label.equalsIgnoreCase("pots")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if(Main.getPlugin().getConfig().getString("Players." + player.getUniqueId().toString() + ".Heals").equalsIgnoreCase("pots")) {
					player.sendMessage(ChatColor.RED + "You already have pots enabled.");
					return true;
				}
				Main.getPlugin().getConfig().set("Players." + player.getUniqueId().toString() + ".Heals", "pots");
				Main.getPlugin().saveConfig();
				player.sendMessage(ChatColor.GREEN + "Switched to pots.");
				return true;
			}
		}
		
		
		
		if(label.equalsIgnoreCase("kit")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if(combatTag.checkTagged(player)) {
					player.sendMessage(ChatColor.RED + "You can't redeem kits while in combat.");
					return true;
				}
				if(args.length <= 0) {
					String kitList = ChatColor.YELLOW + "Kits: " + ChatColor.WHITE + "";
					for(String path : Main.getPlugin().getConfig().getConfigurationSection("kits").getKeys(false)) {
						path = Character.toUpperCase(path.charAt(0)) + path.substring(1);
						kitList += path + " ";
					}
					player.sendMessage(kitList);
					return true;
				}
				if(Main.getPlugin().getConfig().getStringList("kits." + args[0].toLowerCase() + ".items").size() <= 0) {
					player.sendMessage(ChatColor.RED + "Kit not found");
					return true;
				}
				
				if(!player.hasPermission("group." + Main.getPlugin().getConfig().getString("kits." + args[0].toLowerCase() + ".permission"))) {
					player.sendMessage(ChatColor.RED + "You do not have access to this kit.");
					return true;
				}
				
				if(Main.getPlugin().getConfig().contains("Players." + player.getUniqueId() + ".kits." + args[0].toLowerCase())) {
					String cooldown = Main.getPlugin().getConfig().getString("kits." + args[0].toLowerCase() + ".cooldown");
					String playerLastUsedKit = Main.getPlugin().getConfig().getString("Players." + player.getUniqueId() + ".kits." + args[0].toLowerCase());
					Boolean check = Kits.checkKitCooldown(player, Long.parseLong(cooldown) * 3600000, Long.parseLong(playerLastUsedKit));
					if(!check) return true;
				}
				//System.out.println(this.getConfig().getStringList("kits." + args[0] + ".items")); 
				
				List<String> kitItems = Main.getPlugin().getConfig().getStringList("kits." + args[0].toLowerCase() + ".items");
				List<String> kitArmor = Main.getPlugin().getConfig().getStringList("kits." + args[0].toLowerCase() + ".armor");
				int kitSize = kitItems.size() + kitArmor.size();
				if(player.getInventory().firstEmpty() == -1 || Kits.getEmptySlots(player) < kitSize) {
					player.sendMessage(ChatColor.RED + "You do not have enough space in your inventory.");
					return true;
				}
				for(int i = 0; i < kitItems.size(); i++) {
					String[] itemParams = kitItems.get(i).split(" ");
					giveItem(player, itemParams, args[0].toUpperCase());
				}
				
				if(!Main.getPlugin().getConfig().contains("kits." + args[0].toLowerCase() + ".menuItem")) {
					Potion strengthPotion = new Potion(PotionType.STRENGTH, 1);
					ItemStack strengthPot = strengthPotion.toItemStack(1);
					
					ItemMeta strengthPotMeta = strengthPot.getItemMeta();
					
					List<String> lore = new ArrayList<String>();
					lore.add(ChatColor.AQUA + "KIT " + args[0].toUpperCase());
					strengthPotMeta.setLore(lore);
					strengthPot.setItemMeta(strengthPotMeta);
					Potion speedPotion = new Potion(PotionType.SPEED, 1);
					ItemStack speedPot = speedPotion.toItemStack(1);
					ItemMeta speedPotMeta = strengthPot.getItemMeta();
					speedPotMeta.setLore(lore);
					speedPot.setItemMeta(speedPotMeta);
					player.getInventory().addItem(strengthPot);
					player.getInventory().addItem(speedPot);
				}
				
				if(player.getInventory().getHelmet() == null && player.getInventory().getChestplate() == null && player.getInventory().getLeggings() == null && player.getInventory().getBoots() == null) {
					Kits.equipArmor(player, kitArmor, args[0].toUpperCase());
				} else {
					for(int i = 0; i < kitArmor.size(); i++) {
						String[] itemParams = kitArmor.get(i).split(" ");
						giveItem(player, itemParams, args[0].toUpperCase());
					}
				}

				
				giveHeals(player);
				
				
				Main.getPlugin().getConfig().set("Players." + player.getUniqueId() + ".kits." + args[0].toLowerCase(), System.currentTimeMillis());
				Main.getPlugin().saveConfig();
				player.sendMessage(ChatColor.GRAY + "Kit redeemed");
				return true;
				
				
			}
		}
		
		
		
		if(label.equalsIgnoreCase("kits") ) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if(combatTag.checkTagged(player)) {
					player.sendMessage(ChatColor.RED + "You can't redeem kits while in combat.");
					return true;
				}
				
				player.openInventory(kitSelection);
				return true;
			}
			
		}
		return false;
	}
	
	
	public static void createKitSelection() {
		kitSelection = Bukkit.createInventory(null,  9, ChatColor.GOLD + "Kits");
		
		int index = 0;
		for(String path : Main.getPlugin().getConfig().getConfigurationSection("kits").getKeys(false)) {
			if(!Main.getPlugin().getConfig().contains("kits." + path + ".menuItem")) continue;
			System.out.println(index + "index" + path); 
			String [] menuItemParams = Main.getPlugin().getConfig().getString("kits." + path + ".menuItem").split(" ");
			String menuItem = menuItemParams[0];
			ItemStack item = new ItemStack(Material.matchMaterial(menuItem));
			ItemMeta meta = item.getItemMeta();
			
			String kitName = Main.getPlugin().getConfig().getString("kits." + path + ".menuName"); 
			System.out.println(kitName + "kit name");
			String kitColorCode = Main.getPlugin().getConfig().getString("kits." + path + ".menuColorCode");
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&" + kitColorCode + kitName));
			List<String> lore = new ArrayList<String>();
			
			String kitLore = Main.getPlugin().getConfig().getString("kits." + path + ".menuLore");
			lore.add(ChatColor.GRAY + kitLore);
			meta.setLore(lore);
			item.setItemMeta(meta);
			kitSelection.setItem(index, item);
			index++;
		}
		
		/*for (Material material : Material.values()) {
			  System.out.println(material.toString());
			}*/
		ItemStack item = new ItemStack(Material.BARRIER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_RED + "Close");
		item.setItemMeta(meta);
		kitSelection.setItem(8, item);
		
	}
	
	public static void giveItem(Player player, String [] itemParams, String kitName) {
		String itemName = itemParams[0];
		int itemAmount = 1;
		ItemStack item = new ItemStack(Material.DIAMOND_SWORD, itemAmount);
		if(!itemName.equalsIgnoreCase("CUSTOM") && !itemName.equalsIgnoreCase("KITITEM")) {
			if(itemParams.length >= 2) itemAmount = Integer.parseInt(itemParams[1]);
			item = new ItemStack(Material.matchMaterial(itemName), itemAmount);
			if(itemParams.length >= 3) {
				String[] enchantmentList = itemParams[2].split("/");
				System.out.println(enchantmentList.length + "length");
				for(int j = 0; j < enchantmentList.length; j++) {
					String[] enchantmentParams = enchantmentList[j].split(":");
					String enchantmentName = enchantmentParams[0];
					String enchantmentLevel = enchantmentParams[1];
					System.out.println(enchantmentName + enchantmentLevel);
					item.addUnsafeEnchantment(Enchantment.getByName(enchantmentName), Integer.parseInt(enchantmentLevel));
					ItemMeta itemMeta = item.getItemMeta();
					List<String> lore = new ArrayList<String>();
					lore.add(ChatColor.AQUA + "KIT " + kitName);
					itemMeta.setLore(lore);
					item.setItemMeta(itemMeta);
				}
			}
		}

		
		if(itemName.equalsIgnoreCase("KITITEM")) {
			item = new ItemStack(Material.matchMaterial(itemParams[1]));
			ItemMeta itemMeta = item.getItemMeta();
			String[] itemNameParams = itemParams[2].split(":");
			String[] itemNameDisplay = itemNameParams[1].split("/");
			String getItemNameDisplay = "";
			for(int i = 0; i < itemNameDisplay.length; i++) {
				if(i != itemNameDisplay.length - 1) getItemNameDisplay += itemNameDisplay[i] + " ";
				if(i == itemNameDisplay.length - 1) getItemNameDisplay += itemNameDisplay[i];
			}
					
			itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&" + itemNameParams[0] + getItemNameDisplay));
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.AQUA + "KIT " + kitName);
			itemMeta.setLore(lore);
			item.setItemMeta(itemMeta);
		}
		if(itemName.equalsIgnoreCase("CUSTOM")) {
			String[] color = itemParams[1].split(":");
			item = new ItemStack(Material.matchMaterial(itemParams[2]));
			if(itemParams.length >= 4) {
				String[] enchantmentList = itemParams[3].split("/");
				System.out.println(enchantmentList.length + "length");
				for(int j = 0; j < enchantmentList.length; j++) {
					String[] enchantmentParams = enchantmentList[j].split(":");
					String enchantmentName = enchantmentParams[0];
					String enchantmentLevel = enchantmentParams[1];
					System.out.println(enchantmentName + enchantmentLevel);
					item.addUnsafeEnchantment(Enchantment.getByName(enchantmentName), Integer.parseInt(enchantmentLevel));
				}
			}
			LeatherArmorMeta m = (LeatherArmorMeta) item.getItemMeta();
			
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.AQUA + "KIT " + kitName);
			m.setLore(lore);
			m.setColor(Color.fromRGB(Integer.parseInt(color[0]), Integer.parseInt(color[1]), Integer.parseInt(color[2])));;
			item.setItemMeta(m);
		}
		
		player.getInventory().addItem(item);
		
	}
	
	public static void giveHeals(Player player) {
		String healType = Main.getPlugin().getConfig().getString("Players." + player.getUniqueId().toString() + ".Heals");
		for(int slot = 0; slot < player.getInventory().getSize(); slot++){
			if(player.getInventory().getItem(slot) == null) {
				if(healType.equalsIgnoreCase("soup")) {
					player.getInventory().setItem(slot, new ItemStack(Material.matchMaterial("MUSHROOM_SOUP")));
				}
				
				if(healType.equalsIgnoreCase("pots")) {
					Potion healPot = new Potion(PotionType.INSTANT_HEAL, 2);
					healPot.setSplash(true);
					ItemStack potion = healPot.toItemStack(1);
					player.getInventory().setItem(slot, potion);
				}
			}
	        
		}
	}
	
	public static void equipArmor(Player player, List <String> kitArmor, String kitName) {
		for(int i = 0; i < kitArmor.size(); i++) {
			String[] armorParams = kitArmor.get(i).split(" ");
			String itemName = armorParams[0];
			ItemStack item = new ItemStack(Material.IRON_CHESTPLATE, 1);
			if(!itemName.equalsIgnoreCase("CUSTOM")) {
				int itemAmount = 1;
				item = new ItemStack(Material.matchMaterial(itemName), itemAmount);
				if(armorParams.length >= 3) {
					String[] enchantmentList = armorParams[2].split("/");
					System.out.println(enchantmentList.length + "length");
					for(int j = 0; j < enchantmentList.length; j++) {
						String[] enchantmentParams = enchantmentList[j].split(":");
						String enchantmentName = enchantmentParams[0];
						String enchantmentLevel = enchantmentParams[1];
						System.out.println(enchantmentName + enchantmentLevel);
						item.addUnsafeEnchantment(Enchantment.getByName(enchantmentName), Integer.parseInt(enchantmentLevel));
					}
				}
			}
			
			if(itemName.equalsIgnoreCase("CUSTOM")) {
				itemName = armorParams[2];
				String[] color = armorParams[1].split(":");
				item = new ItemStack(Material.matchMaterial(armorParams[2]));
				if(armorParams.length >= 4) {
					String[] enchantmentList = armorParams[3].split("/");
					System.out.println(enchantmentList.length + "length");
					for(int j = 0; j < enchantmentList.length; j++) {
						String[] enchantmentParams = enchantmentList[j].split(":");
						String enchantmentName = enchantmentParams[0];
						String enchantmentLevel = enchantmentParams[1];
						System.out.println(enchantmentName + enchantmentLevel);
						item.addUnsafeEnchantment(Enchantment.getByName(enchantmentName), Integer.parseInt(enchantmentLevel));
					}
				}
				LeatherArmorMeta m = (LeatherArmorMeta) item.getItemMeta();
				m.setColor(Color.fromRGB(Integer.parseInt(color[0]), Integer.parseInt(color[1]), Integer.parseInt(color[2])));;
				item.setItemMeta(m);
			}

			ItemMeta itemMeta = item.getItemMeta();
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.AQUA + "KIT " + kitName);
			itemMeta.setLore(lore);
			item.setItemMeta(itemMeta);
			
			if(itemName.equalsIgnoreCase("IRON_HELMET") || itemName.equalsIgnoreCase("DIAMOND_HELMET") || itemName.equalsIgnoreCase("CHAINMAIL_HELMET") || itemName.equalsIgnoreCase("LEATHER_HELMET")) player.getInventory().setHelmet(item);
			if(itemName.equalsIgnoreCase("IRON_CHESTPLATE") || itemName.equalsIgnoreCase("DIAMOND_CHESTPLATE") || itemName.equalsIgnoreCase("CHAINMAIL_CHESTPLATE") || itemName.equalsIgnoreCase("LEATHER_CHESTPLATE")) player.getInventory().setChestplate(item);
			if(itemName.equalsIgnoreCase("IRON_LEGGINGS") || itemName.equalsIgnoreCase("DIAMOND_LEGGINGS") || itemName.equalsIgnoreCase("CHAINMAIL_LEGGINGS") || itemName.equalsIgnoreCase("LEATHER_LEGGINGS")) player.getInventory().setLeggings(item);
			if(itemName.equalsIgnoreCase("IRON_BOOTS") || itemName.equalsIgnoreCase("DIAMOND_BOOTS") || itemName.equalsIgnoreCase("CHAINMAIL_BOOTS") || itemName.equalsIgnoreCase("LEATHER_BOOTS")) player.getInventory().setBoots(item);
			
			

		}
		
	}
	

	public static Boolean checkKitCooldown(Player player, long cooldown, long lastUsed) {
		if(player.hasPermission("group.mvp")) cooldown = (long) (cooldown * .75);
		if(player.hasPermission("group.alpha")) cooldown = (long) (cooldown * .50);
		if(!player.hasPermission("group.owner") && System.currentTimeMillis() < lastUsed + cooldown) {
			String kitLeft = Kits.getDurationBreakdown((long)Math.floor(((lastUsed + cooldown) - System.currentTimeMillis())));
			
			//long minutes = (long) (lastUsed + cooldown) - System.currentTimeMillis();
			player.sendMessage(ChatColor.RED + "You have " + kitLeft + " until you can use this kit again.");
			return false;
		}
		return true;
	}
	
    public static int getEmptySlots(Player p) {
        PlayerInventory inventory = p.getInventory();
        ItemStack[] cont = inventory.getContents();
        int i = 0;
        for (ItemStack item : cont)
          if (item != null && item.getType() != Material.AIR) {
            i++;
          }
        return 36 - i;
    }
    
    public static String getDurationBreakdown(long millis) {
        if(millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        //sb.append(days);
        //sb.append(" Days ");
        sb.append(hours);
        sb.append(" hours");
        sb.append(", ");
        sb.append(minutes);
        sb.append(" minutes");
        sb.append(" and ");
        sb.append(seconds);
        sb.append(" seconds");

        return(sb.toString());
    }
}




