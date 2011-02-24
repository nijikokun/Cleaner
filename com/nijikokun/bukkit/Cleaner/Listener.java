package com.nijikokun.bukkit.Cleaner;


import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;


/**
 * iListen.java
 * <br /><br />
 * Listens for calls from hMod, and reacts accordingly.
 * 
 * @author Nijikokun <nijikokun@gmail.com>
 */
public class Listener extends PlayerListener {

    /**
     * Miscellaneous object for various functions that don't belong anywhere else
     */
    public Misc Misc = new Misc();
    
    /**
     * Saved inventories
     */
    public HashMap<String, ItemStack[]> saved = new HashMap<String, ItemStack[]>();

    public static Cleaner plugin;

    public Listener(Cleaner instance) {
        plugin = instance;
    }

    private void Cleanse(Player player, boolean force) {
	PlayerInventory inventory = player.getInventory();

	for(int i = 0; i < 40; i++) {
	    if(inventory.getItem(i) == null) { continue; }

	    if(i < 9 && !force) {
		continue;
	    } else {
		inventory.clear(i);
	    }
	}
    }

    /**
     * Commands sent from in game to us.
     *
     * @param player The player who sent the command.
     * @param split The input line split by spaces.
     * @return <code>boolean</code> - True denotes that the command existed, false the command doesn't.
     */
    public void onPlayerCommand(PlayerChatEvent event) {
        String[] split = event.getMessage().split(" ");
	String base = split[0];
        Player player = event.getPlayer();
	int itemInHand = event.getPlayer().getItemInHand().getTypeId();
	String playerName = player.getName();
	PlayerInventory inventory = player.getInventory();
	Messaging.save(player);

	if(Misc.is(base, "/wipe")) {
	    if (!Cleaner.Permissions.Security.permission(player, "cleaner.wipe")) {
		Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
		event.setCancelled(true); return;
	    }

	    boolean force = false;

	    if(Misc.argumentsAre(split, 1)) {
		if(Misc.is(split[1], "-f")) {
		    force = true;
		}
	    }

	    for(Player currently : plugin.getServer().getOnlinePlayers()) {
		Cleanse(currently, force);
	    }

	    Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] Cleaned all player's inventory"+((force) ? " and quickbar" : "")+"!"));
	    event.setCancelled(true); return;
	}

