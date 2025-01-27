package de.turboman.ctf.events;

import de.turboman.ctf.CaptureTheFlag;
import de.turboman.ctf.maps.MapManager;
import io.papermc.paper.datacomponent.item.MapDecorations;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static de.turboman.ctf.CaptureTheFlag.deadPlayers;
import static de.turboman.ctf.CaptureTheFlag.prefix;

public class PlayerDeathEvent implements Listener {
    private final MiniMessage mm = MiniMessage.miniMessage();

    @EventHandler
    public void onEvent(org.bukkit.event.entity.PlayerDeathEvent e) {
        Player p = e.getPlayer();

        e.setShouldDropExperience(false);
        e.setKeepInventory(true);
        e.setCancelled(true);

        for (var t : CaptureTheFlag.teamList.values()) {
            if (!t.players().contains(p.getUniqueId())) continue;

            for (var pp : t.players()) {
                MapManager.playerMaps.get(pp).decorations().put("death_" + p.getUniqueId(), MapDecorations.decorationEntry(MapCursor.Type.TARGET_X, p.getLocation().getBlockX(), p.getLocation().getBlockZ(), 0));

                Bukkit.getPlayer(pp).getInventory().setItemInOffHand(MapManager.getMapItem(pp));
            }
        }

        deadPlayers.add(p.getUniqueId());

        p.sendMessage(mm.deserialize(prefix + "<red><b>You're dead!<!b><green> Wait for a Teammate to revive you!"));
        p.setGlowing(true);
        p.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, Integer.MAX_VALUE, 255, false, false));
    }
}
