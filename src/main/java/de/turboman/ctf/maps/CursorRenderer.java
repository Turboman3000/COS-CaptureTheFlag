package de.turboman.ctf.maps;

import org.bukkit.entity.Player;
import org.bukkit.map.*;
import org.jetbrains.annotations.NotNull;

public class CursorRenderer extends MapRenderer {

    @Override
    public void render(@NotNull MapView view, @NotNull MapCanvas canvas, @NotNull Player player) {
        MapCursorCollection cursors = new MapCursorCollection();

        for (var c : MapManager.playerMaps.get(player.getUniqueId()).cursors().values()) {
            var cursorX = (c.x() - view.getCenterX()) / view.getScale().getValue();
            var cursorY = (c.y() - view.getCenterZ()) / view.getScale().getValue();

            if (c.caption().isEmpty() || c.caption().isBlank()) {
                cursors.addCursor(new MapCursor((byte) cursorX, (byte) cursorY, (byte) 0, c.type(), true));
            } else {
                cursors.addCursor(new MapCursor((byte) cursorX, (byte) cursorY, (byte) 0, c.type(), true, c.caption()));
            }
        }

        canvas.setCursors(cursors);
    }
}
