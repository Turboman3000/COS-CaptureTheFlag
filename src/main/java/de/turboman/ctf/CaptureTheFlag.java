package de.turboman.ctf;

import de.turboman.ctf.commands.CTFCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class CaptureTheFlag extends JavaPlugin {

    @Override
    public void onEnable() {
        Objects.requireNonNull(getCommand("ctf")).setExecutor(new CTFCommand());

        Bukkit.getConsoleSender().sendMessage("[CTF] Plugin enabled");
    }

    @Override
    public void onDisable() {

    }
}
