package me.Buckets.kits;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;



public class shopPortal implements Listener {

	@EventHandler
	public static void onPortalTravel(PlayerPortalEvent event)
	{
	    if(event.getCause() == PlayerPortalEvent.TeleportCause.END_PORTAL) {
	    	event.setCancelled(true);
			double x = Main.getPlugin().getConfig().getDouble("Warps." + "shop" + ".x");
			double y = Main.getPlugin().getConfig().getDouble("Warps." + "shop" + ".y");
			double z = Main.getPlugin().getConfig().getDouble("Warps." + "shop" + ".z");
			float yaw = (float) Main.getPlugin().getConfig().getDouble("Warps." + "shop" + ".yaw");
			float pitch = (float) Main.getPlugin().getConfig().getDouble("Warps." + "shop" + ".pitch");
			Location loc = new Location(Main.getPlugin().getServer().getWorld("Kit World"), x, y, z, yaw, pitch);
	        event.getPlayer().teleport(loc);
	    }
	    
	    if(event.getCause() == PlayerPortalEvent.TeleportCause.NETHER_PORTAL) event.setCancelled(true);
	    

		
	}
	

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (player.getLocation().getBlock().getType().equals(Material.PORTAL)) {
			double x = Main.getPlugin().getConfig().getDouble("Warps." + "blackmarket" + ".x");
			double y = Main.getPlugin().getConfig().getDouble("Warps." + "blackmarket" + ".y");
			double z = Main.getPlugin().getConfig().getDouble("Warps." + "blackmarket" + ".z");
			float yaw = (float) Main.getPlugin().getConfig().getDouble("Warps." + "blackmarket" + ".yaw");
			float pitch = (float) Main.getPlugin().getConfig().getDouble("Warps." + "blackmarket" + ".pitch");
			Location loc = new Location(Main.getPlugin().getServer().getWorld("Kit World"), x, y, z, yaw, pitch);
	        event.getPlayer().teleport(loc);
		}
	}

	
	
	
}
