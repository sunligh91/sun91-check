package com.github.sunligh91.check.gui;

import com.github.sunligh91.check.main.Sun91Check;
import com.github.sunligh91.check.main.Sun91CheckTextLoader;
import com.github.sunligh91.domain.Equip;
import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuiUtil {


    public static void playerClickedGui(InventoryClickEvent event,Sun91Check plugin){
        Inventory inventory = event.getInventory();
        String title = inventory.getTitle();
        Player player = (Player)event.getWhoClicked();
        int rawSlot = event.getRawSlot();
        if(Sun91CheckTextLoader.PLUGIN_GUI_TITLE.equals(title)){
            try {
                menuClicked(player, rawSlot, inventory,event,plugin);
            } catch (Exception e){
                event.setCancelled(true);
                player.sendMessage(Sun91CheckTextLoader.PLUGIN_NAME +" " + Sun91CheckTextLoader.EQUIP_CHECK_FALSE);
                e.printStackTrace();
            }

        }
    }

    private static void menuClicked(Player player, int rawSlot, Inventory inventory, InventoryClickEvent event, Sun91Check plugin) {
        if (rawSlot == 5) {
            if (inventory.getItem(3) == null) {
                player.sendMessage(Sun91CheckTextLoader.PLUGIN_NAME +" §e请在左侧放入要鉴定的物品");
                event.setCancelled(true);
            } else {
                ItemStack item = inventory.getItem(3);
                ItemMeta itemMeta = item.getItemMeta();
                List<String> lore = null;
                try {
                    lore = itemMeta.getLore();
                } catch (NullPointerException e){
                    e.printStackTrace();
                    event.setCancelled(true);
                    return;
                }
                boolean canCheck = false;
                for (int i = 0; i < lore.size(); i++) {
                    String s = lore.get(i);
                    if (s.contains("未鉴定的")){
                        String matcher = getMatcher("[未鉴定的][\\u4e00-\\u9fa5]+", s).substring(4);
                        FileConfiguration config = plugin.getConfig();
                        //获取到对应的物品参数列表
                        MemorySection items = (MemorySection) config.get("items");
                        MemorySection memorySection = (MemorySection)items.get(matcher);
                        //金钱扣除
                        Integer money = Integer.parseInt((String) memorySection.get("money"));
                        double balance = plugin.econ.getBalance(player.getName());
                        if (balance > money && money>0) {
                            plugin.econ.withdrawPlayer(player.getName(), money);
                            player.sendMessage(Sun91CheckTextLoader.PLUGIN_NAME +" " + Sun91CheckTextLoader.CHECK_MONEY_TRUE.replaceAll("%money%",money.toString()));
                        } else {
                            player.sendMessage(Sun91CheckTextLoader.PLUGIN_NAME +" " + Sun91CheckTextLoader.CHECK_MONEY_FALSE);
                            event.setCancelled(true);
                            return;
                        }
                        //概率物品填装

                        Set<String> keys = memorySection.getKeys(false);
                        keys.remove("money");
                        keys.remove("special");
                        List<Equip> all = new ArrayList<>();
                        for (String key : keys) {
                            MemorySection o = (MemorySection) memorySection.get(key);
                            Integer percent = Integer.parseInt((String) o.get("percent"));
                            String command = (String) o.get("command");
                            for (int j = 0; j < percent ; j++) {
                                all.add(new Equip(key,command));
                            }
                        }
                        //物品随机抽取
                        Random random = new Random();
                        int i1 = random.nextInt(all.size());
                        Equip equip = all.get(i1);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),equip.getCommand().replaceAll("%item%",equip.getEquipName())+" "+player.getName());
                        //特殊物品
                        String special = (String) memorySection.get("special");
                        if (equip.getEquipName().equals(special)){
                            Bukkit.broadcastMessage(Sun91CheckTextLoader.SPECIAL_NOTIFICATION.replaceAll("%player%",player.getName()) + " " + special);
                        } else {
                            player.sendMessage(Sun91CheckTextLoader.PLUGIN_NAME + " " + Sun91CheckTextLoader.EQUIP_CHECK_TRUE);
                        }
                        inventory.setItem(3,null);
                        canCheck=true;
                        break;
                    }
                }
                //确认是否完成鉴定，如果鉴定错误弹出消息
                if (!canCheck) {
                    player.sendMessage(Sun91CheckTextLoader.PLUGIN_NAME +" " + Sun91CheckTextLoader.EQUIP_CHECK_FALSE);
                }
                //终止
                event.setCancelled(true);
            }
        }else if(rawSlot != 3 && rawSlot>=0 && rawSlot<=9 ){
            event.setCancelled(true);
        }
    }

    public static String getMatcher(String regex, String source) {
        Matcher matcher = Pattern.compile(regex).matcher(source);
        String result = "";
        while (matcher.find()) {
            result = matcher.group(0);
        }
        return result;
    }


}
