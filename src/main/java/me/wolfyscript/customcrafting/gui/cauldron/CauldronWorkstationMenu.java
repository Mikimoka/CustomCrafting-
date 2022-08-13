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

import java.util.Map;
import java.util.Optional;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.CacheCauldronWorkstation;
import me.wolfyscript.customcrafting.data.persistent.CauldronBlockData;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.CallbackButtonRender;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class CauldronWorkstationMenu extends CCWindow {

    protected static final int INGREDIENT_AMOUNT = 6;
    protected static final String RESULT = "result_slot";

    protected CauldronWorkstationMenu(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, CauldronWorkstationCluster.CAULDRON_MAIN.getKey(), 45, customCrafting);
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
                        cauldronWorkstation.getInput()[recipeSlot] = itemStack;
                    }).render((cache, guiHandler, player, guiInventory, itemStack, i1) -> {
                        CacheCauldronWorkstation cauldronWorkstation = cache.getCauldronWorkstation();
                        ItemStack stack = cauldronWorkstation.getInput()[recipeSlot];
                        if (!ItemUtils.isAirOrNull(stack)) {
                            return CallbackButtonRender.UpdateResult.of(stack);
                        }
                        return CallbackButtonRender.UpdateResult.of(new ItemStack(Material.AIR));
                    })).register();
        }
        getButtonBuilder().action("result").state(state -> state.icon(Material.AIR).action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {

            return false;
        })).register();
        getButtonBuilder().action("result_dummy").state(state -> state.icon(Material.AIR).action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {

            return true;
        }).render((cache, guiHandler, player, guiInventory, itemStack, i) -> {

            return CallbackButtonRender.UpdateResult.of(itemStack);
        })).register();
        getButtonBuilder().action("start").state(state -> state.icon(Material.LIME_CONCRETE).action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            CacheCauldronWorkstation cauldronWorkstation = cache.getCauldronWorkstation();

            cauldronWorkstation.getBlockData().ifPresent(cauldronBlockData -> {


            });

            return false;
        })).register();
        getButtonBuilder().dummy("cauldron_icon").state(s -> s.icon(Material.CAULDRON)).register();
        registerButton(new DummyButton<>("texture_dark", new ButtonState<>(ClusterMain.BACKGROUND, Material.BLACK_STAINED_GLASS_PANE)));
        registerButton(new DummyButton<>("texture_light", new ButtonState<>(ClusterMain.BACKGROUND, Material.BLACK_STAINED_GLASS_PANE)));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        //Prevent super class from rendering
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> event) {
        for (int i = 0; i < getSize(); i++) {
            event.setButton(i, ClusterMain.GLASS_BLACK);
        }
        CCCache cache = event.getGuiHandler().getCustomCache();
        CacheCauldronWorkstation cacheCauldronWorkstation = cache.getCauldronWorkstation();
        Optional<CauldronBlockData> optionalCauldronBlockData = cacheCauldronWorkstation.getBlockData();
        if (optionalCauldronBlockData.isPresent()) {
            CauldronBlockData data = optionalCauldronBlockData.get();
            event.setButton(25, "result");
            data.getRecipe().ifPresentOrElse(customRecipeCauldron -> {
                // Show cooking progress

            }, () -> data.getResult().ifPresent(customItem -> {
                // Allow player to collect the result!


            }));
        }

        int slot;
        for (int i = 0; i < INGREDIENT_AMOUNT; i++) {
            slot = 10 + i + (i / 3) * (9 - 3);
            event.setButton(slot, "crafting.slot_" + i);
        }
        event.setButton(29, "cauldron_icon");
    }

    @Override
    public boolean onClose(GuiHandler<CCCache> guiHandler, GUIInventory<CCCache> guiInventory, InventoryView transaction) {
        Player player = guiHandler.getPlayer();
        World world = player.getWorld();
        CCCache cache = guiHandler.getCustomCache();

        //Reset cache
        CacheCauldronWorkstation cacheCauldronWorkstation = cache.getCauldronWorkstation();
        cacheCauldronWorkstation.setBlockData(null);
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
