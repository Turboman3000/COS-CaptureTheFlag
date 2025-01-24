package de.turboman.ctf;

import de.maxhenkel.voicechat.api.Group;

import java.util.ArrayList;
import java.util.UUID;

public record CTFTeam(UUID id, String name, String color, ArrayList<UUID> players, Group voiceGroup) {
    private static UUID leader = UUID.randomUUID();

    public UUID leader() {
        return leader;
    }

    public void leader(UUID uuid) {
        leader = uuid;
    }
}
