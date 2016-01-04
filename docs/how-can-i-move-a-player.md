How can I move a player?
========================

You can move players in a world that was loaded from a Minecraft world file like so:

* scroll to and right-click on the new player location, this opens a popup menu
* select the player you want to move
* enter the new y-coordinate
* save player locations

**WARNING: This will change the contents of the save folder, so there is a chance that the world gets corrupted. We try to minimize the risk by creating a backup of the changed file, before it is changed. If the backup fails, we will not write the changes. You can find the backup files in a sub folder of the world, named 'amidst_backup'. Especially, make sure to not have the world loaded in minecraft during this process.**

When I load a world file I am asked whether I want to load the Singleplayer or Multiplayer players. What does that mean?
---------------------------------------------------------------------------------------------------------------------------

Minecraft worlds have three different locations to store player information:

* the `level.dat` file contains the singleplayer player
* the `players` directory contains all multiplayer players by name, this was used before Minecraft 1.7.6
* the `playerdata` directory contains all multiplayers players by uuid, this is used since Minecraft 1.7.6

If the `players` and the `playerdata` directory exist, we will simply ignore the `players` directory, since it contains outdated information. However, other situations cannot be decided automatically. If the world was only used by a server, there will be no player information in the `level.dat`, so we will just load the multiplayer players. However, if the map was ever loaded as a singleplayer world, the `level.dat` file will create singleplayer information. Also, the `playerdata` directory will contain information about all the players that used the world as singleplayer world. Of course we could just display the singleplayer player and the multiplayer players, however this might lead to an issue when you want to move the singleplayer player. When the world is loaded as singleplayer world, Minecraft will simply ignore and overwrite the information in the multiplayer directory, that belongs to the player that opened the world. Thus, if you move your player instead of the singleplayer player, this will have no effect.

**tl;dr** If you use the world just as a singleplayer world, simply choose Singleplayer.
