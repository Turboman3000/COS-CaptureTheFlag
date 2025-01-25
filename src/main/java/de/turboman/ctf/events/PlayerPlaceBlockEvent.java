package de.turboman.ctf.events;

import de.turboman.ctf.CaptureTheFlag;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Tag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerPlaceBlockEvent implements Listener {
    private MiniMessage mm = MiniMessage.miniMessage();

    @EventHandler
    public void onEvent(BlockPlaceEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        switch (CaptureTheFlag.GAME_STATE) {
            case PREP, FIGHT -> {
                return;
            }
            case SET_FLAG -> {
                if (Tag.BANNERS.isTagged(e.getBlock().getType())) {
                    for (var t : CaptureTheFlag.teamList.values()) {
                        if (t.leader() != e.getPlayer().getUniqueId()) continue;

                        var loc = e.getBlock().getLocation();

                        t.flagLocation(loc);

                        for (var pl : t.players()) {
                            var player = Bukkit.getPlayer(pl);

                            assert player != null;
                            player.sendMessage(mm.deserialize(CaptureTheFlag.prefix + "<green>Flag placed at <gold>" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()));
                        }
                    }
                    return;
                }
            }
        }

        e.setCancelled(true);
    }
}
