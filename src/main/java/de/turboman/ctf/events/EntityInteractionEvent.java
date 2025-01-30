package de.turboman.ctf.events;

import de.turboman.ctf.CaptureTheFlag;
import de.turboman.ctf.FlagInteractionEntity;
import de.turboman.ctf.GameState;
import de.turboman.ctf.commands.CTFCommand;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.UUID;

public class EntityInteractionEvent implements Listener {
    private MiniMessage mm = MiniMessage.miniMessage();

    @EventHandler
    public void onEvent(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked().getType() != EntityType.INTERACTION) return;

        if (CaptureTheFlag.GAME_STATE != GameState.FIGHT) {
            e.getPlayer().sendMessage(mm.deserialize(CaptureTheFlag.prefix + "<red>You can steal flags only in the Battle Phase!"));
            return;
        }

        for (var tag : e.getRightClicked().getScoreboardTags()) {
            if (!tag.startsWith("teamFlag_")) continue;

            var id = tag.replace("teamFlag_", "");
            var team = CaptureTheFlag.teamList.get(UUID.fromString(id));

            if (team.players().contains(e.getPlayer().getUniqueId())) {
                for (var t2 : CaptureTheFlag.teamList.values()) {
                    if (t2.flagStolenBy() != e.getPlayer().getUniqueId()) continue;

                    team.score(team.score() + 30);
                    t2.flagStolenBy(null);

                    t2.flagLocation().getBlock().setType(CTFCommand.getFlagItem(t2).getType());
                    FlagInteractionEntity.spawnEntity(t2.id(), t2.flagLocation());
                    CaptureTheFlag.scoreObjec.getScore("t1_" + t2.id()).customName(mm.deserialize("<" + t2.color() + ">" + t2.name() + " <dark_gray>-<green><b> ✓"));

                    for (var p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage(mm.deserialize(CaptureTheFlag.prefix + "<" + t2.color() + ">" + t2.name() + "'s<green> Flag was captured by <gold>" + e.getPlayer().getName() + "<" + team.color() + "> (" + team.name() + ")"));
                    }

                    for (var ent : e.getPlayer().getWorld().getEntities()) {
                        if (ent.getType() != EntityType.ITEM_DISPLAY) continue;
                        if (!ent.getScoreboardTags().contains("stolenFlag_" + t2.id())) continue;

                        ent.remove();

                        break;
                    }

                    return;
                }

                e.getPlayer().sendMessage(mm.deserialize(CaptureTheFlag.prefix + "<red>You can't steal your own Team's flag"));
                break;
            }

            for (var pp : team.players()) {
                Bukkit.getPlayer(pp).sendMessage(mm.deserialize(CaptureTheFlag.prefix + "<red>" + e.getPlayer().getName() + " stole your Team's flag!"));
            }

            CaptureTheFlag.scoreObjec.getScore("t1_" + team.id()).customName(mm.deserialize("<" + team.color() + ">" + team.name() + " <dark_gray>-<red><b> x"));

            ItemDisplay itemEntity = (ItemDisplay) e.getPlayer().getLocation().getWorld().spawnEntity(e.getPlayer().getLocation(), EntityType.ITEM_DISPLAY);
            Transformation transform = new Transformation(new Vector3f(0, 0, 0.5f), new AxisAngle4f(1, 0, 0, 0), new Vector3f(1), new AxisAngle4f());

            itemEntity.setTransformation(transform);
            itemEntity.setBillboard(Display.Billboard.FIXED);
            itemEntity.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD);
            itemEntity.setItemStack(new ItemStack(e.getRightClicked().getLocation().getBlock().getType()));
            itemEntity.addScoreboardTag("stolenFlag_" + team.id());

            e.getPlayer().addPassenger(itemEntity);

            e.getRightClicked().getLocation().getBlock().setType(Material.AIR);
            e.getRightClicked().remove();

            team.flagStolenBy(e.getPlayer().getUniqueId());
            e.getPlayer().sendMessage(mm.deserialize(CaptureTheFlag.prefix + "<green>You stole the flag from <" + team.color() + ">" + team.name()));

            break;
        }
    }
}
