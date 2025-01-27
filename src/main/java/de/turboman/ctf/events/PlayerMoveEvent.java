package de.turboman.ctf.events;

import de.turboman.ctf.CaptureTheFlag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerMoveEvent implements Listener {

    @EventHandler
    public void onEvent(org.bukkit.event.player.PlayerMoveEvent e) {
        Player p = e.getPlayer();

        if (!CaptureTheFlag.deadPlayers.contains(p.getUniqueId())) return;

        if (e.hasChangedPosition()) {
            e.setCancelled(true);
        }
    }
}
