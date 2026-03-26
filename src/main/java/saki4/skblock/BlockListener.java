package saki4.skblock;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {

    private final SKBlock plugin;

    public BlockListener(SKBlock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();
        String worldName = b.getWorld().getName();
        String materialName = b.getType().name();

        boolean inBanWorld = plugin.getConfig().getStringList("banworld").contains(worldName);
        boolean inUnbanWorld = plugin.getConfig().getStringList("unbanwolrd").contains(worldName);

        if (inBanWorld && !inUnbanWorld) {
            if (plugin.getPlaseConfig().getStringList("blocks").contains(materialName)) {
                if (!p.hasPermission("skblock.plase")) {
                    e.setCancelled(true);
                    p.sendMessage(plugin.getConfig().getString("blockplase"));
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();
        String worldName = b.getWorld().getName();
        String materialName = b.getType().name();

        boolean inBanWorld = plugin.getConfig().getStringList("banworld").contains(worldName);
        boolean inUnbanWorld = plugin.getConfig().getStringList("unbanwolrd").contains(worldName);

        if (inBanWorld && !inUnbanWorld) {
            if (plugin.getBreakConfig().getStringList("blocks").contains(materialName)) {
                if (!p.hasPermission("skblock.break")) {
                    e.setCancelled(true);
                    p.sendMessage(plugin.getConfig().getString("blockbreak"));
                }
            }
        }
    }
}