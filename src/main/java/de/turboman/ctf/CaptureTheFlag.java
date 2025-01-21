package de.turboman.ctf;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import de.turboman.ctf.commands.CTFCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Objects;

public final class CaptureTheFlag extends JavaPlugin implements VoicechatPlugin {
    public static ArrayList<CTFTeam> teamList = new ArrayList<>();
    public static VoicechatServerApi voicechatAPI;

    @Override
    public void onEnable() {
        Objects.requireNonNull(getCommand("ctf")).setExecutor(new CTFCommand());

        Bukkit.getConsoleSender().sendMessage("[CTF] Plugin enabled");
    }

    @Override
    public void onDisable() {

    }

    @Override
    public String getPluginId() {
        return "capture_the_flag";
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
    }

    private void onServerStarted(VoicechatServerStartedEvent event) {
        voicechatAPI = event.getVoicechat();
    }

    @Override
    public void initialize(VoicechatApi api) {
        VoicechatPlugin.super.initialize(api);
    }
}
