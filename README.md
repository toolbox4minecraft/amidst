Amidst
======

[![Build Status](https://travis-ci.org/toolbox4minecraft/amidst.svg)](https://travis-ci.org/toolbox4minecraft/amidst)

Advanced Minecraft Interface and Data/Structure Tracking

Important Links
---------------

* [Download Current Version](https://github.com/toolbox4minecraft/amidst/releases)
* [Download Old Version](https://github.com/skiphs/amidst/releases)
* [Report a Bug](https://github.com/toolbox4minecraft/amidst/issues/new) (please report bugs, so we can fix them)
* [Request a New Feature](https://github.com/toolbox4minecraft/amidst/issues/new)

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
* move players to another location, including the y-coordinate ([see how it works](https://github.com/toolbox4minecraft/amidst/blob/refactoring/docs/how-can-i-move-a-player.md))

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

What is my internet connection used for?
----------------------------------------

* Amidst will use web services provided by Mojang, e.g. to
  * display information about Minecraft versions
  * display information about players like the name or the skin
* Amidst will check for updates on every start
* Amidst will not track you with Google Analytics (this was the case up to version v3.7)

System Requirements
-------------------

Operating System: Any
Java Version: min 8

Running Amidst
--------------

Most users should be able to simply start Amidst like any other application. However, here is the command to execute the jar file:

	java -jar <filename>

Information for Developers
--------------------------

* [Get Involved](https://github.com/toolbox4minecraft/amidst/issues/new)
* [Building Amidst from Source Code](https://github.com/toolbox4minecraft/amidst/blob/refactoring/docs/building-amidst-from-source-code.md)

Current State of Amidst
-----------------------

Amidst moved from [the main repository](https://github.com/skiphs/AMIDST) by @skiphs to this repository for the following reasons:

* there was more than one main developer
* the organization has better permission management

Admist experienced many changes since the last release (3.7). Thus, we will probably need several pre-releases and testers before the next stable release. However, it contains several new features and bug fixes, which are not included in the original AMIDST. Also, it works with the Minecraft 1.9 shapshots.

We plan to integrate [the fork AmidstExporter](https://github.com/Treer/AmidstExporter) by @Treer back into Amidst. However, there might be more releases of AmidstExporter, before we are able to finish the merge, to make the new features of it available for the users.

Legal information
-----------------

Amidst is not owned by or related to Mojang in any way.

Amidst comes with ABSOLUTELY NO WARRANTY. It is free and open source software, license under the GPLv3, 
[see the license text](https://github.com/toolbox4minecraft/amidst/blob/master/LICENSE.txt). You are welcome to redistribute it under certain conditions.
