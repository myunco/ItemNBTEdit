package ml.mcos.itemnbtedit.nbt;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class V10AndBefore extends V7Base {
    Method createStack;

    public V10AndBefore() {
        try {
            createStack = ItemStack.getMethod("createStack", NBTTagCompound);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
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

}
