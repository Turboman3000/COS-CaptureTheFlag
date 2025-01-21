package de.turboman.ctf.maps;

import org.bukkit.map.MapView;

import java.util.ArrayList;
import java.util.UUID;

public record MapData(MapView view, ArrayList<UUID> unlockedFlags) {
}
