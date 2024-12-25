package com.stemcraft;

import com.stemcraft.exception.InvalidItem;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class BookData {
    private String name;
    private String title;
    private String author;
    private List<String> pages;

    public BookData(String name) {
        this.name = formatName(name);
    }

    public void setName(String name) {
        this.name = formatName(name);
    }

    public static BookData fromItemStack(ItemStack item, String name) {
        if (!Objects.equals(item.getType().toString(), "BOOK_AND_QUILL") && !Objects.equals(item.getType().toString(), "WRITABLE_BOOK")) {
            return null;
        }

        BookMeta meta = (BookMeta)item.getItemMeta();
        String title = meta.getTitle();
        String author = meta.getAuthor();
        List<String> pages = meta.pages().stream()
                .map(Component::toString)
                .toList();

        if(name == null || name.isEmpty() || name.isBlank()) {
            name = BookData.formatName(title);
        }

        BookData data = new BookData(name);
        data.setAuthor(author);
        data.setTitle(title);
        data.setPages(pages);

        return data;
    }

    public static BookData fromItemStack(ItemStack item) {
        return fromItemStack(item, null);
    }


    public ItemStack toItemStack() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        meta.setAuthor(author);
        meta.setTitle(title);

        for (String page : pages) {
            meta.addPages(Component.text(page));
        }

        book.setItemMeta(meta);
        return book;
    }

    private static String formatName(String name) {
        // Remove non-alpha characters
        name = name.replaceAll("[^a-zA-Z0-9\\s]", "");

        // Replace spaces with dashes
        name = name.replace(" ", "-");

        // Convert to lowercase
        name = name.toLowerCase();

        return name;
    }
}
