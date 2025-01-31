package de.turboman.ctf.events;

import de.turboman.ctf.CaptureTheFlag;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropEvent implements Listener {

    @EventHandler
    public void onEvent(PlayerDropItemEvent e) {
        Player p = e.getPlayer();

        if (Tag.BANNERS.isTagged(e.getItemDrop().getItemStack().getType())
                || CaptureTheFlag.deadPlayers.contains(p.getUniqueId())) {
            e.setCancelled(true);
        }
    }
}
