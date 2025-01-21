package de.turboman.ctf.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class PlayerSwitchOffhandEvent implements Listener {

    @EventHandler
    public void onEvent(PlayerSwapHandItemsEvent e) {
        e.setCancelled(true);
    }
}
