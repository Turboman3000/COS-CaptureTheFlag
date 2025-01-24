package de.turboman.ctf;

import de.maxhenkel.voicechat.api.Group;

import java.util.ArrayList;
import java.util.UUID;

public class CTFTeam {
    private final UUID id;
    private final String name;
    private final String color;
    private final ArrayList<UUID> players;
    private final Group voiceGroup;
    private UUID leader;

    public CTFTeam(UUID id, String name, String color, ArrayList<UUID> players, Group voiceGroup, UUID leader) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.players = players;
        this.voiceGroup = voiceGroup;
        this.leader = leader;
    }

    public UUID id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String color() {
        return color;
    }

    public ArrayList<UUID> players() {
        return players;
    }

    public Group voiceGroup() {
        return voiceGroup;
    }

    public UUID leader() {
        return leader;
    }

    public void leader(UUID leader) {
        this.leader = leader;
    }
}