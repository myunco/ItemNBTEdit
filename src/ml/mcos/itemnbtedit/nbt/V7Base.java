package ml.mcos.itemnbtedit.nbt;

import ml.mcos.itemnbtedit.ItemNBTEdit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

public class V7Base implements ItemNBT {
    public static final String NMS_PACKAGE = "net.minecraft.server." + OBC_PACKAGE.substring(23);
    Class<?> CraftItemStack;
    Class<?> NBTTagCompound;
    Class<?> ItemStack;
    Method asNMSCopy;
    Method save;
    Method asBukkitCopy;
    Method parse;

    public V7Base() {
        try {
            CraftItemStack = Class.forName(OBC_PACKAGE + ".inventory.CraftItemStack");
            NBTTagCompound = Class.forName(NMS_PACKAGE + ".NBTTagCompound");
            ItemStack = Class.forName(NMS_PACKAGE + ".ItemStack");
            asNMSCopy = CraftItemStack.getMethod("asNMSCopy", ItemStack.class);
            asBukkitCopy = CraftItemStack.getMethod("asBukkitCopy", ItemStack);
            save = ItemStack.getMethod("save", NBTTagCompound);
            if (mcVersion == 7 && ItemNBTEdit.mcVersionPatch != 10) {
                parse = Class.forName(NMS_PACKAGE + ".MojangsonParser").getMethod("a", String.class);
            } else {
                parse = Class.forName(NMS_PACKAGE + ".MojangsonParser").getMethod("parse", String.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected String asNMSCopy(ItemStack item) {
        try {
            return save.invoke(asNMSCopy.invoke(null, item), NBTTagCompound.getDeclaredConstructor().newInstance()).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{id:\"minecraft:air\"}";
    }

    @Override
    public void getItemNBT(Player player, ItemStack item) {
        showItemNBT(player, asNMSCopy(item));
    }

    protected ItemStack asBukkitCopy(String nbt) throws Throwable {
        return null;
    }

    @Override
    public Result asItemNBT(Player player, String nbt) {
        try {
            ItemStack item = asBukkitCopy(nbt);
            if (item.getType() == Material.AIR) {
                return Result.AIR;
            } else {
                player.getInventory().addItem(item);
                return Result.SUCCESS;
            }
        } catch (Throwable t) {
            player.sendMessage("§c无法解析NBT: \n" + t.getMessage());
            return Result.EXCEPTION;
        }
    }
}
