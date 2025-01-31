package de.turboman.ctf.events;

import de.turboman.ctf.CaptureTheFlag;
import de.turboman.ctf.maps.MapManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

import static de.turboman.ctf.CaptureTheFlag.deadPlayers;

public class PlayerSneakEvent implements Listener {

    @EventHandler
    public void onEvent(PlayerToggleSneakEvent e) {
        if (!e.isSneaking()) return;

        Player p = e.getPlayer();

        if (CaptureTheFlag.deadPlayers.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            return;
        }

        var nearby = (ArrayList<Player>) p.getWorld().getNearbyEntitiesByType(Player.class, p.getLocation(), 2);

        for (var t : CaptureTheFlag.teamList.values()) {
            if (!t.players().contains(p.getUniqueId())) continue;

            for (var near : nearby) {
                if (near.getUniqueId().equals(p.getUniqueId())) continue;
                if (!deadPlayers.contains(near.getUniqueId())) continue;

                if (t.players().contains(near.getUniqueId())) {
                    deadPlayers.remove(near.getUniqueId());

                    for (var pp : t.players()) {
                        MapManager.playerMaps.get(pp).cursors().remove("death_" + near.getUniqueId());
                    }

                    CaptureTheFlag.voicechatAPI.getConnectionOf(near.getUniqueId()).setGroup(t.voiceGroup());

                    near.setGlowing(false);
                    near.removePotionEffect(PotionEffectType.REGENERATION);
                    near.removePotionEffect(PotionEffectType.RESISTANCE);
                    near.removePotionEffect(PotionEffectType.DARKNESS);
                    near.removePotionEffect(PotionEffectType.WEAKNESS);
                }
            }
        }
    }
}
