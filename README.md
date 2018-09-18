<img src="https://avatars1.githubusercontent.com/u/16545761?s=150&v=4" align="right" />

Amidstest
=========

[![Build Status](https://travis-ci.org/Treer/amidstest.svg?branch=master)](https://travis-ci.org/Treer/amidstest)
 
## What is Amidstest?

Amidstest is Amidst converted to support [Minetest](https://www.minetest.net/) — a completely free and open implementation of Minecraft-like games, and a mod paradise.

Amidstest is a tool to display a map of a world (either Minetest or Minecraft), without actually creating it.

With Minetest worlds, it can:

* render an overview of a world from a given seed and mapgen version
* apply different biome sets to the world
* locate biomes
* view biomes as Voronoi diagrams (to aid designing/tuning biomes)
* calculate biome coverage of worlds
* save images of the map


The following has not *yet* been implemented for Minetest worlds:
* display dungeon locations
* locate and load any non-default advanced mapgen parameters you might be tweaking
* Player locations, and exact spawn location.

## Download

* [Releases](https://github.com/treer/amidstest/releases)

## Documentation
* [Installation](https://github.com/Treer/amidstest/wiki/Installation)
* [Getting or sharing more biome profiles](https://github.com/Treer/amidstest/wiki/Get-or-share-more-biome-profiles)
* [Dev info](https://github.com/Treer/amidstest/wiki/Development)

## Screenshots

![default](https://raw.githubusercontent.com/wiki/treer/amidstest/screenshots/carpathian2.png)

![default](https://raw.githubusercontent.com/wiki/treer/amidstest/screenshots/30-biomes_voronoi2.png)

![default](https://raw.githubusercontent.com/wiki/treer/amidstest/screenshots/v7_30-biomes1.png)

![default](https://raw.githubusercontent.com/wiki/treer/amidstest/screenshots/v7_default1.png)



----


## What is Amidst?

Amidst is the original tool for Minecraft which this project has built off. It is a tool to display an overview of a Minecraft world, without actually creating it.

Amidst **can**:

* render an overview of a world from a given seed and Minecraft version
* save an image of the map
* use a save game
* display biome information
* display slime chunks
* display end islands
* display the following structures
  * world spawn
  * strongholds
  * villages
  * witch huts
  * jungle temples
  * desert temples
  * igloos
  * abandoned mine shafts
  * ocean monuments
  * nether fortresses
  * end cities

Amidst **cannot**:

* display changes that were applied to a save game like
  * changes made by world editors like MCEdit
  * changes made while loading the world in Minecraft
* find individual blocks or mobs like
  * diamond ore
  * cows

## Amidst has found a new home

Amidst was moved to a new location, since Skidoodle aka skiphs is to busy to maintain it. It has also found some new developers. One of them is DrFrankenstone aka Treer who is the developer of AmidstExporter. Skidoodle is still an owner of Amidst and agreed to move the project.

### Links

* [Download](https://github.com/toolbox4minecraft/amidst/releases)
* [FAQ](https://github.com/toolbox4minecraft/amidst/wiki/FAQ)
* [Wiki](https://github.com/toolbox4minecraft/amidst/wiki)
* [Reporting a Bug](https://github.com/toolbox4minecraft/amidst/wiki/Supporting-the-Development#reporting-a-bug) - please report bugs, so we can fix them
* [Requesting a Feature](https://github.com/toolbox4minecraft/amidst/wiki/Supporting-the-Development#requesting-a-feature)
* [Thread in the minecraftforum](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-tools/2626547-amidst-has-found-a-new-home)
* [Project Page](https://github.com/toolbox4minecraft/amidst)
* [Supporting the Development](https://github.com/toolbox4minecraft/amidst/wiki/Supporting-the-Development)
* [License Text](https://github.com/toolbox4minecraft/amidst/blob/master/LICENSE.txt)

## What is my internet connection used for?

* Amidst **will** use web services provided by Mojang, e.g. to
  * display information about Minecraft versions.
  * display information about players like the name or the skin.
* Amidst **will** check for updates on every start.
* Amidst **will not** track you with Google Analytics, which was the case in older versions.

## Legal Information

* Amidst is **not** owned by or related to Mojang in any way.
* Amidst comes with **absolutely no warranty**.
* Amidst is free and open source software, licensed under the GPLv3.

## Screenshots

These screenshots are created from the seed 24922 using Amidst v4.0 and Minecraft 1.9.

![default](https://raw.githubusercontent.com/wiki/toolbox4minecraft/amidst/screenshots/screenshot_default_24922_default.png)

### The End Dimension

![The End Dimension](https://raw.githubusercontent.com/wiki/toolbox4minecraft/amidst/screenshots/screenshot_default_24922_end.png)

### Biome Highlighter

![Biome Highlighter](https://raw.githubusercontent.com/wiki/toolbox4minecraft/amidst/screenshots/screenshot_default_24922_biome-highlighter.png)

### Grid

![Grid](https://raw.githubusercontent.com/wiki/toolbox4minecraft/amidst/screenshots/screenshot_default_24922_grid.png)

### Slime Chunks

![Slime Chunks](https://raw.githubusercontent.com/wiki/toolbox4minecraft/amidst/screenshots/screenshot_default_24922_slime.png)
