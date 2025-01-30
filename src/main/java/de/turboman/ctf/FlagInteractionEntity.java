package de.turboman.ctf;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;

import java.util.UUID;

public class FlagInteractionEntity {

    public static void spawnEntity(UUID id, Location loc) {
        Interaction interEntity = (Interaction) loc.getWorld().spawnEntity(loc, EntityType.INTERACTION);

        interEntity.setInteractionHeight(2);
        interEntity.setInteractionWidth(1);
        interEntity.setResponsive(true);
        interEntity.setPersistent(true);
        interEntity.addScoreboardTag("teamFlag_" + id);
    }
}
