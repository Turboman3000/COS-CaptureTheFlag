package de.turboman.ctf.events;

import de.turboman.ctf.CaptureTheFlag;
import org.bukkit.GameMode;
import org.bukkit.Tag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerPlaceBlockEvent implements Listener {

    @EventHandler
    public void onEvent(BlockPlaceEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        switch (CaptureTheFlag.GAME_STATE) {
            case PREP, FIGHT -> {
                return;
            }
            case SET_FLAG -> {
                if (Tag.BANNERS.isTagged(e.getBlock().getType())) {

                    return;
                }
            }
        }

        e.setCancelled(true);
    }
}
