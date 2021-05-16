# DeathTracker
This Minecraft plugin tracks players' deaths and playtime. DeathTracker calculates death time (death density) and gives the top 10 statistics of these values.
Deathtracker works at least for Minecraft 1.16.5 (should work for earlier versions too, not tested) and with Spigotmc or Papermc.

## Features
- `/getdeaths [player]` OR `/getdeaths [player] deaths`
  - Gives the amount of deaths the player has died
  - The command supports automatic tab completion
  - Permission: `deathtracker.getdeaths`

## Planned features
- Top 10 players with the most deaths (With identical deaths, the player who has the least playtime goes first)
- Top 10 players with the least deaths (At least one death. With identical deaths, the player who has the least playtime goes first)
- Top 10 players who die most often (Player with the biggest death/playtime value goes first)
- Top 10 players who die least often (Player with the biggest playtime/deaths value goes first. Players who have zero deaths are not in this list)
- Top 10 players who have not died (Player with the most playtime goes first)
