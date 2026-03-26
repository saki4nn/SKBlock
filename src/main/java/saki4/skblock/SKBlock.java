package saki4.skblock;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SKBlock extends JavaPlugin {

    private File plaseFile, breakFile, deleteFile;
    private FileConfiguration plaseConfig, breakConfig, deleteConfig;
    private BukkitTask clearTask;

    @Override
    public void onEnable() {
        // Приветственное сообщение
        Bukkit.getConsoleSender().sendMessage("§b[SkBlock] §fПлагин зделан студией §6SkyKnock DeV§f. §7https://t.me/skyknockdev");

        saveDefaultConfig();
        createCustomFiles();

        getCommand("skblock").setExecutor(new SkBlockCommand(this));
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);

        startClearTask();
        getLogger().info("SkBlock успешно запущен!");
    }

    @Override
    public void onDisable() {
        saveCustomFiles();
        getLogger().info("SkBlock выключен!");
    }

    private void createCustomFiles() {
        plaseFile = new File(getDataFolder(), "plase.yml");
        breakFile = new File(getDataFolder(), "break.yml");
        deleteFile = new File(getDataFolder(), "delete.yml");

        try {
            if (!plaseFile.exists()) { plaseFile.getParentFile().mkdirs(); plaseFile.createNewFile(); }
            if (!breakFile.exists()) { breakFile.createNewFile(); }
            if (!deleteFile.exists()) { deleteFile.createNewFile(); }
        } catch (IOException e) {
            e.printStackTrace();
        }

        plaseConfig = YamlConfiguration.loadConfiguration(plaseFile);
        breakConfig = YamlConfiguration.loadConfiguration(breakFile);
        deleteConfig = YamlConfiguration.loadConfiguration(deleteFile);
    }

    public void saveCustomFiles() {
        try {
            plaseConfig.save(plaseFile);
            breakConfig.save(breakFile);
            deleteConfig.save(deleteFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startClearTask() {
        if (clearTask != null) clearTask.cancel();
        long delay = getConfig().getLong("delay", 86400) * 20L;
        clearTask = Bukkit.getScheduler().runTaskTimer(this, this::clearBlocks, delay, delay);
    }

    // Метод очистки через сканирование (так как data.yml удален)
    public void clearBlocks() {
        List<String> worldsToClear = getConfig().getStringList("deleteworld");
        List<String> targetBlocks = getDeleteConfig().getStringList("blocks");

        if (targetBlocks.isEmpty()) return;

        for (String worldName : worldsToClear) {
            World world = Bukkit.getWorld(worldName);
            if (world == null) continue;

            // Сканируем только загруженные чанки для оптимизации
            for (org.bukkit.Chunk chunk : world.getLoadedChunks()) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = 0; y < world.getMaxHeight(); y++) {
                            Block block = chunk.getBlock(x, y, z);
                            if (targetBlocks.contains(block.getType().name())) {
                                block.setType(Material.AIR);
                            }
                        }
                    }
                }
            }
        }

        String msg = getConfig().getString("blockdelete");
        if (msg != null && !msg.isEmpty()) {
            Bukkit.broadcastMessage(msg);
        }
    }

    public FileConfiguration getPlaseConfig() { return plaseConfig; }
    public FileConfiguration getBreakConfig() { return breakConfig; }
    public FileConfiguration getDeleteConfig() { return deleteConfig; }
}