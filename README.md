Amidst
======

[![Build Status](https://travis-ci.org/toolbox4minecraft/amidst.svg)](https://travis-ci.org/toolbox4minecraft/amidst)

Advanced Minecraft Interface and Data/Structure Tracking

Where can I get Amidst?
-----------------------

You can download Amidst [here](https://github.com/toolbox4minecraft/amidst/releases/latest). If you find any bugs, please [report](https://github.com/toolbox4minecraft/amidst/issues/new) them so we can fix them. If you want to request a feature, you can to this [here](https://github.com/toolbox4minecraft/amidst/issues/new). If you want to help develop Amidst, please get in contact [here](https://github.com/toolbox4minecraft/amidst/issues/new). Lastly, [here](https://github.com/toolbox4minecraft/amidst/blob/refactoring/docs/BUILDING.md) is a description how you can build Amidst by yourself.

What is Amidst?
---------------

What it **can** do for you:

* generate an overview of a Minecraft world from a given seed and a given Minecraft version
* display biome information
* display slime chunks
* display structures
  * default world spawn
  * strongholds
  * desert temples
  * jungle temples
  * witch huts
  * villages
  * ocean monuments
  * nether fortresses

When the world is loaded from a Minecraft world file, Amidst **can** also:

* display singleplayer and multiplayer player locations
* load player skins
* move players to another location, including the y-coordinate

What it **cannot** do for you:

* display changes to the world, that are made after the world generator was finished, this includes
  * changes made by world editors like MCEdit
  * changes made while loading the world in Minecraft
* find individual blocks or mobs in the world like e.g.
  * diamond ore
  * cows
  
More features include:

* saving an image of the map

Which Minecraft versions are supported?
---------------------------------------

We support Minecraft versions from 1.0 up to the latest snapshot. If you find an issue with a specific Minecraft version, [please report it](https://github.com/toolbox4minecraft/amidst/issues/new).

How can I move a player?
------------------------

You can move players in a world that was loaded from a Minecraft world file like so:

* scroll to and right-click on the new player location, this opens a popup menu
* select the player you want to move
* enter the new y-coordinate
* save player locations

**WARNING: This will change the contents of the save folder, so there is a chance that the world gets corrupted. We try to minimize the risk by creating a backup of the changed file, before it is changed. If the backup fails, we will not write the changes. You can find the backup files in a sub folder of the world, named 'amidst_backup'. Especially, make sure to not have the world loaded in minecraft during this process.**

When I load a world file I am asked what whether I want to load the Singleplayer of Multiplayer players. What does it mean?
---------------------------------------------------------------------------------------------------------------------------

Minecraft worlds have three different locations to store player information:

* the `level.dat` file contains the singleplayer player
* the `players` directory contains all multiplayer players by name, this was used before Minecraft 1.7.6
* the `playerdata` directory contains all multiplayers players by uuid, this is used since Minecraft 1.7.6

If the `players` and the `playerdata` directory exist, we will simply ignore the `players` directory, since it contains outdated information. However, other situations cannot be decided automatically. If the world was only used by a server, there will be no player information in the `level.dat`, so we will just load the multiplayer players. However, if the map was ever loaded as a singleplayer world, the `level.dat` file will create singleplayer information. Also, the `playerdata` directory will contain information about all the players that used the world as singleplayer world. Of course we could just display the singleplayer player and the multiplayer players, however this might lead to an issue when you want to move the singleplayer player. When the world is loaded as singleplayer world, Minecraft will simply ignore and overwrite the information in the multiplayer directory, that belongs to the player that opened the world. Thus, if you move your player instead of the singleplayer player, this will have no effect.

**tl;dr** If you use the world just as a singleplayer world, simply choose Singleplayer.

What is my internet connection used for?
----------------------------------------

* Amidst will use web services provided by Mojang, e.g. to
  * display information about Minecraft versions
  * display information about players like the name or the skin
* Amidst will check for updates on every start
* Amidst will not track you with Google Analytics (this was the case up to version v3.7)

Legal information
-----------------

Amidst is not owned by or related to Mojang in any way.

Amidst comes with ABSOLUTELY NO WARRANTY. It is free and open source software, license under the GPLv3, 
[see the license text](https://github.com/toolbox4minecraft/amidst/blob/master/LICENSE.txt). You are welcome to redistribute it under certain conditions.
