package de.turboman.ctf;

import de.maxhenkel.voicechat.api.Group;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.UUID;

public class CTFTeam {
    private final UUID id;
    private final String name;
    private final String color;
    private final ArrayList<UUID> players;
    private final Group voiceGroup;
    private final BossBar bossBar;
    private final ArrayList<Material> groundBlocks;
    private UUID leader = null;
    private Location flagLocation = null;
    private UUID flagStolenBy = null;
    private float score = 0;
    private CaptureState state = CaptureState.NOT_CAPTURED;

    public CTFTeam(UUID id, String name, String color, ArrayList<UUID> players, Group voiceGroup, BossBar bossBar) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.players = players;
        this.voiceGroup = voiceGroup;
        this.bossBar = bossBar;

        this.groundBlocks = new ArrayList<>();
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

    public Location flagLocation() {
        return flagLocation;
    }

    public void flagLocation(Location flagLocation) {
        this.flagLocation = flagLocation;
    }

    public BossBar bossBar() {
        return bossBar;
    }

    public UUID flagStolenBy() {
        return flagStolenBy;
    }

    public void flagStolenBy(UUID flagStolenBy) {
        this.flagStolenBy = flagStolenBy;
    }

    public float score() {
        return score;
    }

    public void score(float score) {
        this.score = score;
    }

    public CaptureState state() {
        return state;
    }

    public void state(CaptureState state) {
        this.state = state;
    }

    public ArrayList<Material> groundBlocks() {
        return groundBlocks;
    }
}