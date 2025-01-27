package de.turboman.ctf.events;

import de.turboman.ctf.CaptureTheFlag;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class PlayerBreakBlockEvent implements Listener {

    @EventHandler
    public void onEvent(BlockBreakEvent e) {
        Player p = e.getPlayer();

        if (p.getGameMode() == GameMode.CREATIVE) return;
        if (!CaptureTheFlag.deadPlayers.contains(p.getUniqueId())) {
            e.setCancelled(true);
            return;
        }

        switch (CaptureTheFlag.GAME_STATE) {
            case PREP, FIGHT -> {
                return;
            }
        }

        e.setCancelled(true);
    }
}
