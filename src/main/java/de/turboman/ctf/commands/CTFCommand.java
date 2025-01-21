package de.turboman.ctf.commands;

import de.turboman.ctf.CTFTeam;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static de.turboman.ctf.CaptureTheFlag.prefix;
import static de.turboman.ctf.CaptureTheFlag.teamList;

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

                    teamList.add(new CTFTeam(args[2], args[3], new ArrayList<>(), null));
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
            output.add("dark_red");
            output.add("dark_purple");
            output.add("gold");
            output.add("gray");
            output.add("dark_gray");
            output.add("green");
            output.add("aqua");
            output.add("red");
            output.add("light_purple");
            output.add("yellow");
            output.add("white");
        } else if (args.length == 3
                && args[0].equalsIgnoreCase("team")
                && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
            for (var t : teamList) {
                output.add(t.name());
            }
        } else if (args.length == 4
                && args[0].equalsIgnoreCase("team")
                && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
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
