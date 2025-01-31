package de.turboman.ctf;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.turboman.ctf.commands.CTFCommand;
import de.turboman.ctf.events.*;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

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
    public static Scoreboard scoreboard;
    public static Objective scoreObjec;

    public static long PREP_TIME = 60;
    public static long FIGHT_TIME = 60;
    public static long SEARCH_TIME = 5;
    public static boolean SKIP_PREP = false;

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

        getServer().getPluginManager().registerEvents(new EntityInteractionEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerSneakEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerPlaceBlockEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerBreakBlockEvent(), this);
        getServer().getPluginManager().registerEvents(new InventoryClickEvent(), this);
        getServer().getPluginManager().registerEvents(new ItemInteractEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerSwitchOffhandEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathEvent(), this);

        Objects.requireNonNull(getCommand("ctf")).setExecutor(new CTFCommand());

        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        scoreObjec = scoreboard.registerNewObjective("score", Criteria.DUMMY, mm.deserialize("<dark_gray><--- <dark_aqua>Capture the Flag<dark_gray> --->"));
        scoreObjec.setDisplaySlot(DisplaySlot.SIDEBAR);
        scoreObjec.numberFormat(NumberFormat.blank());

        Bukkit.getConsoleSender().sendMessage(mm.deserialize(prefix + "<green>Plugin enabled"));
    }

    public static void startTimer() {
        TIMER_HOURS = PREP_TIME / 60;
        TIMER_MINUTES = PREP_TIME - (TIMER_HOURS * 60);
        TIMER_TOTAL_SECONDS = getTimerSecs();
        TIMER_SEARCH = SEARCH_TIME * 60;

        Bukkit.getAsyncScheduler().runAtFixedRate(plugin, new GameLoop(), 0, 1, TimeUnit.SECONDS);
    }

    static long getTimerSecs() {
        return (TIMER_HOURS * 60 * 60) + (TIMER_MINUTES * 60) + TIMER_SECONDS;
    }
}
