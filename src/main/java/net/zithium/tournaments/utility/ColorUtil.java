package net.zithium.tournaments.utility;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyFormat;

import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class ColorUtil {


    private static final LegacyComponentSerializer LEGACY_HEX_SERIALIZER = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build();

    private static final char[] LEGACY_COLOR_CODES = {
            // Colors
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B', 'C', 'D', 'E', 'F',
            // Decorations
            'k', 'l', 'm', 'n', 'o', 'r', 'K', 'L', 'M', 'N', 'O', 'R'
    };

    private static final Pattern LEGACY_RGB_PATTERN = Pattern.compile(
            "[§&]x[§&]([0-9a-fA-F])[§&]([0-9a-fA-F])[§&]([0-9a-fA-F])[§&]([0-9a-fA-F])[§&]([0-9a-fA-F])[§&]([0-9a-fA-F])");

    private static final Pattern LEGACY_PLUGIN_RGB_PATTERN = Pattern.compile(
            "[§&]#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])");
    private static final String RGB_REPLACEMENT = "<#$1$2$3$4$5$6>";



    public static String color(String message) {
        Component component = MiniMessage.miniMessage().deserialize(replaceLegacyWithTags(message.replace('\u00A7', '&')));
        return LEGACY_HEX_SERIALIZER.serialize(component);
    }

    public static String replaceLegacyWithTags(String input) {
        String output = LEGACY_RGB_PATTERN.matcher(input).replaceAll(RGB_REPLACEMENT);
        output = LEGACY_PLUGIN_RGB_PATTERN.matcher(output).replaceAll(RGB_REPLACEMENT);

        for (char legacyCode : LEGACY_COLOR_CODES) {
            LegacyFormat format = LegacyComponentSerializer.parseChar(legacyCode);
            if (format == null) continue;

            if (format.color() != null)
                output = output.replaceAll("[&§]" + legacyCode, "<" + format.color().asHexString() + ">");

            if (format.decoration() != null)
                output = output.replaceAll("[&§]" + legacyCode, "<" + format.decoration().name() + ">");

            if (format.reset())
                output = output.replaceAll("[&§]" + legacyCode, "<reset>");
        }

        return output;
    }
}
