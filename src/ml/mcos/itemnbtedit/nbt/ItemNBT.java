package ml.mcos.itemnbtedit.nbt;

import ml.mcos.itemnbtedit.ItemNBTEdit;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ItemNBT {
    String OBC_PACKAGE = Bukkit.getServer().getClass().getPackage().getName();
    int mcVersion = ItemNBTEdit.mcVersion;

    void getItemNBT(Player player, ItemStack item);

    @SuppressWarnings("deprecation")
    default void showItemNBT(Player player, String nbt) {
        player.sendMessage("§3NBT:§6" + nbt);
        boolean haveColorChar = nbt.indexOf('§') != -1;
        if (mcVersion <= 7 && ItemNBTEdit.mcVersionPatch != 10) {
            player.sendMessage("§c错误: 当前版本不支持直接发送JSON消息,请手动打开游戏日志文件复制NBT");
            return;
        }
        TextComponent edit = new TextComponent(haveColorChar ? " §7[修改]" : " §b[§a修改§b]");
        String text;
        if (haveColorChar) {
            text = "§cNBT中包含客户端无法输入的颜色字符,请复制NBT,\n在其他地方修改完成后到服务器控制台中粘贴";
        } else if (nbt.length() > 251) {
            text = "§cNBT过长,聊天栏中无法完全存放,请复制NBT,\n在其他地方修改完成后到命令方块或控制台中粘贴";
            edit.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/nbt " + nbt));
        } else {
            text = "§d点击修改NBT";
            edit.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/nbt " + nbt));
        }
        if (mcVersion < 16) {
            edit.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(text)));
        } else {
            edit.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(text)));
        }
        TextComponent copy = new TextComponent(" §b[§a复制§b]");
        if (mcVersion < 15) {
            copy.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, nbt));
        } else {
            copy.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, nbt));
        }
        text = "§d点击复制NBT";
        if (mcVersion < 16) {
            if (mcVersion < 15) {
                text = "§c当前游戏版本不支持直接复制到剪贴板,\n只能放到聊天栏中手动按Ctrl+A、Ctrl+C复制,\n如果NBT长度超过聊天栏输入上限,请手动去游戏日志中复制";
            }
            copy.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(text)));
        } else {
            copy.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(text)));
        }
        player.spigot().sendMessage(edit, copy);
    }

    Result asItemNBT(Player player, String nbt);

    enum Result {
        SUCCESS, EXCEPTION, AIR
    }
}
