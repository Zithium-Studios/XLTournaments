package net.zithium.tournaments.utility;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"UnusedReturnValue", "ConstantConditions", "unused", "deprecation"})
public class ItemStackBuilder {

    private final ItemStack ITEM_STACK;
    private static final Multimap<Attribute, AttributeModifier> EMPTY_ATTRIBUTES_MAP =
            MultimapBuilder.hashKeys().hashSetValues().build();

    public ItemStackBuilder(ItemStack item) {
        this.ITEM_STACK = item;
    }

    public ItemStackBuilder(Material material) {
        this.ITEM_STACK = new ItemStack(material);
    }


    public static ItemStackBuilder getItemStack(ConfigurationSection section, Player player) {
        ItemStack item = XMaterial.matchXMaterial(section.getString("material").toUpperCase()).get().parseItem();

        if (item.getType() == XMaterial.PLAYER_HEAD.get() && section.contains("base64")) {
            item = Base64Util.getBaseHead(section.getString("base64")).clone();
        }

        ItemStackBuilder builder = new ItemStackBuilder(item);

        if (section.contains("amount")) {
            builder.withAmount(section.getInt("amount"));
        }

        if (section.contains("username") && player != null) {
            builder.setSkullOwner(section.getString("username").replace("%player%", player.getName()));
        }

        if (section.contains("display_name")) {
            builder.withName(section.getString("display_name"), player);
        }

        if (section.contains("lore")) {
            builder.withLore(section.getStringList("lore"), player);
        }


        if (section.contains("glow") && section.getBoolean("glow")) {
            builder.withGlow();
        }

        if (section.contains("model_data")) {
            builder.withCustomData(section.getInt("model_data"));
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

    public static ItemStackBuilder getItemStack(ConfigurationSection section) {
        return getItemStack(section, null);
    }

    public ItemStackBuilder withAmount(int amount) {
        ITEM_STACK.setAmount(amount);
        return this;
    }

    public ItemStackBuilder withFlags(ItemFlag... flags) {
        ItemMeta meta = ITEM_STACK.getItemMeta();


        if (meta != null) {
            meta.addItemFlags(flags);
            for (ItemFlag itemFlag : flags) {
                if (itemFlag == ItemFlag.HIDE_ATTRIBUTES) {
                    meta.setAttributeModifiers(EMPTY_ATTRIBUTES_MAP);
                    break;
                }
            }
        }

        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withName(String name) {
        return withName(name, null);
    }

    public ItemStackBuilder withName(String name, @Nullable Player player) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        if (ITEM_STACK.getType() == XMaterial.matchXMaterial(Material.AIR).get()) {
            return this;
        }
        String parsedName = parsePlaceholders(player, name);
        meta.setDisplayName(ColorUtil.color(parsedName));
        ITEM_STACK.setItemMeta(meta);
        return this;
    }


    public ItemStackBuilder setSkullOwner(String owner) {
        try {
            SkullMeta im = (SkullMeta) ITEM_STACK.getItemMeta();
            OfflinePlayer player = Bukkit.getOfflinePlayer(owner);
            im.setOwningPlayer(player);
            ITEM_STACK.setItemMeta(im);
        } catch (ClassCastException ignored) {
        }
        return this;
    }

    public ItemStackBuilder withLore(List<String> lore) {
        return withLore(lore, null);
    }

    public ItemStackBuilder withLore(List<String> lore, @Nullable Player player) {
        ItemMeta meta = ITEM_STACK.getItemMeta();
        List<String> coloredLore = new ArrayList<>();

        if (ITEM_STACK.getType() == XMaterial.matchXMaterial(Material.AIR).parseMaterial()) {
            return this;
        }

        for (String s : lore) {
            String parsedLine = parsePlaceholders(player, s);
            coloredLore.add(ColorUtil.color(parsedLine));
        }

        meta.setLore(coloredLore);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }


    public ItemStackBuilder withCustomData(int data) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.setCustomModelData(data);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withGlow() {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ITEM_STACK.setItemMeta(meta);
        ITEM_STACK.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);
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

    private String parsePlaceholders(@Nullable Player player, String text) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null && player != null) {
            try {
                return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text);
            } catch (Exception ignored) {
                // Ignore errors from PlaceholderAPI calls as we don't care about them.
            }
        }
        return text;
    }

}