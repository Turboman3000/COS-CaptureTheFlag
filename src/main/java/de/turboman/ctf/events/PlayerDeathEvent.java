package de.turboman.ctf.events;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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

        deadPlayers.add(p.getUniqueId());

        p.sendMessage(mm.deserialize(prefix + "<red><b>You're dead!<!b><green> Wait for a Teammate to revive you!"));
        p.setGlowing(true);
        p.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, Integer.MAX_VALUE, 255, false, false));
    }
}
