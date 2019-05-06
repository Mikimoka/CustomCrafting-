package me.wolfyscript.customcrafting.configs.custom_configs.workbench;

import me.wolfyscript.customcrafting.configs.custom_configs.CustomConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.items.ItemUtils;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import org.bukkit.Material;

import java.util.*;

public class CraftConfig extends CustomConfig {

    public CraftConfig(ConfigAPI configAPI, String defaultpath, String defaultName, String folder, String name, boolean override) {
        super(configAPI, defaultpath, defaultName, folder, "workbench", name, override);
    }

    public CraftConfig(ConfigAPI configAPI, String defaultName, String folder, String name, boolean override) {
        super(configAPI, defaultName, folder, "workbench", name, override);
    }

    public CraftConfig(ConfigAPI configAPI, String defaultName, String folder, String name) {
        this(configAPI, defaultName, folder, name, false);
    }

    public CraftConfig(ConfigAPI configAPI, String folder, String name) {
        this(configAPI, "craft_config", folder, name);
    }

    public void setShapeless(boolean shapeless) {
        set("shapeless", shapeless);
    }

    public boolean isShapeless() {
        return getBoolean("shapeless");
    }

    public void setPermission(boolean perm) {
        set("permissions", perm);
    }

    public boolean needPerm() {
        return getBoolean("permissions");
    }

    public void setNeedWorkbench(boolean workbench) {
        set("advanced_workbench", workbench);
    }

    public boolean needWorkbench() {
        return getBoolean("advanced_workbench");
    }

    public void setShape(String... shape) {
        set("shape", shape);
    }

    public String[] getShape() {
        List<String> list = getStringList("shape");
        String[] shape = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            shape[i] = list.get(i);
        }
        return shape;
    }

    public void setResult(CustomItem itemStack) {
        saveCustomItem("result", itemStack);
    }

    public CustomItem getResult() {
        return getCustomItem("result");
    }

    public void setIngredients(HashMap<Character, ArrayList<CustomItem>> ingredients) {
        set("ingredients", new HashMap<String, Object>());
        for (char key : ingredients.keySet()) {
            int variant = 0;
            if (!ItemUtils.isEmpty(ingredients.get((char) key))) {
                for (CustomItem customItem : ingredients.get((char) key)) {
                    saveCustomItem("ingredients." + key + ".var" + (variant++), customItem);
                }
            } else {
                for (CustomItem customItem : ingredients.get((char) key)) {
                    if (customItem != null && !customItem.getType().equals(Material.AIR)) {
                        saveCustomItem("ingredients." + key + ".var" + (variant++), customItem);
                    }
                }
            }
        }
    }

    public HashMap<Character, ArrayList<CustomItem>> getIngredients() {
        HashMap<Character, ArrayList<CustomItem>> result = new HashMap<>();
        Set<String> keys = getConfig().getConfigurationSection("ingredients").getKeys(false);
        for (String key : keys) {
            Set<String> itemKeys = getConfig().getConfigurationSection("ingredients." + key).getKeys(false);
            ArrayList<CustomItem> data = new ArrayList<>();
            for (String itemKey : itemKeys) {
                CustomItem itemStack;
                itemStack = getCustomItem("ingredients." + key + "." + itemKey);
                data.add(itemStack);
            }
            result.put(key.charAt(0), data);
        }
        return result;
    }

}
