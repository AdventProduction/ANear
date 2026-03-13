package kz.ifihtich.anear;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class NearCommand implements TabExecutor {

    private final Map<UUID, Long> cooldowns = new HashMap<>();

    ConfigManager config = new ConfigManager();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (strings.length > 0 && strings[0].equalsIgnoreCase("reload")){
            if (!commandSender.hasPermission("anear.reload")){
                commandSender.sendMessage(config.getString("messages.noPerm"));
                return true;
            }
            ANear.getInstance().reloadConfig();
            commandSender.sendMessage(config.getString("messages.reload"));
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(config.getString("messages.noPlayer"));
            return true;
        }

        Player player = (Player) commandSender;

        long currentTime = System.currentTimeMillis();
        UUID uuid = player.getUniqueId();

        if (cooldowns.containsKey(uuid)){
            long lastUse = cooldowns.get(uuid);
            long timeLeft = (lastUse - currentTime) / 1000;
            if (timeLeft > 0){
                player.sendMessage(config.getString("messages.cooldown").replace("%time%", String.valueOf(timeLeft)));
                return true;
            }
        }

        if (!commandSender.hasPermission("anear.use")){
            commandSender.sendMessage(config.getString("messages.noPerm"));
            return true;
        }

        ConfigurationSection section = ANear.getInstance().getConfig().getConfigurationSection("nears");
        ConfigurationSection directions = ANear.getInstance().getConfig().getConfigurationSection("directions");
        assert section != null;

        List<String> orderedKeys = new ArrayList<>(section.getKeys(false));
        Collections.reverse(orderedKeys);
        for (String path : orderedKeys) {
            if (player.hasPermission("anear." + path)){
                ConfigurationSection near = section.getConfigurationSection(path);
                assert near != null;

                int radius = near.getInt("radius");
                int cooldown = near.getInt("cooldown", 10);
                List<String> messageLines = near.getStringList("message");
                List<String> nearbyPlayers = new ArrayList<>();

                for (Player target : Bukkit.getOnlinePlayers()){
                    if (target.equals(player)) continue;
                    if (!target.getWorld().equals(player.getWorld())) continue;
                    if (ANear.getInstance().getConfig().getBoolean("nears.hide-invisible") && target.hasPotionEffect(PotionEffectType.INVISIBILITY)) continue;

                    double distance = player.getLocation().distance(target.getLocation());
                    if (distance <= radius) {

                        String direction = getArrowDirection(player, target, directions);
                        String distanceTo = String.valueOf(Math.round(player.getLocation().distance(target.getLocation())));
                        nearbyPlayers.add(target.getName() + " " + direction + " " + distanceTo);
                    }
                }

                if (nearbyPlayers.isEmpty()){
                    if (!commandSender.hasPermission("anear.bypass")) {
                        cooldowns.put(uuid, currentTime + cooldown * 1000L);
                    }
                    player.sendMessage(config.getString("messages.empty"));
                    return true;
                }

                int playerIndex = 0;
                for (String messageLine : messageLines) {
                    String line = Utils.color(messageLine);

                    if (line.contains("%player%")) {
                        if (playerIndex < nearbyPlayers.size()) {

                            String[] parts = nearbyPlayers.get(playerIndex).split(" ", 3);
                            String name = parts[0];
                            String direction = parts.length > 2 ? parts[1] : "";
                            String distanceto = parts.length > 2 ? parts[2] : "";

                            player.sendMessage(line.replace("%player%", name).replace("%direction%", direction).replace("%distance%", distanceto));
                            playerIndex++;
                        }
                    } else {
                        player.sendMessage(line);
                    }
                }
                if (!commandSender.hasPermission("anear.bypass")) {
                    cooldowns.put(uuid, currentTime + cooldown * 1000L);
                }
                return true;
            }
        }

        player.sendMessage(config.getString("messages.noPerm"));
        return true;

    }

    private String getArrowDirection(Player from, Player to, ConfigurationSection directions) {
        double dx = to.getLocation().getX() - from.getLocation().getX();
        double dz = to.getLocation().getZ() - from.getLocation().getZ();
        double targetAngle = Math.toDegrees(Math.atan2(-dx, dz));
        if (targetAngle < 0) targetAngle += 360;

        double playerYaw = (from.getLocation().getYaw() + 360) % 360;

        double relativeAngle = (targetAngle - playerYaw + 360) % 360;

        if (relativeAngle >= 337.5 || relativeAngle < 22.5)
            return directions.getString("up", "↑");
        if (relativeAngle >= 22.5 && relativeAngle < 67.5)
            return directions.getString("right-up", "↗");
        if (relativeAngle >= 67.5 && relativeAngle < 112.5)
            return directions.getString("right", "→");
        if (relativeAngle >= 112.5 && relativeAngle < 157.5)
            return directions.getString("right-down", "↘");
        if (relativeAngle >= 157.5 && relativeAngle < 202.5)
            return directions.getString("down", "↓");
        if (relativeAngle >= 202.5 && relativeAngle < 247.5)
            return directions.getString("left-down", "↙");
        if (relativeAngle >= 247.5 && relativeAngle < 292.5)
            return directions.getString("left", "←");
        if (relativeAngle >= 292.5 && relativeAngle < 337.5)
            return directions.getString("left-up", "↖");

        return directions.getString("up", "↑");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1 && commandSender.hasPermission("anear.reload")){
            return Collections.singletonList("reload");
        }
        return Collections.emptyList();
    }
}
