package de.turboman.ctf;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.events.*;
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
        registration.registerEvent(PlayerConnectedEvent.class, this::onClientConnected);
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

    private void onClientConnected(PlayerConnectedEvent event) {
        Player p = (Player) event.getConnection().getPlayer().getPlayer();

        for (var t : CaptureTheFlag.teamList.values()) {
            if (!t.players().contains(p.getUniqueId())) continue;

            event.getConnection().setGroup(t.voiceGroup());
            break;
        }
    }

    @Override
    public void initialize(VoicechatApi api) {
        de.maxhenkel.voicechat.api.VoicechatPlugin.super.initialize(api);
    }
}
