package de.turboman.ctf.events;

import de.turboman.ctf.CaptureState;
import de.turboman.ctf.CaptureTheFlag;
import de.turboman.ctf.FlagInteractionEntity;
import de.turboman.ctf.commands.CTFCommand;
import de.turboman.ctf.maps.MapCursorEntry;
import de.turboman.ctf.maps.MapManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static de.turboman.ctf.CaptureTheFlag.deadPlayers;
import static de.turboman.ctf.CaptureTheFlag.prefix;

public class PlayerDeathEvent implements Listener {
    private final MiniMessage mm = MiniMessage.miniMessage();

    @EventHandler
    public void onEvent(org.bukkit.event.entity.PlayerDeathEvent e) {
        Player p = e.getPlayer();

        e.setShouldDropExperience(false);
        e.setKeepInventory(true);
        e.setCancelled(true);

        if (deadPlayers.contains(p.getUniqueId())) return;

        int teamSize = 0;
        int deadSize = 0;

        for (var t : CaptureTheFlag.teamList.values()) {
            if (!t.players().contains(p.getUniqueId())) continue;

            t.score(t.score() - 15);

            for (var tt : CaptureTheFlag.teamList.values()) {
                if (tt.flagStolenBy() != p.getUniqueId()) continue;

                tt.state(CaptureState.WILD);
                p.getLocation().getBlock().setType(CTFCommand.getFlagItem(tt).getType());
                FlagInteractionEntity.spawnEntity(tt.id(), p.getLocation());

                tt.flagStolenBy(null);

                for (var ent : e.getPlayer().getWorld().getEntities()) {
                    if (ent.getType() != EntityType.ITEM_DISPLAY) continue;
                    if (!ent.getScoreboardTags().contains("stolenFlag_" + tt.id())) continue;

                    ent.remove();

                    break;
                }

                break;
            }

            teamSize = t.players().size();

            for (var pp : t.players()) {
                if (deadPlayers.contains(pp)) {
                    deadSize++;
                }

                MapManager.playerMaps.get(pp).cursors().put("death_" + p.getUniqueId(), new MapCursorEntry(p.getLocation().getBlockX(), p.getLocation().getBlockZ(), MapCursor.Type.TARGET_X, p.getName()));
            }
        }

        deadSize++;
        deadPlayers.add(p.getUniqueId());
        CaptureTheFlag.voicechatAPI.getConnectionOf(p.getUniqueId()).setGroup(null);

        if (deadSize == teamSize) {
            for (var t : CaptureTheFlag.teamList.values()) {
                if (!t.players().contains(p.getUniqueId())) continue;

                for (var pp : t.players()) {
                    deadPlayers.remove(pp);

                    for (var ppp : t.players()) {
                        MapManager.playerMaps.get(ppp).cursors().remove("death_" + pp);
                    }

                    CaptureTheFlag.voicechatAPI.getConnectionOf(pp).setGroup(t.voiceGroup());

                    var ppp = Bukkit.getPlayer(pp);

                    ppp.setGlowing(false);
                    ppp.removePotionEffect(PotionEffectType.REGENERATION);
                    ppp.removePotionEffect(PotionEffectType.RESISTANCE);
                    ppp.removePotionEffect(PotionEffectType.DARKNESS);

                    ppp.teleport(t.flagLocation());
                    ppp.teleportAsync(t.flagLocation());

                    p.sendMessage(mm.deserialize(prefix + "<red>Your entire Team is dead, you're getting Respawned at the Team's flag"));
                }
            }

            return;
        }

        p.sendMessage(mm.deserialize(prefix + "<red><b>You're dead!<!b><green> Wait for a Teammate to revive you!"));
        p.setGlowing(true);
        p.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, Integer.MAX_VALUE, 255, false, false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 255, false, false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 255, false, false));
    }
}
