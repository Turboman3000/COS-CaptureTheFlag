package de.turboman.ctf.events;

import de.turboman.ctf.CaptureTheFlag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ShieldMeta;

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

        Bukkit.getScheduler().scheduleSyncRepeatingTask(CaptureTheFlag.plugin, () -> {
            if (!player.isBlocking()) {
                player.getInventory().setItemInOffHand(null);
            }
        }, 0, 1);
    }
}
