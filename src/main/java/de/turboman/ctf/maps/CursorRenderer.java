package de.turboman.ctf.maps;

import de.turboman.ctf.CaptureTheFlag;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.*;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class CursorRenderer extends MapRenderer {
    private MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public void render(@NotNull MapView view, @NotNull MapCanvas canvas, @NotNull Player player) {
        MapCursorCollection cursors = new MapCursorCollection();

        for (var t : CaptureTheFlag.teamList.values()) {
            if (!t.players().contains(player.getUniqueId())) continue;

            if (t.flagStolenBy() != null) {
                player.sendActionBar(mm.deserialize("<red>Flag stolen by <dark_red><b>" + Bukkit.getPlayer(t.flagStolenBy()).getName() + "</b><red>!"));

                for (int x = 0; x <= 127; x++) {
                    for (int y = 0; y <= 127; y++) {
                        if (y >= 3 && y <= 124 && x >= 3 && x <= 124) continue;

                        canvas.setPixelColor(x, y, Color.RED);
                    }
                }

                break;
            }
        }

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
