# DeathTracker
This Minecraft plugin tracks players' deaths and playtime. DeathTracker calculates death time (death density) and gives the top 10 statistics of these values.
Deathtracker works at least for Minecraft 1.17.1 (should work for earlier versions too, not tested) and with Spigotmc or Papermc.

## Features
- `/getdeaths`
  - gives the issuer its own deathdata(playtime and deaths
  - The command supports automatic tab completion
  - Permission: `deathtracker.getdeaths`
  - `/getdeaths [player]` OR `/getdeaths [player] deaths` 
    - Gives the amount of deaths the player has died
  - `/getdeaths [player] time`
    - Gives the deathrate and the approximate time to die
  - `/getdeaths total deaths`
    - Gives the amount of playtime and deaths the server has
  - `/getdeaths total time`
    - Gives the deathrate and the approximate time to die for the whole server
- `/deathstats`
  - Show the top 10 players on the specific type of death data
  - permission: `deathtracker.deathstats`
  - `/deathstats immortals`
    - Top 10 players who have not died (Player with the most playtime goes first)
  - `/deathstats deaths high`
    - Top 10 players with the most deaths (With identical deaths, the player who has the least playtime goes first)
  - `/deathstats deaths low`
    - Top 10 players with the least deaths (At least one death. With identical deaths, the player who has the least playtime goes first)
  - `/deathstats deathrate high` 
    - Top 10 players who die most often (Player with the biggest death/playtime value goes first)
  - `/deathstats deathrate high` 
    - Top 10 players who die least often (Player with the biggest playtime/deaths value goes first. Players who have zero deaths are not in this list)

## Planned features
- Language file reload
- Automatic language detector→localize the plugin to your own needs
- Command for resetting all or specific players' deathdata
- Set player deaths and playtime with commands


If this plugin doesn't work for you or you've found a bug or have a feature idea, make it a bug report/feature request on Issues tab. 
