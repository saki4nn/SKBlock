package saki4.skblock;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SkBlockCommand implements CommandExecutor, TabCompleter {

    private final SKBlock plugin;

    public SkBlockCommand(SKBlock plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("skblock.admin")) {
            sender.sendMessage(plugin.getConfig().getString("messages.perm"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§cИспользование: /skblock [plase/break/delete/rightnow] ...");
            return true;
        }

        String type = args[0].toLowerCase();

        // Команда мгновенной очистки
        if (type.equals("rightnow")) {
            plugin.clearBlocks();
            sender.sendMessage("§aОчистка блоков запущена вручную!");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cИспользование: /skblock [plase/break/delete] [add/rem/time]");
            return true;
        }

        String action = args[1].toLowerCase();

        if (type.equals("delete") && action.equals("time")) {
            if (args.length < 3) {
                sender.sendMessage("§cУкажите время в секундах!");
                return true;
            }
            try {
                long time = Long.parseLong(args[2]);
                plugin.getConfig().set("delay", time);
                plugin.saveConfig();
                plugin.startClearTask();
                sender.sendMessage(plugin.getConfig().getString("messages.time").replace("%time%", String.valueOf(time)));
            } catch (NumberFormatException e) {
                sender.sendMessage("§cВремя должно быть числом!");
            }
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭту команду может использовать только игрок!");
            return true;
        }

        Player p = (Player) sender;
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR || !item.getType().isBlock()) {
            p.sendMessage("§cВозьмите блок в руку!");
            return true;
        }

        String materialName = item.getType().name();
        FileConfiguration targetConfig = null;

        if (type.equals("plase")) targetConfig = plugin.getPlaseConfig();
        else if (type.equals("break")) targetConfig = plugin.getBreakConfig();
        else if (type.equals("delete")) targetConfig = plugin.getDeleteConfig();

        if (targetConfig == null) {
            p.sendMessage("§cНеизвестный тип!");
            return true;
        }

        List<String> blocks = targetConfig.getStringList("blocks");

        if (action.equals("add")) {
            if (!blocks.contains(materialName)) {
                blocks.add(materialName);
                targetConfig.set("blocks", blocks);
                plugin.saveCustomFiles();
            }
            p.sendMessage(plugin.getConfig().getString("messages.add").replace("%name%", materialName));
        } else if (action.equals("rem")) {
            if (blocks.contains(materialName)) {
                blocks.remove(materialName);
                targetConfig.set("blocks", blocks);
                plugin.saveCustomFiles();
            }
            p.sendMessage(plugin.getConfig().getString("messages.rem").replace("%name%", materialName));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return Arrays.asList("plase", "break", "delete", "rightnow");
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("delete")) return Arrays.asList("add", "rem", "time");
            return Arrays.asList("add", "rem");
        }
        return new ArrayList<>();
    }
}