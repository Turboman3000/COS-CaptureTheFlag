package de.turboman.ctf.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CTFCommand implements CommandExecutor, TabCompleter {
    private MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.isOp()) {
            sender.sendMessage(mm.deserialize("<red>You are not allowed to use this command!"));
            return false;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("team")) {
            switch (args[1]) {
                case "create" -> {

                }
                case "add" -> {

                }
                case "remove" -> {

                }
                case "giveflag" -> {

                }
                case "list" -> {

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
        } else if (args.length == 2 && args[0].equalsIgnoreCase("team")) {
            output.add("create");
            output.add("add");
            output.add("remove");
            output.add("giveflag");
            output.add("list");
        }

        return output;
    }
}
