package de.turboman.ctf;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.turboman.ctf.commands.CTFCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Objects;

public final class CaptureTheFlag extends JavaPlugin {
    public static ArrayList<CTFTeam> teamList = new ArrayList<>();
    public static VoicechatServerApi voicechatAPI;

    @Override
    public void onEnable() {
        BukkitVoicechatService service = getServer().getServicesManager().load(BukkitVoicechatService.class);

        if (service != null) {
            service.registerPlugin(new VoicechatPlugin());

            Objects.requireNonNull(getCommand("ctf")).setExecutor(new CTFCommand());
            
            Bukkit.getConsoleSender().sendMessage("[CTF] Plugin enabled");
        } else {
            Bukkit.getConsoleSender().sendMessage("[CTF] VoiceChat plugin not found");
        }
    }
}
