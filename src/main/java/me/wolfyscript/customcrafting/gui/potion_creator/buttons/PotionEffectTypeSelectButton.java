package me.wolfyscript.customcrafting.gui.potion_creator.buttons;

import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.potions.PotionEffects;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Locale;
import java.util.Random;

public class PotionEffectTypeSelectButton extends ActionButton {

    private static final Random random = new Random(System.currentTimeMillis());

    public PotionEffectTypeSelectButton(PotionEffectType effectType) {
        super("potion_effect_type_" + effectType.getName().toLowerCase(), new ButtonState("effect_type", Material.POTION, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            PotionEffects potionEffectCache = ((TestCache) guiHandler.getCustomCache()).getPotionEffectCache();
            if (potionEffectCache.getApplyPotionEffectType() != null) {
                potionEffectCache.getApplyPotionEffectType().applyPotionEffect((TestCache) guiHandler.getCustomCache(), effectType);
            }
            if (!potionEffectCache.getOpenedFromCluster().isEmpty()) {
                guiHandler.changeToInv(potionEffectCache.getOpenedFromCluster(), potionEffectCache.getOpenedFromWindow());
            } else {
                guiHandler.changeToInv(potionEffectCache.getOpenedFromWindow());
            }
            return true;
        }, (replacements, guiHandler, player, itemStack, i, b) -> getPotionEffectTypeItem(effectType)));
    }

    private static ItemStack getPotionEffectTypeItem(PotionEffectType type) {
        ItemStack itemStack = new ItemStack(Material.POTION);
        PotionMeta itemMeta = (PotionMeta) itemStack.getItemMeta();
        itemMeta.setDisplayName("§7§l » §r§7" + StringUtils.capitalize(type.getName().replace("_", " ").toLowerCase(Locale.ROOT)));
        itemMeta.addCustomEffect(new PotionEffect(type, 1, 1), true);
        itemMeta.setColor(Color.fromRGB(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}