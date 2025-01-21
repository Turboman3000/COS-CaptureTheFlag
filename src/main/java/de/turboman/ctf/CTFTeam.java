package de.turboman.ctf;

import de.maxhenkel.voicechat.api.Group;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;

public record CTFTeam(String name, String color, ArrayList<UUID> players, @Nullable UUID leader, Group voiceGroup) {
}
