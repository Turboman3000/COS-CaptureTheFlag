package de.turboman.ctf.commands;

import de.turboman.ctf.CTFTeam;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
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
        if (!sender.isOp()) {
            sender.sendMessage(mm.deserialize("<red>You are not allowed to use this command!"));
            return false;
        }

        if (args.length >= 2 && args[0].equalsIgnoreCase("team")) {
            switch (args[1]) {
                case "create" -> {
                    if (args.length != 4) break;

                    teamList.add(new CTFTeam(args[2], args[3], new ArrayList<>()));
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

                    Player player = Bukkit.getPlayer(args[3]);

                    for (var t : teamList) {
                        if (t.players().contains(player.getUniqueId())) {
                            sender.sendMessage(mm.deserialize(prefix + "<red>" + player.getName() + " is already in a team!"));
                            break;
                        }
                    }

                    for (var t : teamList) {
                        if (t.name().equals(args[2])) {
                            t.players().add(player.getUniqueId());
                            sender.sendMessage(mm.deserialize(prefix + "<green>Added player <gold>" + player.getName() + "<green> to the Team: <gold>" + t.name()));
                            player.sendMessage(mm.deserialize(prefix + "<green>You are now in Team: <gold>" + t.name()));
                            break;
                        }
                    }

                    sender.sendMessage(mm.deserialize(prefix + "<red>This team doesn't exist!"));
                }
                case "remove" -> {

                }
                case "giveflag" -> {

                }
                case "leader" -> {

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
                && args[1].equalsIgnoreCase("add")) {
            for (var t : teamList) {
                output.add(t.name());
            }
        } else if (args.length == 4
                && args[0].equalsIgnoreCase("team")
                && args[1].equalsIgnoreCase("add")) {
            for (var p : Bukkit.getOnlinePlayers()) {
                output.add(p.getName());
            }
        }

        return output;
    }
}
