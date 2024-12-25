package com.stemcraft.commands;

import com.stemcraft.Books;
import com.stemcraft.common.STEMCraftCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;
import java.util.Map;

public class Book extends STEMCraftCommand {
    Books plugin;

    public Book(Books instance) {
        plugin = instance;

        tabComplete("new");
        tabComplete("save");
        tabComplete("get", "{book}");
        tabComplete("show", "{book}", "{player}");
        tabComplete("delete", "{book}");
    }

    @Override
    public void execute(CommandSender sender, String command, List<String> args) {
        String usage = "Usage: /books <new|save <book>|get <book> [player]|show <book> [player]|delete <book>>";

        if (!sender.hasPermission("stemcraft.books")) {
            message(sender, "You do not have permission to use this command");
            return;
        }

        if (args.isEmpty()) {
            message(sender, usage);
            return;
        }

        switch (args.getFirst().toLowerCase()) {
            case "new":
                executeNew(sender, args);
            case "save":
                executeSave(sender, args);
            case "get":
                executeGet(sender, args);
            case "show":
                executeShow(sender, args);
            case "delete":
                executeDelete(sender, args);
            default:
                message(sender, usage);
        }
    }

    public void executeNew(CommandSender sender, List<String> args) {
        if(!(sender instanceof Player) && args.size() < 2) {
            message(sender, "A player name is required when using this command from the console");
            return;
        }

        Player target;
        if (args.size() >= 2) {
            target = Bukkit.getServer().getPlayerExact(args.get(2));
            if (target == null) {
                message(sender, "The player {player} was not found or online", "player", args.get(2));
                return;
            }
        } else {
            target = (Player) sender;
        }

        Material material = Material.getMaterial("BOOK_AND_QUILL");
        if (material == null)
            material = Material.getMaterial("WRITABLE_BOOK");
        if (material == null)
            throw new UnsupportedOperationException("Something went wrong with Bukkit Material!");

        ItemStack item = new ItemStack(material);
        PlayerInventory inventory = target.getInventory();
        Map<Integer, ItemStack> result = inventory.addItem(item);
        if (!result.isEmpty()) {
            message(sender, "Cannot give {player} a new book as their inventory is full", "player", target.getName());
        } else {
            message(sender, "{player} received new book", "player", target.getName());
        }
    }

    public void executeSave(CommandSender sender, List<String> args) {
        if(!(sender instanceof Player player)) {
            message(sender, "This command is not supported from the console");
            return;
        }

        if(args.size() < 2) {
            message(sender, "Usage: /books save <book>");
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!(item.getType().toString().equals("BOOK_AND_QUILL")) && !(item.getType().toString().equals("WRITABLE_BOOK"))) {
            message(sender, "You are not holding a book and quill or writable book in your hand to save");
            return;
        }

        String name = args.get(2);
        if(plugin.save(item, name)) {
            message(sender, "The book {book} has been saved", "book", name);
        } else {
            message(sender, "There was an error saving the book {book}", "book", name);
        }
    }

    public void executeGet(CommandSender sender, List<String> args) {
        if(!(sender instanceof Player) && args.size() < 3) {
            message(sender, "A player name is required when using this command from the console");
            return;
        }

        if(args.size() < 2) {
            message(sender, "Usage: /books get <book> [player]");
            return;
        }

        String name = args.get(2);
        if(!plugin.exists(name)) {
            message(sender, "The book {book} does not exist", "book", name);
            return;
        }

        Player player;
        if (args.size() >= 3) {
            player = Bukkit.getServer().getPlayerExact(args.get(3));
            if (player == null) {
                message(sender, "The player {player} was not found or online", "player", args.get(2));
                return;
            }
        } else {
            if(!(sender instanceof Player)) {
                // should never get here...
                message(sender, "A player name is required when using this command from the console");
                return;
            }

            player = (Player) sender;
        }

        ItemStack item = plugin.get(name);
        if(item == null) {
            message(sender, "A server error occurred retrieving the book {book}", "book", name);
            return;
        }

        Map<Integer, ItemStack> result = player.getInventory().addItem(item);
        if (!result.isEmpty()) {
            message(sender, "Could not give the book {book} to the player {player} as their inventory is full", "book", name, "player", player.getName());
        } else {
            message(sender, "The player {player} received the book {book}", "book", name, "player", player.getName());
        }
    }

    public void executeShow(CommandSender sender, List<String> args) {
        if(!(sender instanceof Player) && args.size() < 3) {
            message(sender, "A player name is required when using this command from the console");
            return;
        }

        if(args.size() < 2) {
            message(sender, "Usage: /books show <book> [player]");
            return;
        }

        String name = args.get(2);
        if(!plugin.exists(name)) {
            message(sender, "The book {book} does not exist", "book", name);
            return;
        }

        Player player;
        if (args.size() >= 3) {
            player = Bukkit.getServer().getPlayerExact(args.get(3));
            if (player == null) {
                message(sender, "The player {player} was not found or online", "player", args.get(2));
                return;
            }
        } else {
            if(!(sender instanceof Player)) {
                // should never get here...
                message(sender, "A player name is required when using this command from the console");
                return;
            }

            player = (Player) sender;
        }

        ItemStack item = plugin.get(name);
        if(item == null) {
            message(sender, "A server error occurred retrieving the book {book}", "book", name);
            return;
        }

        if(plugin.isBedrockPlayer(player)) {
            plugin.itemAddAttrib(item, "destroy-on-drop", 1);

            Map<Integer, ItemStack> result = player.getInventory().addItem(item);
            if (!result.isEmpty()) {
                message(sender, "Could not give the book {book} to the player {player} as their inventory is full", "book", name, "player", player.getName());
            } else {
                message(sender, "The player {player} received the book {book}", "book", name, "player", player.getName());
            }

            BookMeta meta = (BookMeta) item.getItemMeta();
            message(player, "You have received the book {title}", "title", meta.getTitle());
        } else {
            player.openBook(item);
        }
    }

    public void executeDelete(CommandSender sender, List<String> args) {
        if(args.size() < 2) {
            message(sender, "Usage: /books delete <book>");
            return;
        }

        String name = args.get(2);
        if(!plugin.exists(name)) {
            message(sender, "The book {book} does not exist", "book", name);
            return;
        }

        plugin.delete(name);
        message(sender, "The book {book} has been deleted from the server", "book", name);
    }
}
