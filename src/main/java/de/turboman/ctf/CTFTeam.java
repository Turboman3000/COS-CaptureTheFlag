package de.turboman.ctf;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;

public record CTFTeam(String name, String color, ArrayList<UUID> players, @Nullable UUID leader) {
}
