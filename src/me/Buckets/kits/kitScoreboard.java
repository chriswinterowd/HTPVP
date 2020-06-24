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
		obj.setDisplayName(ChatColor.BOLD + "" + ChatColor.AQUA + "   HTPVP.COM");
		Team line = board.registerNewTeam("statsLine");
		line.addEntry(ChatColor.RED.toString());
		line.setPrefix(ChatColor.WHITE + "" + ChatColor.STRIKETHROUGH + "---------");
		line.setSuffix(ChatColor.WHITE + "" + ChatColor.STRIKETHROUGH + "---------");
		//Score score = obj.getScore(ChatColor.RED + "" + ChatColor.STRIKETHROUGH + "------------");
		obj.getScore(ChatColor.RED.toString()).setScore(5);
		/*Score score2 = obj.getScore(ChatColor.GOLD + "Kills: " + player.getStatistic(Statistic.PLAYER_KILLS));
		score2.setScore(2);
		Score score3 = obj.getScore(ChatColor.GOLD + "Deaths: " + player.getStatistic(Statistic.DEATHS));
		score3.setScore(1);
		DecimalFormat df = new DecimalFormat("#.##");
		double kdr = (double) player.getStatistic(Statistic.PLAYER_KILLS) / (double) player.getStatistic(Statistic.DEATHS);
		Score score4 = obj.getScore(ChatColor.GOLD + "KDR: " + df.format(kdr));
		score4.setScore(0);*/
		Team kills = board.registerNewTeam("statsKills");
		kills.addEntry(ChatColor.BLUE.toString());
		kills.setPrefix(ChatColor.AQUA + "Kills: " + ChatColor.GOLD + Integer.toString(player.getStatistic(Statistic.PLAYER_KILLS)));
		kills.setSuffix("               ");
		obj.getScore(ChatColor.BLUE.toString()).setScore(4);
		
		Team deaths = board.registerNewTeam("statsDeaths");
		deaths.addEntry(ChatColor.GOLD.toString());
		deaths.setPrefix(ChatColor.AQUA + "Deaths: ");
		deaths.setSuffix(ChatColor.GOLD + Integer.toString(player.getStatistic(Statistic.DEATHS)));
		obj.getScore(ChatColor.GOLD.toString()).setScore(3);
		
		Team KDR = board.registerNewTeam("statsKDR");
		KDR.addEntry(ChatColor.AQUA.toString());
		KDR.setPrefix(ChatColor.AQUA + "KDR: ");
		KDR.setSuffix(ChatColor.GOLD + calculateKDR(player, player.getStatistic(Statistic.PLAYER_KILLS), player.getStatistic(Statistic.DEATHS)));
		obj.getScore(ChatColor.AQUA.toString()).setScore(2);
		
		Team credits = board.registerNewTeam("statsCredits");
		credits.addEntry(ChatColor.GREEN.toString());
		credits.setPrefix(ChatColor.AQUA + "Credits: ");
		String creditAmount = Integer.toString(Main.getPlugin().getConfig().getInt("Player." + player.getUniqueId() + ".credits"));
		credits.setSuffix(ChatColor.GOLD + creditAmount);
		obj.getScore(ChatColor.GREEN.toString()).setScore(1);
		
		Team line2 = board.registerNewTeam("statsLine2");
		line2.addEntry(ChatColor.DARK_RED.toString());
		line2.setPrefix(ChatColor.WHITE + "" + ChatColor.STRIKETHROUGH + "---------");
		line2.setSuffix(ChatColor.WHITE + "" + ChatColor.STRIKETHROUGH + "---------");
		obj.getScore(ChatColor.DARK_RED.toString()).setScore(0);
		player.setScoreboard(board);
		
	}
	
	public static String calculateKDR(Player player, int kills, int deaths) {
		DecimalFormat df = new DecimalFormat("#.##");
		double kdr = (double) kills / (double) deaths;
		return df.format(kdr);
	}
	
	
}