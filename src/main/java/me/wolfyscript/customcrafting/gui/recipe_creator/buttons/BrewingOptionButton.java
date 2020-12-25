package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BrewingOptionButton extends ActionButton<CCCache> {

    public BrewingOptionButton(String id, Material material, String option) {
        super(id, material, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getBrewingGUICache().setOption(option);
            return true;
        });
    }

    public BrewingOptionButton(Material material, String option) {
        super(option + ".option", material, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getBrewingGUICache().setOption(option);
            return true;
        });
    }

    public BrewingOptionButton(ItemStack itemStack, String option) {
        super(option + ".option", itemStack, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getBrewingGUICache().setOption(option);
            return true;
        });
    }
}
