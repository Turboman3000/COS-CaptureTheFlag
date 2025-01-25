package de.turboman.ctf.events;

import de.turboman.ctf.CaptureTheFlag;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class PlayerBreakBlockEvent implements Listener {

    @EventHandler
    public void onEvent(BlockBreakEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        switch (CaptureTheFlag.GAME_STATE) {
            case PREP, FIGHT -> {
                return;
            }
        }

        e.setCancelled(true);
    }
}
