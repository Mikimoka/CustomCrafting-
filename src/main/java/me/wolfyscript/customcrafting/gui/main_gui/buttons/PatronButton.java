package me.wolfyscript.customcrafting.gui.main_gui.buttons;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

public class PatronButton extends DummyButton<CCCache> {

    private ItemStack head;

    public PatronButton(String name, String minecraftName, String uuid, @NotNull ItemStack head) {
        super("patron." + name.replace(" ", "_").toLowerCase(Locale.ROOT), new ButtonState("", Material.PLAYER_HEAD));
        this.head = head;
        ItemMeta skullMeta = this.head.getItemMeta();
        skullMeta.setDisplayName("§6§l" + name);
        if (!minecraftName.isEmpty()) {
            skullMeta.setLore(Arrays.asList("§8aka. " + minecraftName));
        }
        this.head.setItemMeta(skullMeta);


        Bukkit.getScheduler().runTaskAsynchronously(CustomCrafting.getInst(), () -> {
            try {
                if (!uuid.isEmpty()) {
                    URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.replace("-", "") + "?unsigned=false");
                    InputStreamReader reader = new InputStreamReader(url.openStream());
                    JsonObject textureProperty = (JsonObject) new JsonParser().parse(reader).getAsJsonObject().get("properties").getAsJsonArray().get(0);
                    this.head = PlayerHeadUtils.getViaValue(textureProperty.get("value").getAsString());
                }
            } catch (IOException e) {
                Bukkit.getLogger().info("Could not get skin data from session servers!");
            }
            ItemMeta meta = this.head.getItemMeta();
            meta.setDisplayName("§6§l" + name);
            if (!minecraftName.isEmpty()) {
                meta.setLore(Collections.singletonList("§8aka. " + minecraftName));
            }
            this.head.setItemMeta(meta);

        });
    }

    public PatronButton(String name, String minecraftName, String uuid) {
        this(name, minecraftName, uuid, new ItemStack(Material.CREEPER_HEAD));
    }

    public PatronButton(String name, String minecraftName) {
        this(name, minecraftName, "", new ItemStack(Material.CREEPER_HEAD));
    }

    public PatronButton(String name) {
        this(name, "", "", new ItemStack(Material.CREEPER_HEAD));
    }

    @Override
    public void render(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> guiInventory, Inventory inventory, ItemStack itemStack, int slot, boolean help) {
        inventory.setItem(slot, head);
    }

    /*
    this.uuid = UUID.fromString(uuid);
            try {
                URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.replace("-", "") + "?unsigned=false");
                InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
                JsonObject textureProperty = (JsonObject) new JsonParser().parse(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0);
                this.head = PlayerHeadUtils.getViaValue(textureProperty.get("value").getAsString());

            } catch (IOException e) {
                System.err.println("Could not get skin data from session servers!");
            }
     */
}
