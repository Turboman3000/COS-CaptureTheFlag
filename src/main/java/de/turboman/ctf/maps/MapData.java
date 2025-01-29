package de.turboman.ctf.maps;

import org.bukkit.map.MapView;

import java.util.HashMap;

public record MapData(MapView view, HashMap<String, MapCursorEntry> cursors) {
}
