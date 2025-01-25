package de.turboman.ctf.maps;

import de.turboman.ctf.CTFTeam;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.MapDecorations;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.Nullable;

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

    public static @Nullable MapCursor.Type getDecoColor(CTFTeam t) {
        return switch (t.color()) {
            case "black" -> MapCursor.Type.BANNER_BLACK;
            case "dark_blue" -> MapCursor.Type.BANNER_BLUE;
            case "dark_green" -> MapCursor.Type.BANNER_GREEN;
            case "dark_aqua" -> MapCursor.Type.BANNER_CYAN;
            case "dark_purple" -> MapCursor.Type.BANNER_PURPLE;
            case "gray" -> MapCursor.Type.BANNER_LIGHT_GRAY;
            case "dark_gray" -> MapCursor.Type.BANNER_GRAY;
            case "green" -> MapCursor.Type.BANNER_LIME;
            case "red" -> MapCursor.Type.BANNER_RED;
            case "light_purple" -> MapCursor.Type.BANNER_MAGENTA;
            case "yellow" -> MapCursor.Type.BANNER_YELLOW;
            case "white" -> MapCursor.Type.BANNER_WHITE;
            default -> null;
        };
    }
}
