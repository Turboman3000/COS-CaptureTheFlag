package de.turboman.ctf;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.turboman.ctf.commands.CTFCommand;
import de.turboman.ctf.events.ItemInteractEvent;
import de.turboman.ctf.events.PlayerMoveEvent;
import de.turboman.ctf.events.PlayerSwitchOffhandEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public final class CaptureTheFlag extends JavaPlugin {
    public static HashMap<UUID, CTFTeam> teamList = new HashMap<>();
    public static VoicechatServerApi voicechatAPI;
    public static final String prefix = "<dark_aqua>Capture the Flag <gold>⇒<reset> ";
    public static Plugin plugin;

    public static long PREP_TIME = 60;
    public static long FIGHT_TIME = 60;

    private MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public void onEnable() {
        plugin = this;

        BukkitVoicechatService service = getServer().getServicesManager().load(BukkitVoicechatService.class);

        if (service != null) {
            service.registerPlugin(new VoicechatPlugin());
        } else {
            Bukkit.getConsoleSender().sendMessage(mm.deserialize(prefix + "<red>VoiceChat plugin not found"));
        }

        getServer().getPluginManager().registerEvents(new ItemInteractEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerSwitchOffhandEvent(), this);

        Objects.requireNonNull(getCommand("ctf")).setExecutor(new CTFCommand());

        Bukkit.getConsoleSender().sendMessage(mm.deserialize(prefix + "<green>Plugin enabled"));
    }
}
