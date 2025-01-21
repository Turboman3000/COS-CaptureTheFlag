package de.turboman.ctf;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.turboman.ctf.commands.CTFCommand;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Objects;

public final class CaptureTheFlag extends JavaPlugin {
    public static ArrayList<CTFTeam> teamList = new ArrayList<>();
    public static VoicechatServerApi voicechatAPI;
    public static final String prefix = "<dark_aqua>Capture the Flag <gold>⇒<reset> ";
    public static Plugin plugin;

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

        Objects.requireNonNull(getCommand("ctf")).setExecutor(new CTFCommand());

        Bukkit.getConsoleSender().sendMessage(mm.deserialize(prefix + "<green>Plugin enabled"));
    }
}
