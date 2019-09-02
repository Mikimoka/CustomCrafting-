package me.wolfyscript.customcrafting.recipes.campfire;

import me.wolfyscript.customcrafting.configs.custom_configs.campfire.CampfireConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.RecipeChoice;

import java.util.ArrayList;
import java.util.List;

public class CustomCampfireRecipe extends CampfireRecipe implements CustomCookingRecipe<CampfireConfig> {

    private boolean exactMeta;

    private RecipePriority recipePriority;
    private List<CustomItem> result;
    private List<CustomItem> source;
    private String id;
    private CampfireConfig config;

    public CustomCampfireRecipe(CampfireConfig config) {
        super(new NamespacedKey(config.getFolder(), config.getName()), config.getResult().get(0), new RecipeChoice.ExactChoice(new ArrayList<>(config.getSource())), config.getXP(), config.getCookingTime());
        this.id = config.getId();
        this.config = config;
        this.result = config.getResult();
        this.source = config.getSource();
        this.recipePriority = config.getPriority();
        this.exactMeta = config.isExactMeta();
        setGroup(config.getGroup());
    }

    public List<CustomItem> getSource() {
        return source;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public List<CustomItem> getCustomResults() {
        return result;
    }

    @Override
    public RecipePriority getPriority() {
        return recipePriority;
    }

    @Override
    public void load() {

    }

    @Override
    public void save() {

    }

    @Override
    public CampfireConfig getConfig() {
        return config;
    }

    @Override
    public boolean isExactMeta() {
        return exactMeta;
    }
}