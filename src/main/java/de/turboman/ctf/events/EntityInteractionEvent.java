package de.turboman.ctf.events;

import de.turboman.ctf.CaptureTheFlag;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.UUID;

public class EntityInteractionEvent implements Listener {

    @EventHandler
    public void onEvent(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked().getType() != EntityType.INTERACTION) return;

        for (var tag : e.getRightClicked().getScoreboardTags()) {
            if (!tag.startsWith("teamFlag_")) continue;

            var id = tag.replace("teamFlag_", "");
            var team = CaptureTheFlag.teamList.get(UUID.fromString(id));

            if (team.players().contains(e.getPlayer().getUniqueId())) continue;

            team.flagStolenBy(e.getPlayer().getUniqueId());

            break;
        }
    }
}
