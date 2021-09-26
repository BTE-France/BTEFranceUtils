# ![Logo](https://i.imgur.com/1ZPB2Wt.png) BTEFranceUtils
A Spigot plugin for Minecraft 1.12.2 for the BTE France server. 
This plugin implements a few features that either did not have any existing implementations or that were simple enough that we preferred to group them all in one plugin we control for better maintainability of our infrastructure. 
Some of these features are closely linked to the rest of our server setup and will not work as drop in solution. As such, no compiled binary is provided. 

## Features
- Customizable public and private welcome messages to great users
- Banroulette: a 21st century solution to staff boredom
- Minecraft to Discord schematic synchronization

## Plugins and Mods Dependencies
- Bukkit API server implementation with Forge support (We use [Mohist](https://mohistmc.com)) 
- [DiscordSRV](https://www.spigotmc.org/resources/discordsrv.18494/) 
- [Terra++](https://www.curseforge.com/minecraft/mc-mods/terraplusplus) 
- [BTE-France WorldEdit](https://github.com/BTE-France/BTE-France-WorldEdit) (Our customized world edit, with additional hooks and patches)

## Direct library dependencies
- [nominatim-api](https://github.com/jeremiehuchet/nominatim-Java-api) Java nominatim client, LGPLv3.0
