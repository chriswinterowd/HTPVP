package me.Buckets.kits;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import net.md_5.bungee.api.ChatColor;


public class kitScoreboard {
	
	public static void createBoard(Player player) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		Objective obj = board.registerNewObjective("kitScoreboard", "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		obj.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "HTPVP.COM");
		Team line = board.registerNewTeam("statsLine");
		line.addEntry(ChatColor.RED.toString());
		line.setPrefix(ChatColor.WHITE + "" + ChatColor.STRIKETHROUGH + "            ");
		line.setSuffix(ChatColor.WHITE + "" + ChatColor.STRIKETHROUGH + "            ");
		//Score score = obj.getScore(ChatColor.RED + "" + ChatColor.STRIKETHROUGH + "------------");
		obj.getScore(ChatColor.RED.toString()).setScore(6);
		long killsStat = Main.getPlugin().getConfig().getLong("Players." + player.getUniqueId() + ".kills");
		long deathsStat = Main.getPlugin().getConfig().getLong("Players." + player.getUniqueId() + ".deaths");
		long playerKillstreak = Main.getPlugin().getConfig().getLong("Players." + player.getUniqueId() + ".killstreak");
		
		Team kills = board.registerNewTeam("statsKills");
		kills.addEntry(ChatColor.BLUE.toString());
		kills.setPrefix(ChatColor.AQUA + "Kills: " + ChatColor.GOLD + Long.toString(killsStat));
		obj.getScore(ChatColor.BLUE.toString()).setScore(5);
		
		Team deaths = board.registerNewTeam("statsDeaths");
		deaths.addEntry(ChatColor.GOLD.toString());
		deaths.setPrefix(ChatColor.AQUA + "Deaths: ");
		deaths.setSuffix(ChatColor.GOLD + Long.toString(deathsStat));
		obj.getScore(ChatColor.GOLD.toString()).setScore(4);
		
		Team KDR = board.registerNewTeam("statsKDR");
		KDR.addEntry(ChatColor.AQUA.toString());
		KDR.setPrefix(ChatColor.AQUA + "KDR: ");
		KDR.setSuffix(ChatColor.GOLD + calculateKDR(player, killsStat, deathsStat));
		obj.getScore(ChatColor.AQUA.toString()).setScore(3);
		
		Team killstreak = board.registerNewTeam("statsKillstreak");
		killstreak.addEntry(ChatColor.DARK_PURPLE.toString());
		killstreak.setPrefix(ChatColor.AQUA + "Killstreak: ");
		killstreak.setSuffix(ChatColor.GOLD + Long.toString(playerKillstreak));
		obj.getScore(ChatColor.DARK_PURPLE.toString()).setScore(2);
		
		Team credits = board.registerNewTeam("statsCredits");
		credits.addEntry(ChatColor.GREEN.toString());
		credits.setPrefix(ChatColor.AQUA + "Credits: ");
		String creditAmount = Integer.toString(Main.getPlugin().getConfig().getInt("Players." + player.getUniqueId() + ".credits"));
		credits.setSuffix(ChatColor.GOLD + creditAmount);
		obj.getScore(ChatColor.GREEN.toString()).setScore(1);
		Team line2 = board.registerNewTeam("statsLine2");
		line2.addEntry(ChatColor.DARK_RED.toString());
		line2.setPrefix(ChatColor.WHITE + "" + ChatColor.STRIKETHROUGH + "            ");
		line2.setSuffix(ChatColor.WHITE + "" + ChatColor.STRIKETHROUGH + "            ");
		obj.getScore(ChatColor.DARK_RED.toString()).setScore(0);
		
		player.setScoreboard(board);
	}
	
	public static String calculateKDR(Player player, long kills, long deaths) {
		DecimalFormat df = new DecimalFormat("#.##");
		double kdr = (double) kills / (double) deaths;
		System.out.println(kills + " " + deaths + " " + kdr);
		return df.format(kdr);
	}
	
	
	
}
