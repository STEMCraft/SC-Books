package com.stemcraft.storage;

import com.stemcraft.BookData;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface BookDataStorage {

    /**
     * Return a list of known books in storage
     *
     * @return The list of book names.
     */
    List<String> list();

    /**
     * Checks if a book exists on the server
     *
     * @param name   The name of the book to check.
     * @return If the book name exists.
     */
    boolean exists(String name);

    /**
     * Load the book from storage
     *
     * @param name     The book to load.
     * @return The book as an ItemStack or NULL.
     */
    BookData get(String name);

    /**
     * Save a book to storage.
     *
     * @param data The book data.
     * @return If the book save was successful
     */
    boolean save(BookData data);


    /**
     * Delete a book from storage.
     *
     * @param name The book name.
     */
    void delete(String name);
}
