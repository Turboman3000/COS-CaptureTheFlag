package de.turboman.ctf.maps;

import org.bukkit.map.MapCursor;

public record MapCursorEntry(long x, long y, MapCursor.Type type, String caption) {
}
