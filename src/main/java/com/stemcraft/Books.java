package com.stemcraft;

import com.stemcraft.commands.Book;
import com.stemcraft.common.STEMCraftPlugin;
import com.stemcraft.storage.BookDataStorage;
import com.stemcraft.storage.YamlBookDataStorage;
import org.bukkit.inventory.ItemStack;

import java.util.List;


public class Books extends STEMCraftPlugin {
    private static Books instance;
    private BookDataStorage storage;

    @Override
    public void onEnable() {
        super.onEnable();

        instance = this;

        // Currently this plugin only supports YAML storage
        storage = new YamlBookDataStorage(instance);

        registerTabCompletion("book", this::list);

        registerCommand(new Book(instance));
    }

    public Books getInstance() {
        return instance;
    }

    /**
     * Save a book to storage.
     *
     * @param book   The book item stack to save.
     * @param name The book name.
     */
    public boolean save(ItemStack book, String name) {
        BookData data = BookData.fromItemStack(book, name);

        return storage.save(data);
    }

    /**
     * Save a book to storage.
     *
     * @param name The book name.
     * @param title The book title.
     * @param author The book author
     * @param pages The book pages
     * @return If the book save was successful
     */
    public boolean save(String name, String title, String author, List<String> pages) {
        BookData data = new BookData(name);

        data.setTitle(title);
        data.setAuthor(author);
        data.setPages(pages);

        return storage.save(data);
    }

    /**
     * Load the book from storage
     *
     * @param name     The book to load.
     * @return The book as an ItemStack or NULL.
     */
    public ItemStack get(String name) {
        BookData data = storage.get(name);

        return data.toItemStack();
    }

    /**
     * Checks if a book exists on the server
     *
     * @param name   The name of the book to check.
     * @return If the book name exists.
     */
    public boolean exists(String name) {
        return storage.exists(name);
    }

    /**
     * Return a list of known books in storage
     *
     * @return The list of book names.
     */
    public List<String> list() {
        return storage.list();
    }

    /**
     * Delete a book from storage
     *
     * @param name The book to delete.
     */
    public void delete(String name) {
        storage.delete(name);
    }
}
