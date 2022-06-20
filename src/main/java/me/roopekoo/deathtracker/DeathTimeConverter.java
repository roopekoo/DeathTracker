package me.roopekoo.deathtracker;

/**
 Convert ticks to suitable time units
 */
public class DeathTimeConverter {
	/**
	 Convert deathTime to string with suitable time units
	 @param deathTime deaths per time
	 @return time converted to suitable time/death string
	 */
	public String deathPerTime(double deathTime) {
		String time;
		String deathText = "death";
		String unit;
		if(deathTime*TicksToUnit.SECOND.value>=1) {
			deathTime = deathTime*TicksToUnit.SECOND.value;
			unit = "sec";
		} else if(deathTime*TicksToUnit.MINUTE.value>=1) {
			deathTime = deathTime*TicksToUnit.MINUTE.value;
			unit = "min";
		} else if(deathTime*TicksToUnit.HOUR.value>=1) {
			deathTime = deathTime*TicksToUnit.HOUR.value;
			unit = "hour";
		} else if(deathTime*TicksToUnit.DAY.value>=1) {
			deathTime = deathTime*TicksToUnit.DAY.value;
			unit = "day";
		} else {
			deathTime = deathTime*TicksToUnit.YEAR.value;
			unit = "year";
		}
		time = String.format("%.2f", deathTime);
		if(time.contains(".00")|time.contains(",00")) {
			if(!String.valueOf(time.charAt(0)).equals("1")) {
				deathText = deathText+"s";
			}
			time = String.valueOf(time.charAt(0));
		} else {
			deathText = deathText+"s";
		}

		deathText = Lang.valueOf(deathText.toUpperCase()).toString();
		unit = Lang.valueOf(unit.toUpperCase()).toString();
		return time+" "+deathText+"/"+unit;
	}

	public String playTicksToShortStr(double playTime) {
		String time;
		String unit;
		if(60>playTime/TicksToUnit.SECOND.value) {
			playTime = playTime/TicksToUnit.SECOND.value;
			unit = "sec";
		} else if(60>playTime/TicksToUnit.MINUTE.value) {
			playTime = playTime/TicksToUnit.MINUTE.value;
			unit = "min";
		} else if(24>playTime/TicksToUnit.HOUR.value) {
			playTime = playTime/TicksToUnit.HOUR.value;
			unit = "hour";
		} else if(365.25>playTime/TicksToUnit.DAY.value) {
			playTime = playTime/TicksToUnit.DAY.value;
			unit = "day";
		} else {
			playTime = playTime/TicksToUnit.YEAR.value;
			unit = "year";
		}
		time = String.format("%.2f", playTime);
		if(time.contains(".00")|time.contains(",00")) {
			if(!String.valueOf(time.charAt(0)).equals("1")) {
				unit = unit+"s";
			}
			time = String.valueOf(time.charAt(0));
		} else {
			unit = unit+"s";
		}
		unit = Lang.valueOf(unit.toUpperCase()).toString();
		return time+" "+unit;
	}

	/**
	 Store values for time units
	 */
	private enum TicksToUnit {
		SECOND(20),
		MINUTE(20*60),
		HOUR(20*60*60),
		DAY(20*60*60*24),
		YEAR(20*60*60*24*365.25);

		public final double value;

		/**
		 Construct value
		 @param value double
		 */
		TicksToUnit(double value) {
			this.value = value;
		}
	}
}
