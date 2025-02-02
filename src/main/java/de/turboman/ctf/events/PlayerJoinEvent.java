package de.turboman.ctf.events;

import de.turboman.ctf.CaptureTheFlag;
import de.turboman.ctf.GameState;
import de.turboman.ctf.maps.MapManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerJoinEvent implements Listener {

    @EventHandler
    public void onEvent(org.bukkit.event.player.PlayerJoinEvent e) {
        Player p = e.getPlayer();

        for (var t : CaptureTheFlag.teamList.values()) {
            if (!t.players().contains(p.getUniqueId())) continue;

            if (MapManager.playerMaps.get(p.getUniqueId()) != null) {
                p.getInventory().setItemInOffHand(MapManager.getMapItem(p.getUniqueId(), true));
            }

            if (CaptureTheFlag.GAME_STATE != GameState.NO_GAME) {
                p.setScoreboard(CaptureTheFlag.scoreboard);
            }

            p.showBossBar(t.bossBar());
        }
    }
}
