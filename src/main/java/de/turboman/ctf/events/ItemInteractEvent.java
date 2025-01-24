package de.turboman.ctf.events;

import de.turboman.ctf.CaptureTheFlag;
import de.turboman.ctf.maps.MapManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ShieldMeta;

import java.util.concurrent.TimeUnit;

public class ItemInteractEvent implements Listener {

    @EventHandler
    public void onEvent(PlayerInteractEvent e) {
        if (!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
        if (!(e.getItem().getType() == Material.DIAMOND_SWORD
                || e.getItem().getType() == Material.STONE_SWORD
                || e.getItem().getType() == Material.GOLDEN_SWORD
                || e.getItem().getType() == Material.WOODEN_SWORD
                || e.getItem().getType() == Material.IRON_SWORD
                || e.getItem().getType() == Material.NETHERITE_SWORD)) return;

        Player player = e.getPlayer();

        ItemStack shieldItem = new ItemStack(Material.SHIELD);
        ShieldMeta itemMeta = (ShieldMeta) shieldItem.getItemMeta();

        itemMeta.setUnbreakable(true);

        shieldItem.setItemMeta(itemMeta);
        player.getInventory().setItemInOffHand(shieldItem);

        Bukkit.getAsyncScheduler().runAtFixedRate(CaptureTheFlag.plugin, (task) -> {
            if (!player.isBlocking()) {
                player.getInventory().setItemInOffHand(MapManager.getMapItem(player.getUniqueId()));
                task.cancel();
            }
        }, 500, 150, TimeUnit.MILLISECONDS);
    }
}
