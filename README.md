# Jump and Run

A minecraft parkour speedrunning plugin made for [AuraGames Network](https://laby.net/server/auragames).

## Install

> Important: This plugin only works properly with the given modified [spigot.jar](https://github.com/7rebux/jump-and-run/blob/main/_server/spigot-1.8.8-edit.jar)!

> Depends on Multiverse-Core

1. Download the latest [release](https://github.com/7rebux/jump-and-run/releases/download/2.0.0/JumpAndRun.jar) and drag it into your plugins folder
2. Start your minecraft server
3. Update the config with your SQL server credentials
4. Reload the server
5. Have fun!

## Config

Name | Type | Description | Variables
--- | --- | --- | ---
resetHeight | Int | If the player falls below this height he will be teleported back to the last checkpoint | -
worldName | String | The name of the world | -
parkoursPerPage | Int | Amount of parkours that show on one page
database.* | * | SQL server credentials | -
timer.bar | String | Timer action bar text | `{time}`
timer.units.* | String | Timer unit text | -
difficulty.* | String | Difficulty text | -
items.* | String | Item names | -
messages.* | String | Message texts | [**See config file**](https://github.com/7rebux/jump-and-run/blob/main/src/main/kotlin/net/rebux/jumpandrun/config/PluginConfig.kt)


## Commands

- `/jumpandrun` `/jnr`
  - Permissons: `jumpandrun.manage`
  - Description: Add, remove and list jump and runs
- `/top`
  - Description: Lists the top 5 players of the current jump and run
