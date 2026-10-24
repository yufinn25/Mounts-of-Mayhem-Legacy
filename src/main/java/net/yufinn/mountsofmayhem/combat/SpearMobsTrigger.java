package net.yufinn.mountsofmayhem.combat;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.yufinn.mountsofmayhem.MountsOfMayhem;

public class SpearMobsTrigger extends SimpleCriterionTrigger<SpearMobsTrigger.Instance> {
    public static final ResourceLocation ID = MountsOfMayhem.id("spear_mobs");
    public static final SpearMobsTrigger INSTANCE = new SpearMobsTrigger();

    public static void register(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> CriteriaTriggers.register(INSTANCE));
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected Instance createInstance(JsonObject json, ContextAwarePredicate player, DeserializationContext context) {
        return new Instance(player, GsonHelper.getAsInt(json, "count", 1));
    }

    public void trigger(ServerPlayer player, int count) {
        trigger(player, instance -> count >= instance.count);
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        private final int count;

        public Instance(ContextAwarePredicate player, int count) {
            super(ID, player);
            this.count = count;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject json = super.serializeToJson(context);
            json.addProperty("count", count);
            return json;
        }
    }
}
