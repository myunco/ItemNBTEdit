package ml.mcos.itemnbtedit.nbt;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class V17AndAfter implements ItemNBT {
    Class<?> CraftItemStack;
    Class<?> NBTTagCompound;
    Class<?> ItemStack;
    Method asNMSCopy;
    Method save;
    Method asBukkitCopy;
    Method createStack;
    Method parse;

    public V17AndAfter(int mcVersion) {
        try {
            CraftItemStack = Class.forName(OBC_PACKAGE + ".inventory.CraftItemStack");
            NBTTagCompound = Class.forName("net.minecraft.nbt.NBTTagCompound");
            ItemStack = Class.forName("net.minecraft.world.item.ItemStack");
            asNMSCopy = CraftItemStack.getMethod("asNMSCopy", ItemStack.class);
            asBukkitCopy = CraftItemStack.getMethod("asBukkitCopy", ItemStack);
            parse = Class.forName("net.minecraft.nbt.MojangsonParser").getMethod(getMethodName(mcVersion, "parse"), String.class);
            save = ItemStack.getMethod(getMethodName(mcVersion, "save"), NBTTagCompound);
            createStack = ItemStack.getMethod(getMethodName(mcVersion, "createStack"), NBTTagCompound);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getMethodName(int mcVersion, String method) {
        switch (method) {
            case "parse":
                if (mcVersion > 17) {
                    return "a";
                }
                break;
            case "save":
                if (mcVersion > 17) {
                    return "b";
                }
                break;
            case "createStack":
                return "a";
        }
        return method;
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
        try {
            return (ItemStack) asBukkitCopy.invoke(null, createStack.invoke(null, parse.invoke(null, nbt)));
        } catch (InvocationTargetException e) {
            throw e.getCause();
        } catch (Exception e) {
            e.printStackTrace();
            return new ItemStack(Material.AIR);
        }
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
