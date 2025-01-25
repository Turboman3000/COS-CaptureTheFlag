package de.turboman.ctf.events;

import de.turboman.ctf.CaptureTheFlag;
import de.turboman.ctf.GameState;
import de.turboman.ctf.maps.MapManager;
import io.papermc.paper.datacomponent.item.MapDecorations;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
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
                    int teamSet = 0;

                    for (var t : CaptureTheFlag.teamList.values()) {
                        if (t.flagLocation() != null) {
                            teamSet++;
                        }

                        if (t.leader() != e.getPlayer().getUniqueId()) continue;

                        var loc = e.getBlock().getLocation();

                        teamSet++;
                        t.flagLocation(loc);

                        for (var pl : t.players()) {
                            var player = Bukkit.getPlayer(pl);

                            MapManager.playerMaps.get(pl).decorations().put("own_flag", MapDecorations.decorationEntry(MapManager.getDecoColor(t), loc.getBlockX(), loc.getBlockZ(), 0));

                            assert player != null;
                            player.getInventory().setItemInOffHand(MapManager.getMapItem(pl));
                            player.playSound(Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.MASTER, 100, 2));
                            player.sendMessage(mm.deserialize(CaptureTheFlag.prefix + "<green>Flag placed at <gold>" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()));
                        }

                        t.bossBar().name(mm.deserialize("<yellow>Your Team Flag has been Placed!"));
                        t.bossBar().color(BossBar.Color.YELLOW);

                        if (teamSet == CaptureTheFlag.teamList.size()) {
                            CaptureTheFlag.GAME_STATE = GameState.PREP;

                            for (var t2 : CaptureTheFlag.teamList.values()) {
                                t2.bossBar().name(mm.deserialize("<green>Preparation Time<gold> " + CaptureTheFlag.PREP_TIME + ":00"));
                                t2.bossBar().color(BossBar.Color.GREEN);
                                t2.bossBar().progress(1);
                            }

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
