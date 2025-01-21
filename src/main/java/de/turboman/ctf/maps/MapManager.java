package de.turboman.ctf.maps;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import java.util.ArrayList;
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

        /*
         HashMap<String, MapDecorations.DecorationEntry> mapDeco = new HashMap<>();
         mapDeco.put(UUID.randomUUID() + "", MapDecorations.decorationEntry(getDecoColor(t), player.getLocation().getBlockX(), player.getLocation().getBlockZ(), 0));

         mapItem.setData(DataComponentTypes.MAP_DECORATIONS, MapDecorations.mapDecorations().putAll(mapDeco).build());
         */

        playerMaps.put(player.getUniqueId(), new MapData(view, new ArrayList<>()));
    }

    public static ItemStack getMapItem(UUID uuid) {
        MapData data = playerMaps.get(uuid);
        ItemStack mapItem = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) mapItem.getItemMeta();

        meta.setMapView(data.view());

        mapItem.setItemMeta(meta);

        return mapItem;
    }
}
