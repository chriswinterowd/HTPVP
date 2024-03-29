package me.Buckets.kits;
import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.world.DataException;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import net.md_5.bungee.api.ChatColor;


public class baseSystem implements Listener {
	public static WorldEditPlugin getWorldEdit() {
		Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		if(p instanceof WorldEditPlugin) return (WorldEditPlugin) p;
		else return null;
	}
	
    @SuppressWarnings("deprecation")
    public static void loadSchematic(Player player, Location location, String base) {
        EditSession editSession = new EditSession(new BukkitWorld(player.getLocation().getWorld()), Integer.MAX_VALUE);
        Vector loc = new Vector(location.getX(), location.getY(), location.getZ());
        Vector loc2 = new Vector(location.getX() + 25, location.getY(), location.getZ() + 25);
        File schem = new File(getWorldEdit().getDataFolder() + File.separator + "schematics" + File.separator + base + ".schematic");
        try {
            CuboidClipboard cc = CuboidClipboard.loadSchematic(schem);
            cc.paste(editSession, loc, false);
         
            //cc.paste(editSession, loc2, false);
        } catch (DataException | IOException | MaxChangedBlocksException e) {
            e.printStackTrace();
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
    	if(event.getPlayer() != null) {
    		Player player = (Player) event.getPlayer();
    		if(player.hasPermission("group.admin")) return;
			RegionContainer container = createBase.getWorldGuard().getRegionContainer();
			RegionQuery query = container.createQuery();

			ApplicableRegionSet set = query.getApplicableRegions(player.getLocation());
			Boolean checkOwnership = false;
		    for (ProtectedRegion region : set) {
		    	if (region.getOwners().contains(player.getUniqueId())) checkOwnership = true;
		    }
		    
    		if(player.hasPermission("build") && checkOwnership) {
    			if(event.getBlock().getType() == Material.GLOWSTONE) event.getBlock().setType(Material.AIR);
    			return;
    		}
    	}
    	
    	
    	if(event.getBlock().getType() == Material.CHEST || event.getBlock().getType() == Material.ANVIL || event.getBlock().getType() == Material.BREWING_STAND || event.getBlock().getType() == Material.BREWING_STAND || event.getBlock().getType().toString().endsWith("WOOL")) return;
    	Player player = (Player) event.getPlayer();
    	event.setCancelled(true);
    }
    
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
    	Player player = (Player) event.getPlayer();
    	if(event.getPlayer() != null) {
    		if(player.hasPermission("group.admin")) return;
    		if(player.hasPermission("build")) {
    			RegionContainer container = createBase.getWorldGuard().getRegionContainer();
    			RegionQuery query = container.createQuery();

    			ApplicableRegionSet set = query.getApplicableRegions(player.getLocation());
    			if(event.getBlock().getType() == Material.CHEST || event.getBlock().getType() == Material.ANVIL || event.getBlock().getType() == Material.BREWING_STAND) {
        		    for (ProtectedRegion region : set) {
        		    	
        		    	if (region.getId().contains("playerwarp")) {
        		    		
        		    		event.setCancelled(true);
        		    		break;
        		    		
        		    		
        		    		
        		    	}
        		    	
        		    }
     
    			}

    		}
    		return;
    	}
	    
	   
    	if(event.getBlock().getType() == Material.CHEST || event.getBlock().getType() == Material.ANVIL || event.getBlock().getType() == Material.BREWING_STAND || event.getBlock().getType().toString().endsWith("WOOL")) return;
    	event.setCancelled(true);
    }
    
    
    
    @EventHandler
    public void openTable(PlayerInteractEvent event) {
    	if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
    	if(event.getClickedBlock().getType() == Material.WORKBENCH) event.setCancelled(true);
    	if(event.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE) event.setCancelled(true);
    	if(event.getClickedBlock().getType() == Material.FURNACE) event.setCancelled(true);
    }
    
	
	
}
