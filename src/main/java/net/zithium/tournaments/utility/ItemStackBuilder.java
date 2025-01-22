package net.zithium.tournaments.utility;

import net.zithium.library.version.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemStackBuilder {

    private final ItemStack ITEM_STACK;

    public ItemStackBuilder(ItemStack item) {
        this.ITEM_STACK = item;
    }

    public ItemStackBuilder(Material material) {
        this(new ItemStack(material));
    }

    public static ItemStackBuilder getItemStack(ConfigurationSection section) {
        ItemStack item = XMaterial.matchXMaterial(section.getString("material").toUpperCase()).get().parseItem();

        if (item.getType() == XMaterial.PLAYER_HEAD.parseMaterial() && section.contains("base64")) {
            item = Base64Util.getBaseHead(section.getString("base64")).clone();
        }

        ItemStackBuilder builder = new ItemStackBuilder(item);

        if (section.contains("amount")) {
            builder.withAmount(section.getInt("amount"));
        }

        if (section.contains("display_name")) {
            builder.withName(section.getString("display_name"));
        }

        if (section.contains("lore")) {
            builder.withLore(section.getStringList("lore"));
        }

        if (section.contains("glow") && section.getBoolean("glow")) {
            builder.withGlow();
        }

        if (section.contains("custom_model_data")) {
            builder.withCustomModelData(section.getInt("custom_model_data"));
        }

        if (section.contains("item_flags")) {
            List<ItemFlag> flags = new ArrayList<>();
            section.getStringList("item_flags").forEach(text -> {
                try {
                    ItemFlag flag = ItemFlag.valueOf(text);
                    flags.add(flag);
                } catch (IllegalArgumentException ignored) {
                }
            });
            builder.withFlags(flags.toArray(new ItemFlag[0]));
        }

        return builder;
    }

    public ItemStackBuilder withAmount(int amount) {
        ITEM_STACK.setAmount(amount);
        return this;
    }

    public ItemStackBuilder withFlags(ItemFlag... flags) {
        ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.addItemFlags(flags);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withName(String name) {

        if (ITEM_STACK.getType() == Material.AIR) {
            return this;
        }

        final ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.setDisplayName(ColorUtil.color(name));
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withLore(List<String> lore) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();

        if (ITEM_STACK.getType() == Material.AIR) {
            return this;
        }

        List<String> coloredLore = new ArrayList<String>();
        for (String s : lore) {
            coloredLore.add(ColorUtil.color(s));
        }

        meta.setLore(coloredLore);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withCustomModelData(int data) {
        ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.setCustomModelData(data);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }


    public ItemStackBuilder withGlow() {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ITEM_STACK.setItemMeta(meta);
        ITEM_STACK.addUnsafeEnchantment(Enchantment.AQUA_AFFINITY, 1);
        return this;
    }

    private String replace(String message, Object... replacements) {
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 >= replacements.length) break;
            message = message.replace(String.valueOf(replacements[i]), String.valueOf(replacements[i + 1]));
        }

        return message;
    }

    public ItemStack build() {
        return ITEM_STACK;
    }
}
