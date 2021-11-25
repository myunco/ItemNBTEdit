package ml.mcos.itemnbtedit.nbt;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class V12AndV11 extends V7Base {

    @Override
    protected ItemStack asBukkitCopy(String nbt) throws Throwable {
        try {
            return (ItemStack) asBukkitCopy.invoke(null, ItemStack.getDeclaredConstructor(NBTTagCompound).newInstance(parse.invoke(null, nbt)));
        } catch (InvocationTargetException e) {
            throw e.getCause();
        } catch (Exception e) {
            e.printStackTrace();
            return new ItemStack(Material.AIR);
        }
    }

}
