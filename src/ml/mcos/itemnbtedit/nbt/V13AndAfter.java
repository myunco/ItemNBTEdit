package ml.mcos.itemnbtedit.nbt;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class V13AndAfter extends V7Base {
    Method a;

    public V13AndAfter() {
        try {
            a = ItemStack.getMethod("a", NBTTagCompound);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected ItemStack asBukkitCopy(String nbt) throws Throwable {
        try {
            return (ItemStack) asBukkitCopy.invoke(null, a.invoke(null, parse.invoke(null, nbt)));
        } catch (InvocationTargetException e) {
            throw e.getCause();
        } catch (Exception e) {
            e.printStackTrace();
            return new ItemStack(Material.AIR);
        }
    }
}
