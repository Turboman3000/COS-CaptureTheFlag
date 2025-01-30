package de.turboman.ctf;

import de.turboman.ctf.maps.MapCursorEntry;
import de.turboman.ctf.maps.MapManager;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.function.Consumer;

import static de.turboman.ctf.CaptureTheFlag.*;

public class GameLoop implements Consumer<ScheduledTask> {
    private static MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public void accept(ScheduledTask task) {
        if (TIMER_SECONDS <= 5
                && TIMER_HOURS == 0
                && TIMER_MINUTES == 0
                && GAME_STATE == GameState.PREP) {
            for (var p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(mm.deserialize(prefix + "<green>Battle Time begins in <gold>" + TIMER_SECONDS));
                p.playSound(Sound.sound(Key.key("minecraft:block.note_block.pling"), Sound.Source.MASTER, 1, 2));
            }
        }

        if (TIMER_SECONDS == 0
                && TIMER_HOURS == 0
                && TIMER_MINUTES == 0
                && GAME_STATE == GameState.PREP) {
            TIMER_HOURS = FIGHT_TIME / 60;
            TIMER_MINUTES = FIGHT_TIME - (TIMER_HOURS * 60);
            TIMER_TOTAL_SECONDS = getTimerSecs();

            GAME_STATE = GameState.FIGHT;

            for (var t : teamList.values()) {
                for (var pp : t.players()) {
                    var p = Bukkit.getPlayer(pp);

                    assert p != null;
                    p.teleportAsync(t.flagLocation());
                }
            }

            for (var p : Bukkit.getOnlinePlayers()) {
                p.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ZERO, Duration.ofMillis(2500), Duration.ofMillis(2500)));
                p.sendTitlePart(TitlePart.TITLE, mm.deserialize(""));
                p.sendTitlePart(TitlePart.SUBTITLE, mm.deserialize("<gold><b>It's Battle Time!"));

                Bukkit.getScheduler().runTask(plugin, () -> {
                    p.removePotionEffect(PotionEffectType.REGENERATION);
                    p.removePotionEffect(PotionEffectType.RESISTANCE);
                });

                p.sendMessage(mm.deserialize(prefix + "<green>It's Time to Fight! Use the resources that you've collected to fight for your Team and to protect your Team's flag!"));
                p.playSound(Sound.sound(Key.key("minecraft:entity.ender_dragon.growl"), Sound.Source.MASTER, 0.7f, 1));
            }
        }

        if (TIMER_SEARCH <= 5 && TIMER_SEARCH != 0) {
            for (var p : Bukkit.getOnlinePlayers()) {
                p.playSound(Sound.sound(Key.key("minecraft:block.note_block.pling"), Sound.Source.MASTER, 1, 2));
                p.sendActionBar(mm.deserialize("<green>Positions of all Players will be shown on the Map in <gold>" + TIMER_SEARCH + "<green> seconds!"));
            }
        }

        if (TIMER_SEARCH == 0) {
            TIMER_SEARCH = SEARCH_TIME * 60;

            for (var p : Bukkit.getOnlinePlayers()) {
                for (var pp : Bukkit.getOnlinePlayers()) {
                    if (pp.getUniqueId() == p.getUniqueId()) continue;

                    MapManager.playerMaps.get(p.getUniqueId()).cursors().remove("player_" + pp.getUniqueId());

                    boolean isTeam = false;

                    for (var t : teamList.values()) {
                        if (!t.players().contains(p.getUniqueId())) continue;
                        if (t.players().contains(pp.getUniqueId())) {
                            isTeam = true;
                            break;
                        }
                    }

                    MapManager.playerMaps.get(p.getUniqueId()).cursors()
                            .put("player_" + pp.getUniqueId(),
                                    new MapCursorEntry(pp.getLocation().getBlockX(),
                                            pp.getLocation().getBlockZ(),
                                            isTeam ? MapCursor.Type.BLUE_MARKER : MapCursor.Type.RED_MARKER,
                                            ""));
                }

                p.playSound(Sound.sound(Key.key("minecraft:entity.player.levelup"), Sound.Source.MASTER, 1, 2));
                p.sendActionBar(mm.deserialize("<green>Positions of all Players are shown on the Map!"));
            }
        }

        if (GAME_STATE == GameState.FIGHT) {
            TIMER_SEARCH--;
        }

        TIMER_SECONDS--;

        if (TIMER_SECONDS == -1) {
            TIMER_SECONDS = 59;
            TIMER_MINUTES--;
        }
        if (TIMER_MINUTES == -1) {
            TIMER_SECONDS = 59;
            TIMER_MINUTES = 59;
            TIMER_HOURS--;
        }

        var progress = (float) getTimerSecs() / TIMER_TOTAL_SECONDS;
        var timeText = String.format("%02d", TIMER_HOURS) + ":" + String.format("%02d", TIMER_MINUTES) + ":" + String.format("%02d", TIMER_SECONDS);

        if (GAME_STATE == GameState.PREP) {
            for (var t2 : teamList.values()) {
                t2.score(t2.score() + 1);

                t2.bossBar().name(mm.deserialize("<green>Preparation Time<gold> " + timeText));
                t2.bossBar().color(BossBar.Color.GREEN);
                t2.bossBar().progress(progress);
            }
        }

        if (GAME_STATE == GameState.FIGHT) {
            for (var t2 : teamList.values()) {
                if (t2.flagStolenBy() != null) {
                    t2.score(t2.score() + 1);
                }

                t2.bossBar().name(mm.deserialize("<green>Battle Time<gold> " + timeText));
                t2.bossBar().color(BossBar.Color.GREEN);
                t2.bossBar().progress(progress);
            }
        }
    }

    @NotNull
    @Override
    public Consumer<ScheduledTask> andThen(@NotNull Consumer<? super ScheduledTask> after) {
        return Consumer.super.andThen(after);
    }
}
