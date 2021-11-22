package es.meriland.chat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.regex.Pattern;

public class Parser {

    // TODO: Revisar y mejorar la expressión
    private static final Pattern url = Pattern.compile("^(?:(https?)://)?([-\\w_.]{2,}\\.[a-z]{2,4})(/\\S*)?$");

    public static BaseComponent[] parse(boolean removeFirst, boolean allowColors, String... msg) {
        return parse(new ComponentBuilder(), removeFirst, allowColors, msg);
    }

    public static BaseComponent[] parse(ComponentBuilder builder, boolean removeFirst, boolean allowColors, String... msg) {
        for (int i = removeFirst ? 1 : 0; i < msg.length; i++) {
            if(url.matcher(msg[i]).find()) {
                BaseComponent link = new TextComponent(msg[i]);
                link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, msg[i]));
                builder.append(new TextComponent(" "), ComponentBuilder.FormatRetention.FORMATTING).append(link);
            } else if (allowColors) {
                builder.append(TextComponent.fromLegacyText(" " + ChatColor.translateAlternateColorCodes('&',msg[i])), ComponentBuilder.FormatRetention.FORMATTING);
            } else {
                builder.append(" " + msg[i], ComponentBuilder.FormatRetention.FORMATTING);
            }
        }

        return builder.create();
    }
}
