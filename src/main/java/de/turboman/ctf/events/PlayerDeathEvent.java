package de.turboman.ctf.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;

public class PlayerDeathEvent implements Listener {

    @EventHandler
    public void onEvent(org.bukkit.event.entity.PlayerDeathEvent e) throws InvocationTargetException {
        e.setShouldDropExperience(false);
        e.setKeepInventory(true);
        e.setCancelled(true);
    }
}
