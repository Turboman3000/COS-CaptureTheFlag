package de.turboman.ctf;

import java.util.ArrayList;
import java.util.UUID;

public record CTFTeam(String name, String color, ArrayList<UUID> players, UUID leader) {
}
