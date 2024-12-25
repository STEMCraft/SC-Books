package com.stemcraft.storage;

import com.stemcraft.BookData;
import com.stemcraft.Books;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class YamlBookDataStorage implements BookDataStorage {
    private final Books plugin;

    public YamlBookDataStorage(Books instance) {
        plugin = instance;
    }

    /**
     * Return a list of known books in storage
     *
     * @return The list of book names.
     */
    public List<String> list() {
        File bookFile = new File(plugin.getDataFolder(), "books.yml");
        if(bookFile.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(bookFile);
            ConfigurationSection booksSection = config.getConfigurationSection("books");

            if (booksSection != null) {
                Set<String> keys = booksSection.getKeys(false);
                return new ArrayList<>(keys);
            }
        }

        return new ArrayList<>();
    }


    /**
     * Checks if a book exists on the server
     *
     * @param name   The name of the book to check.
     * @return If the book name exists.
     */
    @Override
    public boolean exists(String name) {
        File bookFile = new File(plugin.getDataFolder(), "books.yml");
        if(bookFile.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(bookFile);

            return config.isConfigurationSection("books." + name.toLowerCase());
        }

        return false;
    }

    /**
     * Load the book from storage
     *
     * @param name     The book to load.
     * @return The book as an ItemStack or NULL.
     */
    @Override
    public BookData get(String name) {
        File bookFile = new File(plugin.getDataFolder(), "books.yml");
        if (bookFile.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(bookFile);

            ConfigurationSection bookSection = config.getConfigurationSection("books." + name);
            if (bookSection != null) {
                BookData data = new BookData(name);

                data.setTitle(bookSection.getString("title"));
                data.setAuthor(bookSection.getString("author"));
                data.setPages(bookSection.getStringList("pages"));

                return data;
            }
        }

        return null;
    }

    /**
     * Save a book to storage.
     *
     * @param data The book data.
     * @return If the book save was successful
     */
    @Override
    public boolean save(BookData data) {
        File bookFile = new File(plugin.getDataFolder(), "books.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(bookFile);

        config.set("books." + data.getName() + ".title", data.getTitle());
        config.set("books." + data.getName() + ".author", data.getAuthor());
        config.set("books." + data.getName() + ".pages", data.getPages());

        try {
            config.save(bookFile);
            return true;
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save book " + data.getName(), e);
            return false;
        }
    }

    /**
     * Delete a book from storage.
     *
     * @param name The book name.
     */
    @Override
    public void delete(String name) {
        File bookFile = new File(plugin.getDataFolder(), "books.yml");
        if (bookFile.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(bookFile);

            String sectionPath = "books." + name;
            if (config.isConfigurationSection(sectionPath)) {
                config.set(sectionPath, null); // Remove the section
                try {
                    config.save(bookFile);
                } catch (IOException e) {
                    plugin.getLogger().log(Level.SEVERE, "Failed to delete book " + name, e);
                }
            }
        }
    }
}
