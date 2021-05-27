package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class CraftLimitCondition extends Condition {

    long limit = 0;

    public CraftLimitCondition() {
        super("craft_delay");
        setOption(Conditions.Option.IGNORE);
        setAvailableOptions(Conditions.Option.IGNORE, Conditions.Option.LOWER_EXACT, Conditions.Option.LOWER);
    }

    @Override
    public boolean check(ICustomRecipe<?, ?> recipe, Conditions.Data data) {
        Player player = data.getPlayer();
        if (player != null) {
            CCPlayerData playerStore = PlayerUtil.getStore(player);
            if (playerStore != null) {
                long amount = playerStore.getRecipeCrafts(recipe.getNamespacedKey());
                switch (option) {
                    case IGNORE:
                        return true;
                    case LOWER_EXACT:
                        return amount <= limit;
                    case LOWER:
                        return amount < limit;
                    default:
                        return false;
                }
            }
        }
        return true;
    }

    @Override
    public void readFromJson(JsonNode node) {
        this.limit = node.path("limit").asLong();
    }

    @Override
    public void writeJson(@NotNull JsonGenerator gen) throws IOException {
        gen.writeNumberField("limit", limit);
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }
}
