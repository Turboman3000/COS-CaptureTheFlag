package de.turboman.ctf.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerDeathEvent implements Listener {

    @EventHandler
    public void onEvent(org.bukkit.event.entity.PlayerDeathEvent e) {
        e.setShouldDropExperience(false);
        e.setKeepInventory(true);
        e.setCancelled(true);
    }
}
