package de.turboman.ctf.maps;

import org.bukkit.entity.Player;
import org.bukkit.map.*;
import org.jetbrains.annotations.NotNull;

public class CursorRenderer extends MapRenderer {

    @Override
    public void render(@NotNull MapView view, @NotNull MapCanvas canvas, @NotNull Player player) {
        MapCursorCollection cursors = new MapCursorCollection();

        for (var c : MapManager.playerMaps.get(player.getUniqueId()).cursors().values()) {
            cursors.addCursor(new MapCursor((byte) c.x(), (byte) c.y(), (byte) 0, c.type(), true, c.caption()));
        }

        canvas.setCursors(cursors);
    }
}
