package me.wolfyscript.customcrafting.recipes.types.cauldron;

import com.google.gson.JsonObject;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.types.RecipeConfig;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CauldronConfig extends RecipeConfig {

    public CauldronConfig(CustomCrafting customCrafting, String folder, String name) {
        super(customCrafting, folder, "cauldron", name, "cauldron");
    }

    /*
    Memory Config only! Do not use to load config out of files!
     */
    public CauldronConfig(String jsonData, CustomCrafting customCrafting, String namespace, String key) {
        super(jsonData, customCrafting, namespace, "cauldron", key, "cauldron");
    }

    public CauldronConfig(CustomCrafting customCrafting) {
        super(customCrafting, "cauldron");
    }

    public boolean dropItems() {
        return getBoolean("dropItems.enabled");
    }

    public void setDropItems(boolean dropItems) {
        set("dropItems.enabled", dropItems);
    }

    public void setHandItem(CustomItem customItem) {
        saveCustomItem("dropItems.handItem", customItem);
    }

    public CustomItem getHandItem() {
        return getCustomItem("dropItems.handItem");
    }

    public void setXP(float xp) {
        set("exp", xp);
    }

    public float getXP() {
        return (float) getDouble("exp");
    }

    public void setCookingTime(int cookingTime) {
        set("cookingTime", cookingTime);
    }

    public int getCookingTime() {
        return getInt("cookingTime");
    }

    public void setWaterLevel(int waterLevel) {
        set("waterLevel", waterLevel);
    }

    public int getWaterLevel() {
        return getInt("waterLevel");
    }

    public void setWater(boolean noWater) {
        set("water", noWater);
    }

    public boolean needsWater() {
        return getBoolean("water");
    }

    public boolean needsFire() {
        return getBoolean("fire");
    }

    public void setFire(boolean needsFire) {
        set("fire", needsFire);
    }

    public void setIngredients(List<CustomItem> source) {
        set("ingredients", new JsonObject());
        for (int i = 0; i < source.size(); i++) {
            saveCustomItem("ingredients.var" + i, source.get(i));
        }
    }

    public List<CustomItem> getIngredients() {
        List<CustomItem> sources = new ArrayList<>();
        if (get("ingredients") != null) {
            Set<String> variants = getValues("ingredients").keySet();
            for (String variant : variants) {
                sources.add(getCustomItem("ingredients." + variant));
            }
        }
        return sources;
    }

    public void setMythicMob(String mobName, int level, double modX, double modY, double modZ) {
        set("mythicMob.name", mobName);
        set("mythicMob.level", level);
        set("mythicMob.modX", modX);
        set("mythicMob.modY", modY);
        set("mythicMob.modZ", modZ);
    }

    public String getMythicMobName() {
        return getString("mythicMob.name", "<none>");
    }

    public int getMythicMobLevel() {
        return getInt("mythicMob.level", 1);
    }

    public Vector getMythicMobMod() {
        Vector vector = new Vector(getDouble("mythicMob.modX", 0), getDouble("mythicMob.modY", 0.5), getDouble("mythicMob.modZ", 0));
        return vector;
    }
}
