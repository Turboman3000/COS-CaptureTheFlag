package de.turboman.ctf;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.events.CreateGroupEvent;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.LeaveGroupEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.Objects;

public class VoicechatPlugin implements de.maxhenkel.voicechat.api.VoicechatPlugin {
    private MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public String getPluginId() {
        return "capture_the_flag";
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
        registration.registerEvent(CreateGroupEvent.class, this::onGroupCreate);
        registration.registerEvent(LeaveGroupEvent.class, this::onGroupLeave);
    }

    private void onGroupCreate(CreateGroupEvent event) {
        if (event.getConnection() == null) return;
        Player player = (Player) event.getConnection().getPlayer().getPlayer();

        if (player.isOp()) return;

        player.sendMessage(mm.deserialize(CaptureTheFlag.prefix + "<red>You cannot create a group!"));
        event.cancel();
    }

    private void onGroupLeave(LeaveGroupEvent event) {
        Player p = (Player) Objects.requireNonNull(event.getConnection()).getPlayer().getPlayer();

        if (CaptureTheFlag.deadPlayers.contains(p.getUniqueId())) return;

        if (Objects.requireNonNull(event.getGroup()).isHidden() && event.getGroup().isPersistent()) {
            event.cancel();
        }
    }

    private void onServerStarted(VoicechatServerStartedEvent event) {
        CaptureTheFlag.voicechatAPI = event.getVoicechat();
    }

    @Override
    public void initialize(VoicechatApi api) {
        de.maxhenkel.voicechat.api.VoicechatPlugin.super.initialize(api);
    }
}
