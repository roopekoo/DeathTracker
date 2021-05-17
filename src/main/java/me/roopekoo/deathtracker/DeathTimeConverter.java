package me.roopekoo.deathtracker;

public class DeathTimeConverter {
	public String deathPerTime(double deathTime) {
		deathTime = deathTime * 20;
		if (deathTime >= 1) {
			return String.format("%.2f", deathTime) + " deaths/second";
		}
		deathTime = deathTime * 60;
		if (deathTime >= 1) {
			return String.format("%.2f", deathTime) + " deaths/minute";
		}
		deathTime = deathTime * 60;
		if (deathTime >= 1) {
			return String.format("%.2f", deathTime) + " deaths/hour";
		}
		deathTime = deathTime * 24;
		if (deathTime >= 1) {
			return String.format("%.2f", deathTime) + " deaths/day";
		}
		deathTime = deathTime * 365.25;
		return String.format("%.2f", deathTime) + " deaths/year";
	}

	public String playTicksToShortStr(double playTime) {
		playTime = playTime * 50;
		if (1000 > playTime) {
			return String.format("%.2f", playTime) + " milliseconds";
		}
		playTime = playTime / 1000;
		if (60 > playTime) {
			return String.format("%.2f", playTime) + " seconds";
		}
		playTime = playTime / 60;
		if (60 > playTime) {
			return String.format("%.2f", playTime) + " minutes";
		}
		playTime = playTime / 60;
		if (24 > playTime) {
			return String.format("%.2f", playTime) + " hours";
		}
		playTime = playTime / 24;
		if (365.25 > playTime) {
			return String.format("%.2f", playTime) + " days";
		}
		playTime = playTime / 365.25;
		return String.format("%.2f", playTime) + " years";
	}
}