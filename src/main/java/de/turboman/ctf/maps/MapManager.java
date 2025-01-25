package de.turboman.ctf.maps;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.MapDecorations;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import java.util.HashMap;
import java.util.UUID;

public class MapManager {
    public final static HashMap<UUID, MapData> playerMaps = new HashMap<>();

    public static void initMap(Player player) {
        var world = player.getLocation().getWorld();
        var border = world.getWorldBorder();
        var view = Bukkit.createMap(world);

        view.setCenterX(border.getCenter().getBlockX());
        view.setCenterZ(border.getCenter().getBlockZ());

        view.setScale(MapView.Scale.NORMAL);
        view.setUnlimitedTracking(true);
        view.setTrackingPosition(true);

        playerMaps.put(player.getUniqueId(), new MapData(view, new HashMap<>()));
    }

    public static ItemStack getMapItem(UUID uuid) {
        MapData data = playerMaps.get(uuid);
        ItemStack mapItem = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) mapItem.getItemMeta();

        meta.setMapView(data.view());

        mapItem.setData(DataComponentTypes.MAP_DECORATIONS, MapDecorations.mapDecorations().putAll(data.decorations()).build());
        mapItem.setItemMeta(meta);

        return mapItem;
    }
}
