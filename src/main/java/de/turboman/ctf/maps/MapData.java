package de.turboman.ctf.maps;

import io.papermc.paper.datacomponent.item.MapDecorations;
import org.bukkit.map.MapView;

import java.util.HashMap;

public record MapData(MapView view, HashMap<String, MapDecorations.DecorationEntry> decorations) {
}
