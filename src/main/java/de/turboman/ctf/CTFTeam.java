package de.turboman.ctf;

import org.bukkit.entity.Player;

import java.util.List;

public record CTFTeam(String name, String color, List<Player> players) {
}
