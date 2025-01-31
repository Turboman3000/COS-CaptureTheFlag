package de.turboman.ctf.events;

import org.bukkit.Tag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;

public class InventoryClickEvent implements Listener {

    @EventHandler
    public void onEvent(org.bukkit.event.inventory.InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (e.getClickedInventory().getType() != InventoryType.PLAYER) return;

        if (e.getClick() == ClickType.CREATIVE) {
            return;
        }

        if (e.getClick() == ClickType.SWAP_OFFHAND) {
            e.setCancelled(true);
            return;
        }

        if (e.getCurrentItem() != null) {
            if (Tag.BANNERS.isTagged(e.getCurrentItem().getType())) {
                e.setCancelled(true);
            }
        }

        if (e.getSlot() == 40) {
            e.setCancelled(true);
        }
    }
}
