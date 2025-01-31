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
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

import static de.turboman.ctf.CaptureTheFlag.*;

public class GameLoop implements Consumer<ScheduledTask> {
    private static MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public void accept(ScheduledTask task) {
        if (TIMER_HOURS == 0
                && TIMER_MINUTES == 0
                && TIMER_SECONDS <= 10
                && TIMER_SECONDS != 0
                && GAME_STATE == GameState.FIGHT) {
            for (var p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(mm.deserialize(prefix + "<green>Game ends in <gold>" + TIMER_SECONDS + "<green> seconds!"));
                p.playSound(Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.MASTER, 1, 0));
            }
        }

        if (TIMER_HOURS == 0
                && TIMER_MINUTES == 0
                && TIMER_SECONDS == 0
                && GAME_STATE == GameState.FIGHT) {
            for (var p : Bukkit.getOnlinePlayers()) {
                for (var b : p.activeBossBars()) {
                    p.hideBossBar(b);
                }

                voicechatAPI.getConnectionOf(p.getUniqueId()).setGroup(null);

                p.playSound(Sound.sound(Key.key("minecraft:entity.wither.death"), Sound.Source.MASTER, 0.85f, 1));
                p.sendMessage(mm.deserialize(prefix + "<gold><b>The Game is Over!"));

                CTFTeam highestPoints = null;

                for (var t : teamList.values()) {
                    if (highestPoints == null || highestPoints.score() < t.score()) {
                        highestPoints = t;
                    }

                    p.sendMessage(mm.deserialize(prefix + "<green>Team <" + t.color() + ">" + t.name() + "<green> has <gold>" + ((int) t.score()) + "<green> points!"));
                }

                assert highestPoints != null;

                p.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ZERO, Duration.ofMillis(5000), Duration.ofMillis(3000)));
                p.sendTitlePart(TitlePart.TITLE, mm.deserialize("<" + highestPoints.color() + ">" + highestPoints.name()));
                p.sendTitlePart(TitlePart.SUBTITLE, mm.deserialize("<gold>wins!"));
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                for (var ent : Bukkit.getWorld("world").getEntities()) {
                    if (ent.getType() == EntityType.INTERACTION
                            || ent.getType() == EntityType.ITEM_DISPLAY) {
                        ent.remove();
                    }
                }

                for (var t : teamList.values()) {
                    scoreObjec.getScore("t0_" + t.id()).resetScore();
                    scoreObjec.getScore("t1_" + t.id()).resetScore();
                    scoreObjec.getScore("t2_" + t.id()).resetScore();

                    t.flagLocation().getBlock().setType(Material.AIR);

                    int index = -1;

                    for (var x = -1; x <= 1; x++) {
                        for (var z = -1; z <= 1; z++) {
                            index++;

                            t.flagLocation().clone().add(x, -1, z).getBlock().setType(t.groundBlocks().get(index));
                        }
                    }
                }

                deadPlayers.clear();

                HashMap<UUID, CTFTeam> keys = (HashMap<UUID, CTFTeam>) teamList.clone();

                for (var key : keys.keySet()) {
                    teamList.remove(key);
                }
            });

            task.cancel();

            return;
        }

        if (TIMER_SECONDS <= 5
                && TIMER_SECONDS != 0
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

        for (var t2 : teamList.values()) {
            float addition = 1;

            int stolenFlags = 0;

            for (var t3 : teamList.values()) {
                if (t3.flagStolenBy() != null) {
                    stolenFlags++;
                }
            }

            if (stolenFlags == teamList.size()) {
                addition = 0;
            } else {
                if (t2.flagStolenBy() != null) {
                    addition = -1;
                }

                for (var t3 : teamList.values()) {
                    if (t2.players().contains(t3.flagStolenBy())) {
                        addition = 0.5f;
                        break;
                    }
                }
            }

            t2.score(t2.score() + addition);

            scoreObjec.getScore("t2_" + t2.id()).customName(mm.deserialize("    <gold>" + (int) t2.score()));

            t2.bossBar().name(mm.deserialize((GAME_STATE == GameState.FIGHT ? "<green>Battle Time" : "<green>Preparation Time") + "<gold> " + timeText));
            t2.bossBar().color(BossBar.Color.GREEN);
            t2.bossBar().progress(progress);
        }
    }

    @NotNull
    @Override
    public Consumer<ScheduledTask> andThen(@NotNull Consumer<? super ScheduledTask> after) {
        return Consumer.super.andThen(after);
    }
}
