/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.gui.cauldron;

import com.wolfyscript.utilities.bukkit.TagResolverUtil;
import java.util.Map;
import java.util.Optional;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.CacheCauldronWorkstation;
import me.wolfyscript.customcrafting.data.persistent.CauldronBlockData;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.customcrafting.listeners.customevents.CauldronPreCookEvent;
import me.wolfyscript.customcrafting.recipes.CustomRecipeCauldron;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.lib.net.kyori.adventure.text.Component;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.CallbackButtonRender;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CauldronWorkstationMenu extends CCWindow {

    protected static final int INGREDIENT_AMOUNT = 6;
    protected static final String RESULT = "result_slot";

    protected CauldronWorkstationMenu(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, CauldronWorkstationCluster.CAULDRON_MAIN.getKey(), 54, customCrafting);
        setForceSyncUpdate(true);
    }

    @Override
    public void onInit() {
        for (int i = 0; i < INGREDIENT_AMOUNT; i++) {
            int recipeSlot = i;
            getButtonBuilder().action("crafting.slot_" + i).state(state -> state.icon(Material.AIR)
                    .action((cache, guiHandler, player, guiInventory, i1, event) -> event instanceof InventoryClickEvent clickEvent && clickEvent.getSlot() == 25)
                    .postAction((cache, guiHandler, player, guiInventory, itemStack, i1, event) -> {
                        CacheCauldronWorkstation cauldronWorkstation = cache.getCauldronWorkstation();

                        if (cauldronWorkstation.getInput().get(recipeSlot) == null) {
                            // In case the item was put into an empty slot put it into the first empty slot
                            int nextIndex = cauldronWorkstation.getInput().indexOf(null);
                            cauldronWorkstation.getInput().set(nextIndex, itemStack);
                        } else {
                            cauldronWorkstation.getInput().set(recipeSlot, itemStack);
                        }
                        cauldronWorkstation.getBlock().ifPresent(block -> cauldronWorkstation.getBlockData().ifPresent(data -> {
                            for (CustomRecipeCauldron recipeCauldron : customCrafting.getRegistries().getRecipes().getAvailable(RecipeType.CAULDRON, player)) {
                                if (recipeCauldron.checkRecipe(cauldronWorkstation.getInput())) {
                                    CauldronPreCookEvent preCookEvent = new CauldronPreCookEvent(customCrafting, recipeCauldron, player, block);
                                    if (!preCookEvent.isCancelled()) {
                                        //Cache event results
                                        cauldronWorkstation.setPreCookEvent(preCookEvent);
                                    }
                                    return;
                                }
                            }
                        }));
                    }).render((cache, guiHandler, player, guiInventory, itemStack, i1) -> {
                        CacheCauldronWorkstation cauldronWorkstation = cache.getCauldronWorkstation();
                        ItemStack stack = cauldronWorkstation.getInput().get(recipeSlot);
                        if (!ItemUtils.isAirOrNull(stack)) {
                            return CallbackButtonRender.UpdateResult.of(stack);
                        }
                        return CallbackButtonRender.UpdateResult.of(new ItemStack(Material.AIR));
                    })).register();
        }

        for (int resultSlot = 0; resultSlot < 4; resultSlot++) {
            int finalResultSlot = resultSlot;
            getButtonBuilder().action("result_" + resultSlot).state(state -> state.icon(Material.AIR)
                    .action((cache, guiHandler, player, inventory, slot, event) -> false)
                    .postAction((cache, guiHandler, player, inventory, itemStack, i, event) -> cache.getCauldronWorkstation().getBlockData().ifPresent(cauldronBlockData -> cauldronBlockData.getResult()[finalResultSlot] = itemStack))
                    .render((cache, guiHandler, player, inventory, itemStack, slot) -> {
                        ItemStack result = cache.getCauldronWorkstation().getBlockData().map(cauldronBlockData -> cauldronBlockData.getResult()[finalResultSlot]).orElse(ItemUtils.AIR);
                        return CallbackButtonRender.UpdateResult.of(result);
                    })).register();
        }
        getButtonBuilder().action("start").state(state -> state.icon(Material.LIME_CONCRETE).action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            CacheCauldronWorkstation cauldronWorkstation = cache.getCauldronWorkstation();
            cauldronWorkstation.getBlockData().ifPresent(cauldronBlockData -> {
                if (cauldronBlockData.isResultEmpty()) {
                    cauldronWorkstation.resetInput();
                    cauldronBlockData.initNewRecipe(cauldronWorkstation);
                    cauldronWorkstation.setPreCookEvent(null);
                }
            });
            return true;
        })).register();
        getButtonBuilder().dummy("start_disabled").state(state -> state.icon(Material.GRAY_CONCRETE)).register();
        getButtonBuilder().dummy("cauldron_icon").state(s -> s.icon(Material.CAULDRON)).register();
        registerButton(new DummyButton<>("texture_dark", new ButtonState<>(ClusterMain.BACKGROUND, Material.BLACK_STAINED_GLASS_PANE)));
        registerButton(new DummyButton<>("texture_light", new ButtonState<>(ClusterMain.BACKGROUND, Material.BLACK_STAINED_GLASS_PANE)));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        //Prevent super class from rendering
    }

    @Override
    public Component onUpdateTitle(Player player, @Nullable GUIInventory<CCCache> inventory, GuiHandler<CCCache> guiHandler) {
        Optional<CauldronBlockData> optionalData = guiHandler.getCustomCache().getCauldronWorkstation().getBlockData();
        if (optionalData.isPresent()) {
            CauldronBlockData data = optionalData.get();
            String progress = data.getPassedTicks() + " / " + data.getCookingTime();
            return this.wolfyUtilities.getLanguageAPI().getComponent("inventories." + getNamespacedKey().getNamespace() + "." + getNamespacedKey().getKey() + ".gui_name", TagResolverUtil.papi(player), Placeholder.parsed("progress", progress));
        }
        return super.onUpdateTitle(player, inventory, guiHandler);
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> event) {
        for (int i = 0; i < getSize(); i++) {
            event.setButton(i, ClusterMain.GLASS_GRAY);
        }
        CCCache cache = event.getGuiHandler().getCustomCache();
        CacheCauldronWorkstation cacheCauldronWorkstation = cache.getCauldronWorkstation();
        Optional<CauldronBlockData> optionalCauldronBlockData = cacheCauldronWorkstation.getBlockData();
        if (optionalCauldronBlockData.isPresent()) {
            CauldronBlockData data = optionalCauldronBlockData.get();
            event.setButton(25, "result");
            data.getRecipe().ifPresent(customRecipeCauldron -> {
                // Show cooking progress

            });
        }

        event.setButton(11, "crafting.slot_" + 3);
        event.setButton(12, "crafting.slot_" + 4);
        event.setButton(13, "crafting.slot_" + 5);

        event.setButton(19, "crafting.slot_" + 2);
        event.setButton(29, "crafting.slot_" + 1);

        event.setButton(30, "crafting.slot_" + 0);

        event.setButton(39, "cauldron_icon");
        event.setButton(32, "start");

        event.setButton(25, "result_0");
        event.setButton(26, "result_1");
        event.setButton(34, "result_2");
        event.setButton(35, "result_3");
    }

    @Override
    public boolean onClose(GuiHandler<CCCache> guiHandler, GUIInventory<CCCache> guiInventory, InventoryView transaction) {
        Player player = guiHandler.getPlayer();
        World world = player.getWorld();
        CCCache cache = guiHandler.getCustomCache();

        //Reset cache
        CacheCauldronWorkstation cacheCauldronWorkstation = cache.getCauldronWorkstation();
        cacheCauldronWorkstation.setBlockData(null);
        cacheCauldronWorkstation.setBlock(null);
        for (ItemStack itemStack : cacheCauldronWorkstation.getInput()) {
            if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
                Map<Integer, ItemStack> items = player.getInventory().addItem(itemStack);
                items.values().forEach(itemStack1 -> world.dropItemNaturally(player.getLocation(), itemStack1));
            }
        }
        cacheCauldronWorkstation.resetInput();
        return false;
    }

}
