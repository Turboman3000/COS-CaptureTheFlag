package de.turboman.ctf.commands;

import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.turboman.ctf.CTFTeam;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static de.turboman.ctf.CaptureTheFlag.*;

public class CTFCommand implements CommandExecutor, TabCompleter {
    private MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender.isOp() || sender.hasPermission("ctf.command"))) {
            sender.sendMessage(mm.deserialize(prefix + "<red>You are not allowed to use this command!"));
            return false;
        }

        if (args.length >= 2 && args[0].equalsIgnoreCase("team")) {
            switch (args[1]) {
                case "create" -> {
                    if (args.length != 4) break;

                    var group = voicechatAPI.groupBuilder()
                            .setHidden(true)
                            .setId(UUID.randomUUID())
                            .setName(args[2])
                            .setType(Group.Type.OPEN)
                            .setPersistent(true)
                            .setPassword(UUID.randomUUID().toString().replace("-", ""))
                            .build();

                    teamList.add(new CTFTeam(args[2], args[3], new ArrayList<>(), null, group));
                    sender.sendMessage(mm.deserialize(prefix + "<green>Team <gold>" + args[2] + "<green> created!"));
                }
                case "delete" -> {
                    if (args.length != 3) break;

                    for (var t : teamList) {
                        if (t.name().equals(args[2])) {
                            teamList.remove(t);
                            break;
                        }
                    }
                }
                case "add" -> {
                    if (args.length != 4) break;

                    var player = Bukkit.getPlayer(args[3]);

                    for (var t : teamList) {
                        assert player != null;
                        if (t.players().contains(player.getUniqueId())) {
                            sender.sendMessage(mm.deserialize(prefix + "<dark_red>" + player.getName() + "<red> is already in a team!"));
                            break;
                        }
                    }

                    boolean noTeam = true;

                    for (var t : teamList) {
                        if (t.name().equals(args[2])) {
                            noTeam = false;
                            t.players().add(player.getUniqueId());

                            var playerName = mm.deserialize("<" + t.color() + ">" + player.getName());

                            player.customName(playerName);
                            player.playerListName(playerName);
                            player.displayName(playerName);

                            VoicechatConnection connection = voicechatAPI.getConnectionOf(player.getUniqueId());

                            assert connection != null;
                            connection.setGroup(t.voiceGroup());

                            sender.sendMessage(mm.deserialize(prefix + "<green>Added player <gold>" + player.getName() + "<green> to the Team: <gold>" + t.name()));
                            player.sendMessage(mm.deserialize(prefix + "<green>You are now in Team: <gold>" + t.name()));
                            break;
                        }
                    }

                    if (noTeam) {
                        sender.sendMessage(mm.deserialize(prefix + "<red>This team doesn't exist!"));
                    }
                }
                case "remove" -> {
                    if (args.length != 4) break;

                    var player = Bukkit.getPlayer(args[3]);
                    boolean noPlayerInTeam = true;

                    for (var t : teamList) {
                        assert player != null;
                        if (t.players().contains(player.getUniqueId())) {
                            noPlayerInTeam = false;
                            break;
                        }
                    }

                    if (noPlayerInTeam) {
                        sender.sendMessage(mm.deserialize(prefix + "<dark_red>" + player.getName() + "<red> is not in a team!"));
                    }

                    boolean noTeam = true;

                    for (var t : teamList) {
                        if (t.name().equals(args[2])) {
                            noTeam = false;
                            t.players().remove(player.getUniqueId());

                            var playerName = mm.deserialize(player.getName());

                            player.customName(playerName);
                            player.playerListName(playerName);
                            player.displayName(playerName);

                            sender.sendMessage(mm.deserialize(prefix + "<green>Removed player <gold>" + player.getName() + "<green> from the Team: <gold>" + t.name()));
                            player.sendMessage(mm.deserialize(prefix + "<green>You are removed from Team: <gold>" + t.name()));
                            break;
                        }
                    }

                    if (noTeam) {
                        sender.sendMessage(mm.deserialize(prefix + "<red>This team doesn't exist!"));
                    }
                }
                case "giveflag" -> {
                    if (args.length != 3) break;

                    ItemStack flagItem;

                    boolean noTeam = true;

                    for (var t : teamList) {
                        if (t.name().equals(args[2])) {
                            noTeam = false;

                            flagItem = switch (t.color()) {
                                case "black" -> new ItemStack(Material.BLACK_BANNER);
                                case "dark_blue" -> new ItemStack(Material.BLUE_BANNER);
                                case "dark_green" -> new ItemStack(Material.GREEN_BANNER);
                                case "dark_aqua" -> new ItemStack(Material.CYAN_BANNER);
                                case "dark_purple" -> new ItemStack(Material.PURPLE_BANNER);
                                case "gray" -> new ItemStack(Material.LIGHT_GRAY_BANNER);
                                case "dark_gray" -> new ItemStack(Material.GRAY_BANNER);
                                case "green" -> new ItemStack(Material.LIME_BANNER);
                                case "red" -> new ItemStack(Material.RED_BANNER);
                                case "light_purple" -> new ItemStack(Material.MAGENTA_BANNER);
                                case "yellow" -> new ItemStack(Material.YELLOW_BANNER);
                                case "white" -> new ItemStack(Material.WHITE_BANNER);
                                default -> null;
                            };

                            assert flagItem != null;
                            var meta = flagItem.getItemMeta();

                            meta.displayName(mm.deserialize("<!i><" + t.color() + "> " + t.name() + "'s Flag"));
                            flagItem.setItemMeta(meta);

                            /*
                            var str = Objects.requireNonNull(meta.displayName()).toString()
                                    .replace("TextComponentImpl{content=\" ", "");

                            str = str.split(Pattern.quote("\", style=StyleImpl{"))[0].replace("'s Flag", "");
                             */

                            var pl = t.leader();

                            if (pl == null) {
                                pl = t.players().getFirst();
                            }

                            var player = Bukkit.getPlayer(pl);

                            assert player != null;
                            player.getInventory().addItem(flagItem);

                            sender.sendMessage(mm.deserialize(prefix + "<green>Gave player <gold>" + player.getName() + "<green> flag for Team <gold>" + t.name()));
                            break;
                        }
                    }

                    if (noTeam) {
                        sender.sendMessage(mm.deserialize(prefix + "<red>This team doesn't exist!"));
                    }
                }
                case "leader" -> {
                    if (args.length != 3) break;

                    for (var t : teamList) {
                        if (t.name().equals(args[2])) {
                            if (t.leader() == null) {
                                sender.sendMessage(mm.deserialize(prefix + "<red>Team <dark_red>" + t.name() + "<red> doesn't have a leader!"));
                                break;
                            }

                            var player = Bukkit.getOfflinePlayer(t.leader());

                            sender.sendMessage(mm.deserialize(prefix + "<green>Leader of Team <gold>" + t.name() + "<green> is <gold>" + player.getName()));
                            break;
                        }
                    }
                }
                case "list" -> {
                    sender.sendMessage(mm.deserialize(prefix + "<green>List of Teams:"));

                    for (var t : teamList) {
                        sender.sendMessage(mm.deserialize(prefix + "<" + t.color() + ">" + t.name() + "<gray> (" + t.players().size() + ")"));
                    }
                }
            }
        } else {
            switch (args[0]) {
                case "start" -> {
                    AtomicInteger sec = new AtomicInteger(4);

                    Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                        sec.getAndDecrement();

                        for (var pl : Bukkit.getOnlinePlayers()) {
                            if (sec.get() >= 1) {
                                pl.playSound(Sound.sound(Key.key("minecraft:ui.button.click"), Sound.Source.MASTER, 100, sec.get() - 1));
                            } else {
                                pl.playSound(Sound.sound(Key.key("minecraft:entity.player.levelup"), Sound.Source.MASTER, 100, 1));
                            }

                            pl.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ZERO, Duration.ofMillis(1500), Duration.ofMillis(2500)));
                            pl.sendTitlePart(TitlePart.TITLE, mm.deserialize("<green>" + sec));
                            pl.sendTitlePart(TitlePart.SUBTITLE, mm.deserialize("<gold>Starting in"));
                        }

                        if (sec.get() != 0) return;

                        Bukkit.getScheduler().cancelTasks(plugin);

                        var world = Objects.requireNonNull(Bukkit.getWorld("world"));
                        var random = new Random();
                        var border = world.getWorldBorder();

                        int x = random.nextInt(((int) (border.getSize() / 2)));
                        int z = random.nextInt(((int) (border.getSize() / 2)));

                        int cX = border.getCenter().getBlockX();
                        int cZ = border.getCenter().getBlockZ();

                        if (teamList.size() >= 2) {
                            var loc1 = new Location(world, cX + x + 50, world.getHighestBlockYAt(cX + x + 50, cZ + z + 50), cZ + z + 50);
                            var loc2 = new Location(world, cX + -x - 50, world.getHighestBlockYAt(cX + -x - 50, cZ + -z - 50), cZ + -z - 50);

                            for (var player : teamList.getFirst().players()) {
                                var p = Bukkit.getPlayer(player);

                                assert p != null;
                                p.teleport(loc1);
                            }

                            for (var player : teamList.get(1).players()) {
                                var p = Bukkit.getPlayer(player);

                                assert p != null;
                                p.teleport(loc2);
                            }
                        }

                        if (teamList.size() >= 3) {
                            var loc = new Location(world, cX + -x - 50, world.getHighestBlockYAt(cX + -x - 50, z + 50), cZ + z + 50);

                            for (var player : teamList.get(2).players()) {
                                var p = Bukkit.getPlayer(player);

                                assert p != null;
                                p.teleport(loc);
                            }
                        }

                        if (teamList.size() >= 4) {
                            var loc = new Location(world, cX + x + 50, world.getHighestBlockYAt(cX + x + 50, -z - 50), cZ + -z - 50);

                            for (var player : teamList.get(3).players()) {
                                var p = Bukkit.getPlayer(player);

                                assert p != null;
                                p.teleport(loc);
                            }
                        }

                    }, 0, 20);
                }
                case "stop" -> {

                }
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {
        var output = new ArrayList<String>();

        if (args.length == 1) {
            output.add("team");
            output.add("start");
            output.add("stop");
        } else if (args.length == 2
                && args[0].equalsIgnoreCase("team")) {
            output.add("create");
            output.add("delete");
            output.add("add");
            output.add("remove");
            output.add("giveflag");
            output.add("leader");
            output.add("list");
        } else if (args.length == 4
                && args[0].equalsIgnoreCase("team")
                && args[1].equalsIgnoreCase("create")) {
            output.add("black");
            output.add("dark_blue");
            output.add("dark_green");
            output.add("dark_aqua");
            output.add("dark_purple");
            output.add("gray");
            output.add("dark_gray");
            output.add("green");
            output.add("red");
            output.add("light_purple");
            output.add("yellow");
            output.add("white");
        } else if (args.length == 3
                && args[0].equalsIgnoreCase("team")
                && (args[1].equalsIgnoreCase("add")
                || args[1].equalsIgnoreCase("remove")
                || args[1].equalsIgnoreCase("giveflag"))) {
            for (var t : teamList) {
                output.add(t.name());
            }
        } else if (args.length == 4
                && args[0].equalsIgnoreCase("team")
                && (args[1].equalsIgnoreCase("add")
                || args[1].equalsIgnoreCase("remove"))) {
            for (var p : Bukkit.getOnlinePlayers()) {
                output.add(p.getName());
            }
        } else if (args.length == 3
                && args[0].equalsIgnoreCase("team")
                && args[1].equalsIgnoreCase("leader")) {
            for (var t : teamList) {
                output.add(t.name());
            }
        }

        return output;
    }
}
