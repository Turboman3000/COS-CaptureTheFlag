package de.turboman.ctf;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.turboman.ctf.commands.CTFCommand;
import de.turboman.ctf.events.*;
import de.turboman.ctf.maps.MapManager;
import io.papermc.paper.datacomponent.item.MapDecorations;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.map.MapCursor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class CaptureTheFlag extends JavaPlugin {
    public static ArrayList<UUID> deadPlayers = new ArrayList<>();
    public static HashMap<UUID, CTFTeam> teamList = new HashMap<>();
    public static VoicechatServerApi voicechatAPI;
    public static final String prefix = "<dark_aqua>Capture the Flag <gold>⇒<reset> ";
    public static Plugin plugin;

    public static long PREP_TIME = 60;
    public static long FIGHT_TIME = 60;
    public static long SEARCH_TIME = 5;

    public static long TIMER_HOURS = 0;
    public static long TIMER_MINUTES = 0;
    public static long TIMER_SECONDS = 0;
    public static long TIMER_TOTAL_SECONDS = 0;
    public static long TIMER_SEARCH = 0;

    public static GameState GAME_STATE = GameState.NO_GAME;

    private static MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public void onEnable() {
        plugin = this;

        BukkitVoicechatService service = getServer().getServicesManager().load(BukkitVoicechatService.class);

        if (service != null) {
            service.registerPlugin(new VoicechatPlugin());
        } else {
            Bukkit.getConsoleSender().sendMessage(mm.deserialize(prefix + "<red>VoiceChat plugin not found"));
        }

        getServer().getPluginManager().registerEvents(new PlayerSneakEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerPlaceBlockEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerBreakBlockEvent(), this);
        getServer().getPluginManager().registerEvents(new InventoryClickEvent(), this);
        getServer().getPluginManager().registerEvents(new ItemInteractEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerSwitchOffhandEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathEvent(), this);

        Objects.requireNonNull(getCommand("ctf")).setExecutor(new CTFCommand());

        Bukkit.getConsoleSender().sendMessage(mm.deserialize(prefix + "<green>Plugin enabled"));
    }

    public static void startTimer() {
        TIMER_HOURS = PREP_TIME / 60;
        TIMER_MINUTES = PREP_TIME - (TIMER_HOURS * 60);
        TIMER_TOTAL_SECONDS = getTimerSecs();
        TIMER_SEARCH = SEARCH_TIME * 60;

        Bukkit.getAsyncScheduler().runAtFixedRate(plugin, (task) -> {
            if (TIMER_SECONDS == 0
                    && TIMER_MINUTES == 0
                    && TIMER_HOURS == 0
                    && GAME_STATE == GameState.PREP) {
                TIMER_HOURS = FIGHT_TIME / 60;
                TIMER_MINUTES = FIGHT_TIME - (TIMER_HOURS * 60);
                TIMER_TOTAL_SECONDS = getTimerSecs();

                GAME_STATE = GameState.FIGHT;
            }

            if (TIMER_SEARCH <= 5 && TIMER_SEARCH != 0) {
                for (var p : Bukkit.getOnlinePlayers()) {
                    p.playSound(Sound.sound(Key.key("minecraft:block.note_block.pling"), Sound.Source.MASTER, 1, 2));
                    p.sendMessage(mm.deserialize(prefix + "<green>Positions of all Players will be shown on the Map in <gold>" + TIMER_SEARCH + "<green> seconds!"));
                }
            }

            if (TIMER_SEARCH == 0) {
                TIMER_SEARCH = SEARCH_TIME * 60;


                for (var p : Bukkit.getOnlinePlayers()) {
                    for (var pp : Bukkit.getOnlinePlayers()) {
                        if (pp.getUniqueId() == p.getUniqueId()) continue;

                        MapManager.playerMaps.get(p.getUniqueId()).decorations().remove("player_" + pp.getUniqueId());

                        boolean isTeam = false;

                        for (var t : teamList.values()) {
                            if (!t.players().contains(p.getUniqueId())) continue;
                            if (t.players().contains(pp.getUniqueId())) {
                                isTeam = true;
                                break;
                            }
                        }

                        if (isTeam) {
                            MapManager.playerMaps.get(p.getUniqueId()).decorations().put("player_" + pp.getUniqueId(), MapDecorations.decorationEntry(MapCursor.Type.BLUE_MARKER, pp.getLocation().getBlockX(), pp.getLocation().getBlockZ(), 0));
                        } else {
                            MapManager.playerMaps.get(p.getUniqueId()).decorations().put("player_" + pp.getUniqueId(), MapDecorations.decorationEntry(MapCursor.Type.RED_MARKER, pp.getLocation().getBlockX(), pp.getLocation().getBlockZ(), 0));
                        }
                    }

                    p.getInventory().setItemInOffHand(MapManager.getMapItem(p.getUniqueId()));
                    p.playSound(Sound.sound(Key.key("minecraft:entity.player.levelup"), Sound.Source.MASTER, 1, 2));
                    p.sendMessage(mm.deserialize(prefix + "<green>Positions of all Players are shown on the Map!"));
                }
            }

            TIMER_SEARCH--;
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
                    t2.bossBar().name(mm.deserialize("<green>Preparation Time<gold> " + timeText));
                    t2.bossBar().color(BossBar.Color.GREEN);
                    t2.bossBar().progress(progress);
                }
            }

            if (GAME_STATE == GameState.FIGHT) {
                for (var t2 : teamList.values()) {
                    t2.bossBar().name(mm.deserialize("<green>Battle Time<gold> " + timeText));
                    t2.bossBar().color(BossBar.Color.GREEN);
                    t2.bossBar().progress(progress);
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private static long getTimerSecs() {
        return (TIMER_HOURS * 60 * 60) + (TIMER_MINUTES * 60) + TIMER_SECONDS;
    }
}
