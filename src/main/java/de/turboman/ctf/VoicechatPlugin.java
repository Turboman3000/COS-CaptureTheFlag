package de.turboman.ctf;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.LeaveGroupEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;

import java.util.Objects;

public class VoicechatPlugin implements de.maxhenkel.voicechat.api.VoicechatPlugin {

    @Override
    public String getPluginId() {
        return "capture_the_flag";
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
        registration.registerEvent(LeaveGroupEvent.class, this::onGroupLeave);
    }

    private void onGroupLeave(LeaveGroupEvent event) {
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
