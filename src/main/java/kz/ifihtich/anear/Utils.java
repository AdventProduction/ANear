package kz.ifihtich.anear;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#[a-fA-F0-9]{6}");

    public static String color(String message) {
        if (message == null) return "";

        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hexCode = matcher.group();
            String color = ChatColor.of(hexCode.substring(1)).toString();
            matcher.appendReplacement(buffer, color);
        }
        matcher.appendTail(buffer);

        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    public static String colorConsole(String message) {
        if (message == null) return "";

        Pattern pattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = pattern.matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hex = matcher.group(1);
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);

            String ansi = String.format("\u001B[38;2;%d;%d;%dm", r, g, b);
            matcher.appendReplacement(buffer, ansi);
        }
        matcher.appendTail(buffer);

        return buffer.toString() + "\u001B[0m";
    }

    public static void logo(){
        Bukkit.getLogger().info(Utils.colorConsole("    &#FF7200_        _                     _     &#FFFFFF  ____                   _    "));
        Bukkit.getLogger().info(Utils.colorConsole("   &#FF7200/ \\    __| |__   __ &#FFFFFF___  &#FF7200_ __  | |_  &#FFFFFF  |  _ \\  _ __  &#FF7200___    &#FFFFFF__| |   "));
        Bukkit.getLogger().info(Utils.colorConsole("  &#FF7200/ _ \\  / _` |\\ \\ / /&#FFFFFF/ _ \\&#FF7200| '_ \\ | __| &#FFFFFF  | |_) || '__|&#FF7200/ _ \\  &#FFFFFF/ _` |   "));
        Bukkit.getLogger().info(Utils.colorConsole(" &#FF7200/ ___ \\| (_| | \\ V /&#FFFFFF|  __/&#FF7200| | | || |_  &#FFFFFF  |  __/ | |  &#FF7200| (_) |&#FFFFFF| (_| | &#FFFFFF_ "));
        Bukkit.getLogger().info(Utils.colorConsole("&#FF7200/_/   \\_\\\\__,_|  \\_/  &#FFFFFF\\___|&#FF7200|_| |_| \\__| &#FFFFFF  |_|    |_|   &#FF7200\\___/  &#FFFFFF\\__,_|&#FFFFFF(_)"));
        Bukkit.getLogger().info(" ");
        Bukkit.getLogger().info(Utils.colorConsole("&#0088ccTelegram: &#FFFFFF@bagksp & @klikerstoris"));
        Bukkit.getLogger().info(" ");
    }
}
