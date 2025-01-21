package de.turboman.ctf;

import java.util.List;
import java.util.UUID;

public record CTFTeam(String name, String color, List<UUID> players) {
}
