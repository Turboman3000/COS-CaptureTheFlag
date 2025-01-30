package de.turboman.ctf.events;

import de.turboman.ctf.CaptureTheFlag;
import de.turboman.ctf.FlagInteractionEntity;
import de.turboman.ctf.GameState;
import de.turboman.ctf.maps.MapCursorEntry;
import de.turboman.ctf.maps.MapManager;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.UUID;

public class PlayerPlaceBlockEvent implements Listener {
    private MiniMessage mm = MiniMessage.miniMessage();

    @EventHandler
    public void onEvent(BlockPlaceEvent e) {
        Player p = e.getPlayer();

        if (p.getGameMode() == GameMode.CREATIVE) return;

        switch (e.getBlockPlaced().getType()) {
            case Material.BLACK_WALL_BANNER, Material.BLUE_WALL_BANNER, Material.GREEN_WALL_BANNER,
                 Material.CYAN_WALL_BANNER, Material.PURPLE_WALL_BANNER, Material.LIGHT_GRAY_WALL_BANNER,
                 Material.GRAY_WALL_BANNER, Material.LIME_WALL_BANNER, Material.RED_WALL_BANNER,
                 Material.MAGENTA_WALL_BANNER, Material.YELLOW_WALL_BANNER, Material.WHITE_WALL_BANNER -> {
                e.setCancelled(true);
                return;
            }
        }

        if (e.getBlockAgainst().getType() == Material.BEDROCK) {
            p.sendMessage(mm.deserialize(CaptureTheFlag.prefix + "<red>You can't place blocks here!"));

            e.setCancelled(true);
            return;
        }

        if (CaptureTheFlag.deadPlayers.contains(p.getUniqueId())) {
            e.setCancelled(true);
            return;
        }

        switch (CaptureTheFlag.GAME_STATE) {
            case PREP, FIGHT -> {
                return;
            }
            case SET_FLAG -> {
                if (Tag.BANNERS.isTagged(e.getBlock().getType())) {
                    int teamSet = 0;

                    // Race Condition
                    for (var t : CaptureTheFlag.teamList.values()) {
                        if (t.flagLocation() != null) {
                            teamSet++;
                        }
                    }

                    for (var t : CaptureTheFlag.teamList.values()) {
                        if (t.leader() != p.getUniqueId()) continue;

                        var loc = e.getBlock().getLocation();

                        FlagInteractionEntity.getEntity(t.id(), loc.add(0.5d, 0, 0.5d));

                        teamSet++;
                        t.flagLocation(loc);

                        for (var x = -1; x <= 1; x++) {
                            for (var z = -1; z <= 1; z++) {
                                loc.clone().add(x, -1, z).getBlock().setType(Material.BEDROCK);
                            }
                        }

                        for (var pl : t.players()) {
                            var player = Bukkit.getPlayer(pl);

                            MapManager.playerMaps.get(pl).cursors().put(UUID.randomUUID() + "", new MapCursorEntry(loc.getBlockX(), loc.getBlockZ(), MapManager.getDecoColor(t), t.name()));

                            assert player != null;
                            player.playSound(Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.MASTER, 100, 2));
                            player.sendMessage(mm.deserialize(CaptureTheFlag.prefix + "<green>Flag placed at <gold>" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()));
                        }

                        t.bossBar().name(mm.deserialize("<yellow>Your Team Flag has been Placed!"));
                        t.bossBar().color(BossBar.Color.YELLOW);

                        if (teamSet == CaptureTheFlag.teamList.size()) {
                            CaptureTheFlag.GAME_STATE = GameState.PREP;
                            CaptureTheFlag.startTimer();

                            for (var player : Bukkit.getOnlinePlayers()) {
                                player.sendMessage(mm.deserialize(CaptureTheFlag.prefix + "<green>All flags are set! It's time to gather some Resources to protect your Team's Flag!"));
                                player.playSound(Sound.sound(Key.key("minecraft:entity.wither.spawn"), Sound.Source.MASTER, 0.15f, 2));
                            }

                            break;
                        }
                    }
                    return;
                }
            }
        }

        e.setCancelled(true);
    }
}