	if(Misc.is(base, "/clean")) {
	    if (!Cleaner.Permissions.Security.permission(player, "cleaner.clean")) {
		Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
		event.setCancelled(true); return;
	    }

	    Player current = null;
	    boolean force = false;


	    if(Misc.argumentsAre(split, 1) || Misc.argumentsAre(split, 2)) {

		current = Misc.player(split[1]);

		if(Misc.is(split[1], "-f")) {
		    force = true;
		} else {
		    if(Misc.argumentsAre(split, 2)) {
			if(Misc.is(split[2], "-f")) {
			    force = true;
			}
		    }

		    if(current == null) {
			Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, <white>"+split[1]+"<rose> is not online!"));
			event.setCancelled(true); return;
		    }

		    if (!Cleaner.Permissions.Security.permission(player, "cleaner.clean.player")) {
			Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
			event.setCancelled(true); return;
		    }
		}
	    }

	    if(force) {
		if(current == null) {
		    if (!Cleaner.Permissions.Security.permission(player, "cleaner.clean.force")) {
			Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
			event.setCancelled(true); return;
		    }
		} else {
		    if (!Cleaner.Permissions.Security.permission(player, "cleaner.clean.player.force")) {
			Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
			event.setCancelled(true); return;
		    }
		}
	    }

	    Cleanse((current == null) ? player : current, force);
	    
	    if(current == null) {
		Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] Inventory"+((force) ? " and quickbar" : "")+" cleaned!"));
	    } else {
		Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] Cleaned <white>"+current.getName()+"'s<yellow> inventory"+((force) ? " and quickbar" : "")+"!"));
	    }

	    event.setCancelled(true); return;
	}
	
	if(Misc.is(base, "/preview")) {
	    if (!Cleaner.Permissions.Security.permission(player, "cleaner.preview")) {
		Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
		event.setCancelled(true); return;
	    }
	    
	    if(Misc.argumentsAre(split, 1)) {
		Player current = Misc.player(split[1]);

		if(current == null) {
		    Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, <white>"+split[1]+"<rose> is not online!"));
		    event.setCancelled(true); return;
		}

		PlayerInventory currently = current.getInventory();

		if(current.getName().equalsIgnoreCase(playerName)) {
		    Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you cannot preview your own inventory!"));
		    event.setCancelled(true); return;
		}

		if(!saved.containsKey(player.getName())) {
		    saved.put(player.getName(), inventory.getContents());
		}

		Cleanse(player, true);

		for(int i = 0; i < 40; i++) {
		    ItemStack item = currently.getItem(i);

		    if(item == null || item.getTypeId() == 0) { continue; }

		    inventory.setItem(i, currently.getItem(i));
		}

		Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] Now viewing <white>"+current.getName()+"'s<yellow> inventory!"));
	    } else {
		Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you must declare a player at least!"));
		Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Usage: <white>/preview <player>"));
	    }

	    event.setCancelled(true); return;
	}

	if(Misc.is(base, "/restore")) {
	    if (!Cleaner.Permissions.Security.permission(player, "cleaner.preview.restore")) {
		Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
		event.setCancelled(true); return;
	    }

	    if(saved.containsKey(player.getName())) {

		Cleanse(player, true);

		for(ItemStack item : saved.get(player.getName())) {
		    if(item == null || item.getTypeId() == 0) { continue; }
		    inventory.setItem(inventory.firstEmpty(), item);
		}

		saved.remove(player.getName());

		Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] Inventory restored!"));
	    } else {
		Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, no inventory is stored for you!"));
	    }

	    event.setCancelled(true); return;
	}

	if(Misc.is(base, "/replace")) {
	    if (!Cleaner.Permissions.Security.permission(player, "cleaner.replace")) {
		Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
		event.setCancelled(true); return;
	    }

	    ArrayList<String> players = new ArrayList<String>();

	    if(Misc.argumentsAre(split, 1)) {
		Player current = Misc.player(split[1]);
		PlayerInventory currently = current.getInventory();

		if(current == null) {
		    Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, <white>"+split[1]+"<rose> is not online!"));
		    event.setCancelled(true); return;
		}

		if (!Cleaner.Permissions.Security.permission(player, "cleaner.replace.player")) {
		    Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
		    event.setCancelled(true); return;
		}

		if(current.getName().equalsIgnoreCase(playerName)) {
		    Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you cannot replace your own inventory!"));
		    event.setCancelled(true); return;
		}

		for(int i = 0; i < 40; i++) {
		    ItemStack item = inventory.getItem(i);

		    if(item == null) {
			continue;
		    }

		    currently.remove(i);
		    int amount = item.getAmount();
		    currently.setItem(i, item);
		}

		Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] "+current.getName()+" had their inventory replaced with yours!"));
		event.setCancelled(true); return;
	    }

	    if(Misc.argumentsAre(split, 2) || Misc.argumentsAre(split, 3)) {
		Player current = null;
		int id = 1;
		int with = 1;

		if(Misc.argumentsAre(split, 2)) {
		    if (!Cleaner.Permissions.Security.permission(player, "cleaner.replace.items")) {
			Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
			event.setCancelled(true); return;
		    }
		    
		    id = Items.validate(split[1]);
		    with = Items.validate(split[2]);

		    if(id == -1) {
			Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, <white>"+split[1]+"<rose> is not a valid item id!"));
			event.setCancelled(true); return;
		    }

		    if(with == -1) {
			Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, <white>"+split[2]+"<rose> is not a valid item id!"));
			event.setCancelled(true); return;
		    }

		    for(Player currently : plugin.getServer().getOnlinePlayers()) {
			boolean replaced = false;
			if(currently == null) { continue; }
			inventory = currently.getInventory();

			for(int i = 0; i < 40; i++) {
			    ItemStack item = inventory.getItem(i);
			    if(item == null) { continue; }
			    if(item.getTypeId() != id) { continue; }

			    int amount = item.getAmount();
			    inventory.remove(i);
			    inventory.setItem(i, new ItemStack(with, amount));
			    replaced = true;
			}

			if(replaced) {
			    players.add("&f" + currently.getName() + "&f");
			}
		    }

		    if(players.isEmpty()) {
			Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] No players had <white>"+Items.name(id)+"<yellow> to replace!"));
			event.setCancelled(true); return;
		    }

		    Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] Replaced <white>"+Items.name(id)+"<yellow> with <white>"+Items.name(with)+"<yellow> on players:"));
		    Messaging.send(players.toString());
		    event.setCancelled(true); return;
		}

		if(Misc.argumentsAre(split, 3)) {
		    boolean replaced = false;
		    current = Misc.player(split[1]);
		    id = Items.validate(split[2]);
		    with = Items.validate(split[3]);

		    if(current == null) {
			Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, <white>"+split[1]+"<rose> is not online!"));
			event.setCancelled(true); return;
		    }

		    if (!Cleaner.Permissions.Security.permission(player, "cleaner.replace.player.items")) {
			Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
			event.setCancelled(true); return;
		    }

		    if(id == -1) {
			Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, <white>"+split[1]+"<rose> is not a valid item id!"));
			event.setCancelled(true); return;
		    }

		    if(with == -1) {
			Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, <white>"+split[2]+"<rose> is not a valid item id!"));
			event.setCancelled(true); return;
		    }

		    inventory = current.getInventory();

		    for(int i = 0; i < 40; i++) {
			ItemStack item = inventory.getItem(i);
			if(item == null) { continue; }
			if(item.getTypeId() != id) { continue; }

			int amount = item.getAmount();
			inventory.remove(i);
			inventory.setItem(i, new ItemStack(with, amount));
			replaced = true;
		    }

		    if(!replaced) {
			Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] "+current.getName()+" did not have any <white>"+Items.name(id)+"<yellow> to replace!"));
			event.setCancelled(true); return;
		    }

		    Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] "+current.getName()+" had <white>"+Items.name(id)+"<yellow> replaced with <white>"+Items.name(with)+"<yellow>!"));
		    event.setCancelled(true); return;
		}
	    }


	    Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Invalid arguments given."));
	    Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Usage: /replace [player]."));
	    Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Usage: /replace [player] [item] [with]."));
	    Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Usage: /replace [item] [with]."));
	    event.setCancelled(true); return;
	}

	if(Misc.is(base, "/scan")) {
	    if (!Cleaner.Permissions.Security.permission(player, "cleaner.scan")) {
		Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
		event.setCancelled(true); return;
	    }

	    ArrayList<String> players = new ArrayList<String>();
	    boolean remove = false;
	    boolean fully = false;
	    boolean verbose = false;
	    Player current = null;
	    int id = 1;
	    int amount = 1;

	    if(Misc.argumentsAre(split, 1) || Misc.argumentsAre(split, 2) || Misc.argumentsAre(split, 3) || Misc.argumentsAre(split, 4)) {
		current = Misc.player(split[1]);

		if(current == null) {
		    if (!Cleaner.Permissions.Security.permission(player, "cleaner.scan.items")) {
			Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
			event.setCancelled(true); return;
		    }

		    id = Items.validate(split[1]);

		    if(id == -1) {
			Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, <white>"+split[1]+"<rose> is not a valid item id!"));
			event.setCancelled(true); return;
		    }

		    if(Misc.argumentsAre(split, 2)) {
			if(Misc.isEither(split[2], "remove", "-r")) {
			    remove = true;
			} else if(Misc.isEither(split[2], "force", "-f")) {
			    if (!Cleaner.Permissions.Security.permission(player, "cleaner.scan.remove.force")) {
				Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
				event.setCancelled(true); return;
			    }

			    fully = true;
			} else {
			    amount = Integer.valueOf(split[2]);

			    if(amount < 1) {
				amount = 1;
			    }
			}
		    }

		    if(Misc.argumentsAre(split, 3)) {
			if(Misc.isEither(split[3], "remove", "-r")) {
			    if (!Cleaner.Permissions.Security.permission(player, "cleaner.scan.remove")) {
				Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
				event.setCancelled(true); return;
			    }

			    remove = true;
			    remove = false;
			} else if(Misc.isEither(split[3], "force", "-f")) {
			    if (!Cleaner.Permissions.Security.permission(player, "cleaner.scan.remove.force")) {
				Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
				event.setCancelled(true); return;
			    }

			    fully = true;
			    remove = false;
			} else {
			    amount = Integer.valueOf(split[3]);

			    if(amount < 1) {
				amount = 1;
			    }
			}
		    }
		} else {
		    if (!Cleaner.Permissions.Security.permission(player, "cleaner.scan.player")) {
			Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
			event.setCancelled(true); return;
		    }

		    if(Misc.argumentsAre(split, 2)) {
			id = Items.validate(split[2]);

			if(id == -1) {
			    Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, <white>"+split[2]+"<rose> is not a valid item id!"));
			    event.setCancelled(true); return;
			}
		    } else {
			verbose = true;
		    }

		    if(Misc.argumentsAre(split, 3)) {
			if(Misc.isEither(split[3], "remove", "-r")) {
			    if (!Cleaner.Permissions.Security.permission(player, "cleaner.scan.player.remove")) {
				Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
				event.setCancelled(true); return;
			    }

			    remove = true;
			} else if(Misc.isEither(split[3], "force", "-f")) {
			    if (!Cleaner.Permissions.Security.permission(player, "cleaner.scan.player.remove.force")) {
				Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
				event.setCancelled(true); return;
			    }

			    fully = true;
			} else {
			    amount = Integer.valueOf(split[3]);

			    if(amount < 1) {
				amount = 1;
			    }
			}
		    }

		    if(Misc.argumentsAre(split, 4)) {
			if(Misc.isEither(split[4], "remove", "-r")) {
			    if (!Cleaner.Permissions.Security.permission(player, "cleaner.scan.player.remove")) {
				Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
				event.setCancelled(true); return;
			    }

			    remove = true;
			    fully = false;
			} else if(Misc.isEither(split[4], "force", "-f")) {
			    if (!Cleaner.Permissions.Security.permission(player, "cleaner.scan.player.remove.force")) {
				Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
				event.setCancelled(true); return;
			    }

			    fully = true;
			    remove = false;
			} else {
			    amount = Integer.valueOf(split[4]);

			    if(amount < 1) {
				amount = 1;
			    }
			}
		    }
		}
	    }

	    if(current == null) {
		if (!Cleaner.Permissions.Security.permission(player, "cleaner.scan.items")) {
		    Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] <rose>Sorry, you do not have permission to do this!"));
		    event.setCancelled(true); return;
		}
		
		for(Player currently : plugin.getServer().getOnlinePlayers()) {
		    if(currently == null) { continue; }
		    inventory = currently.getInventory();

		    int has = Items.hasAmount(currently, id);

		    if(has >= amount) {
			players.add("&f" + currently.getName() + "&f (&c"+has+"&f)");

			if(remove) {
			    Items.remove(currently, id, amount);
			}

			if(fully) {
			    Items.remove(currently, id, has);
			}
		    }
		}

		if(players.isEmpty()) {
		    Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] No players have <white>"+Items.name(id)+"<yellow> min-amount <white>[<yellow>"+amount+"<white>]!"));
		    event.setCancelled(true); return;
		}

		if(!remove && !fully) {
		    Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] Players containing <white>[<yellow>"+amount+"<white>] <white>"+Items.name(id)+"<yellow>:"));
		} else if(remove) {
		    Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] Removed  <white>[<yellow>"+amount+"<white>] <white>"+Items.name(id)+"<yellow> from players:"));
		} else if(fully) {
		    Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] Removed <white>"+Items.name(id)+"<yellow> from players:"));
		}

		Messaging.send(
			players.toString()
		);
	    } else {
		inventory = current.getInventory();
		if(verbose) {
		    ArrayList<Integer> went = new ArrayList<Integer>();

		    for(ItemStack item : inventory.getContents()) {
			if(item == null) { continue; }
			id = item.getTypeId();
			if(went.contains(id)) { continue; }
			int has = Items.hasAmount(current, id);

			boolean isWater = (id == 8) || (id == 9);
			boolean isLava = (id == 10) || (id == 11);
			boolean isTNT = (id == 46);
			boolean isAdminium = (id == 7);

			players.add("&"+ ((isTNT || isWater || isLava || isAdminium) ? "c" : "f") + Items.name(id) + "&f (&c"+has+"&f)");
			went.add(id);
		    }

		    Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] Verbose scanning of <white>"+current.getName()+"'s<yellow> inventory:"));
		    
		    if(!players.isEmpty()) {
			Messaging.send(players.toString());
		    } else {
			Messaging.send(Messaging.colorize("Inventory is empty."));
		    }
		} else {
		    int has = Items.hasAmount(current, id);
		    if(has >= amount) {
			if(remove) {
			    Items.remove(current, id, amount);
			}

			if(fully) {
			    Items.remove(current, id, has);
			}

			if(!remove && !fully) {
			    Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] "+current.getName()+" has <white>"+Items.name(id)+"<yellow> min <white>[<yellow>"+amount+"<white>] total <white>[<yellow>"+has+"<white>]!"));
			} else if(remove) {
			    Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] Removed <white>[<yellow>"+amount+"<white>] <white>"+Items.name(id)+"<yellow> from "+current.getName()+" !"));
			} else if(fully) {
			    Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] Removed <white>"+Items.name(id)+"<yellow> from "+current.getName()+" !"));
			}
		    } else {
			Messaging.send(Messaging.colorize("<yellow>[<white>Cleaner<yellow>] "+current.getName()+" does not have any <white>"+Items.name(id)+"<yellow>!"));
		    }
		}
	    }

	    event.setCancelled(true); return;
	}
    }
}
