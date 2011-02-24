package com.nijikokun.bukkit.Cleaner;

import com.nijikokun.bukkit.General.General;
import com.nijikokun.bukkit.Permissions.Permissions;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * Cleaner 1.x
 * Copyright (C) 2011  Nijikokun <nijikokun@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class Cleaner extends JavaPlugin {
    /*
     * Central Data pertaining directly to the plugin name & versioning.
     */
    public static String name = "Cleaner";
    public static String codename = "Hit";
    public static String version = "1.7";

    /**
     * Grab the logging system to attach to.
     */
    protected static final Logger log = Logger.getLogger("Minecraft");

    /**
     * Listener for the plugin system.
     */
    public Listener l = new Listener(this);

    /*
     * Data locations
     */
    public static String main_directory = "Cleaner" + File.separator;

    /**
     * Item names
     */
    public static HashMap<String,String> items;

    /**
     * Internal Properties controllers
     */
    public static iProperty Settings, Items;

    /**
     * Miscellaneous object for various functions that don't belong anywhere else
     */
    public static Misc Misc = new Misc();
    
    /**
     * Controller for permissions and security.
     */
    public static Permissions Permissions = null;

    /*
     * Variables
     */
    public static boolean debugging;

    public Cleaner(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);

        registerEvents();
	log.info(Messaging.bracketize(name) + " version " + Messaging.bracketize(version) + " ("+codename+") loaded");
    }

    public void onDisable() {
	log.info(Messaging.bracketize(name) + " version " + Messaging.bracketize(version) + " ("+codename+") disabled");
    }

    public void onEnable() {
	setup();
	setupCommands();
	setupPermissions();
	setupItems();
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND, l, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, l, Priority.Normal, this);
    }

    /**
     * Setup variables, directories, and data.
     */
    public void setup() {
	// File Data
	Items = new iProperty("items.db");
    }

    /**
     * Setup Commands
     */
    public void setupCommands() { }

    /**
     * Setup Permissions that need to be watched throughout the listener.
     */
    public void setupPermissions() {
	Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");

	if(this.Permissions == null) {
	    if(test != null) {
		this.Permissions = (Permissions)test;
	    } else {
		log.info(Messaging.bracketize(name) + " Permission system not enabled. Disabling plugin.");
		this.getServer().getPluginManager().disablePlugin(this);
	    }
	}
    }

    /**
     * Setup Items
     */
    public void setupItems() {
	Map mappedItems = null;
	items = new HashMap<String, String>();

	try {
	    mappedItems = Items.returnMap();
	} catch (Exception ex) {
	    log.info(Messaging.bracketize(name + " Flatfile") + " could not grab item list!");
	}

	if(mappedItems != null) {
	    for (Object item : mappedItems.keySet()) {
		String id = ((String)item);
		String itemName = (String) mappedItems.get(item);

		if(debugging) {
		    log.info("Item #["+id+"] loaded as ["+itemName+"]");
		}

		items.put(id, itemName);
	    }
	}
    }
}
