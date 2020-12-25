package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;

@JsonSerialize(using = ICustomRecipe.Serializer.class)
public interface ICustomRecipe<C extends ICustomRecipe<?>> {

    WolfyUtilities getAPI();

    boolean hasNamespacedKey();

    NamespacedKey getNamespacedKey();

    void setNamespacedKey(NamespacedKey namespacedKey);

    RecipeType<C> getRecipeType();

    String getGroup();

    void setGroup(String group);

    @Deprecated
    default CustomItem getCustomResult() {
        return getResult();
    }

    @Deprecated
    default List<CustomItem> getCustomResults(){
        return getResults();
    }

    @Nullable
    default CustomItem getResult() {
        return getResults().size() > 0 ? getResults().get(0) : null;
    }

    List<CustomItem> getResults();

    void setResult(List<CustomItem> result);

    RecipePriority getPriority();

    void setPriority(RecipePriority priority);

    boolean isExactMeta();

    void setExactMeta(boolean exactMeta);

    Conditions getConditions();

    void setConditions(Conditions conditions);

    boolean isHidden();

    void setHidden(boolean hidden);

    /**
     * This method saves the Recipe into the File or the Database.
     * It can also send a confirmation message to the player if the player is not null.
     */
    default boolean save(@Nullable Player player) {
        if (getNamespacedKey() != null) {
            try {
                if (CustomCrafting.hasDataBaseHandler()) {
                    CustomCrafting.getDataBaseHandler().updateRecipe(this, false);
                } else {
                    File file = new File(CustomCrafting.getInst().getDataFolder() + File.separator + "recipes" + File.separator + getNamespacedKey().getNamespace() + File.separator + getRecipeType().getId(), getNamespacedKey().getKey() + ".json");
                    file.getParentFile().mkdirs();
                    if (file.exists() || file.createNewFile()) {
                        JacksonUtil.getObjectWriter(CustomCrafting.getInst().getConfigHandler().getConfig().isPrettyPrinting()).writeValue(file, this);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            if (player != null) {
                getAPI().getChat().sendPlayerMessage(player, "recipe_creator", "save.success");
                getAPI().getChat().sendPlayerMessage(player, "§6" + "recipes/" + getNamespacedKey().getNamespace() + "/" + getRecipeType().getId() + "/" + getNamespacedKey().getKey());
            }
            return true;
        }
        if (player != null) {
            getAPI().getChat().sendPlayerMessage(player, "&c" + "Missing NamespacedKey!");
        }
        return false;
    }

    default boolean save() {
        return save(null);
    }

    default boolean delete(@Nullable Player player) {
        if (getNamespacedKey() != null) {
            Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> CustomCrafting.getInst().getRecipeHandler().unregisterRecipe(this));
            if (CustomCrafting.hasDataBaseHandler()) {
                CustomCrafting.getDataBaseHandler().removeRecipe(getNamespacedKey().getNamespace(), getNamespacedKey().getKey());
                player.sendMessage("§aRecipe deleted!");
                return true;
            } else {
                File file = new File(CustomCrafting.getInst().getDataFolder() + File.separator + "recipes" + File.separator + getNamespacedKey().getNamespace() + File.separator + getRecipeType().getId(), getNamespacedKey().getKey() + ".json");
                System.gc();
                if (file.delete()) {
                    if (player != null) getAPI().getChat().sendPlayerMessage(player, "&aRecipe deleted!");
                    return true;
                } else {
                    file.deleteOnExit();
                    if (player != null)
                        getAPI().getChat().sendPlayerMessage(player, "&cCouldn't delete recipe on runtime! File is being deleted on restart!");
                }
            }
            return false;
        }
        if (player != null) {
            getAPI().getChat().sendPlayerMessage(player, "&c" + "Missing NamespacedKey!");
        }
        return false;
    }

    default boolean delete() {
        return delete(null);
    }

    C clone();

    void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException;

    void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event);

    void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster);

    class Serializer extends StdSerializer<ICustomRecipe> {

        public Serializer() {
            super(ICustomRecipe.class);
        }

        protected Serializer(Class<ICustomRecipe> vc) {
            super(vc);
        }

        @Override
        public void serialize(ICustomRecipe iCustomRecipe, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
            gen.writeStartObject();
            iCustomRecipe.writeToJson(gen, serializerProvider);
            gen.writeEndObject();
        }
    }

}
