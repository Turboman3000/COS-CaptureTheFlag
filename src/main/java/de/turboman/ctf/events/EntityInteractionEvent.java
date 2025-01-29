package de.turboman.ctf.events;

import de.turboman.ctf.CaptureTheFlag;
import de.turboman.ctf.GameState;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.UUID;

public class EntityInteractionEvent implements Listener {
    private MiniMessage mm = MiniMessage.miniMessage();

    @EventHandler
    public void onEvent(PlayerInteractAtEntityEvent e) {
        if (CaptureTheFlag.GAME_STATE != GameState.FIGHT) {
            e.getPlayer().sendMessage(mm.deserialize(CaptureTheFlag.prefix + "<red>You can steel flags only in the Battle Phase!"));
            return;
        }

        if (e.getRightClicked().getType() != EntityType.INTERACTION) return;

        for (var tag : e.getRightClicked().getScoreboardTags()) {
            if (!tag.startsWith("teamFlag_")) continue;

            var id = tag.replace("teamFlag_", "");
            var team = CaptureTheFlag.teamList.get(UUID.fromString(id));

            if (team.players().contains(e.getPlayer().getUniqueId())) {
                e.getPlayer().sendMessage(mm.deserialize(CaptureTheFlag.prefix + "<red>You can't steel your own Team's flag"));
                continue;
            }

            for (var pp : team.players()) {
                Bukkit.getPlayer(pp).sendMessage(mm.deserialize(CaptureTheFlag.prefix + "<red>" + e.getPlayer().getName() + " stole your Team's flag!"));
            }

            CaptureTheFlag.scoreObjec.getScore("t1_" + team.id()).customName(mm.deserialize("<" + team.color() + ">" + team.name() + " <dark_gray>-<red><b> x"));

            e.getRightClicked().getLocation().getBlock().setType(Material.AIR);
            e.getRightClicked().remove();

            team.flagStolenBy(e.getPlayer().getUniqueId());
            e.getPlayer().sendMessage(mm.deserialize(CaptureTheFlag.prefix + "<green>You stole the flag from <" + team.color() + ">" + team.name()));

            break;
        }
    }
}
