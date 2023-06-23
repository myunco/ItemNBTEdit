package ml.mcos.itemnbtedit;

import ml.mcos.itemnbtedit.metrics.Metrics;
import ml.mcos.itemnbtedit.nbt.ItemNBT;
import ml.mcos.itemnbtedit.nbt.V10AndBefore;
import ml.mcos.itemnbtedit.nbt.V12AndV11;
import ml.mcos.itemnbtedit.nbt.V13AndAfter;
import ml.mcos.itemnbtedit.nbt.V17AndAfter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemNBTEdit extends JavaPlugin {
    public static int mcVersion;
    public static int mcVersionPatch;
    private ItemNBT nbt;

    @Override
    public void onEnable() {
        mcVersion = getMinecraftVersion();
        getLogger().info("minecraft version: 1" + mcVersion + mcVersionPatch);
        if (mcVersion < 4) {
            getLogger().severe("错误: 当前游戏版本不受支持");
            getServer().getPluginManager().disablePlugin(this);
        } else if (mcVersion < 7) {
            getLogger().warning("警告: 当前游戏版本仅能查看NBT");
        }
        newNBTObject();
        new Metrics(this, 13403);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                nbt.getItemNBT(player, getItemInMainHand(player));
            } else {
                switch (nbt.asItemNBT(player, mergeString(args, 0))) {
                    case SUCCESS:
                        player.sendMessage("§a生成的物品已添加到你的物品栏。");
                        break;
                    case AIR:
                        player.sendMessage("§6错误: NBT中指定的物品不存在");
                }
            }
        } else {
            if (args.length < 2) {
                sender.sendMessage("错误: 控制台只能使用/nbt <player> <ItemNBT>");
            } else {
                Player player = getServer().getPlayer(args[0]);
                if (player == null) {
                    sender.sendMessage("错误: 无法找到该玩家。");
                } else {
                    switch (nbt.asItemNBT(player, mergeString(args, 1))) {
                        case SUCCESS:
                            sender.sendMessage("生成的物品已添加到该玩家的物品栏。");
                            break;
                        case AIR:
                            sender.sendMessage("§6错误: NBT中指定的物品不存在");
                    }
                }
            }
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    private ItemStack getItemInMainHand(Player player) {
        if (mcVersion < 9) {
            return player.getItemInHand();
        }
        return player.getInventory().getItemInMainHand();
    }

    private int getMinecraftVersion() {
        String[] version = getServer().getBukkitVersion().replace('-', '.').split("\\.");
        try {
            mcVersionPatch = Integer.parseInt(version[2]);
        } catch (NumberFormatException ignored) {
        }
        return Integer.parseInt(version[1]);
    }

    private void newNBTObject() {
        switch (mcVersion) {
            case 20:
            case 19:
            case 18:
            case 17:
                nbt = new V17AndAfter(mcVersion);
                break;
            case 16:
            case 15:
            case 14:
            case 13:
                nbt = new V13AndAfter();
                break;
            case 12:
            case 11:
                nbt = new V12AndV11();
                break;
            case 10:
            case 9:
            case 8:
            case 7:
                // 6 5 4 Version don't have MojangsonParser class.
            case 6:
            case 5:
            case 4:
                nbt = new V10AndBefore();
                break;
            default:
                getLogger().warning("警告: 当前游戏版本尚未经过测试, 可能无法正常使用。");
                nbt = new V17AndAfter(mcVersion);
        }
    }

    private String mergeString(String[] args, int start) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            if (i != start) {
                builder.append(' ');
            }
            builder.append(args[i]);
        }
        return builder.toString();
    }

}
