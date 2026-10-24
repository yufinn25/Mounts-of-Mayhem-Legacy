package net.yufinn.mountsofmayhem.client;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.yufinn.mountsofmayhem.menu.ModMenus;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yufinn.mountsofmayhem.MountsOfMayhem;
import net.yufinn.mountsofmayhem.entity.ModEntities;

@Mod.EventBusSubscriber(modid = MountsOfMayhem.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {
    private ClientSetup() {}

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(ModMenus.NAUTILUS.get(), NautilusInventoryScreen::new));
    }

    @SubscribeEvent
    public static void layers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(NautilusModel.LAYER, NautilusModel::createAdult);
        event.registerLayerDefinition(NautilusModel.CORAL_LAYER, NautilusModel::createAdultCoral);
        event.registerLayerDefinition(ParchedRenderer.LAYER, ParchedModelLayer::create);
        event.registerLayerDefinition(NautilusModel.SADDLE_LAYER, NautilusModel::createSaddle);
        event.registerLayerDefinition(NautilusModel.ARMOR_LAYER, NautilusModel::createArmor);
        event.registerLayerDefinition(NautilusModel.BABY_LAYER, NautilusModel::createBaby);
    }

    @SubscribeEvent
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.NAUTILUS.get(), NautilusRenderer::new);
        event.registerEntityRenderer(ModEntities.ZOMBIE_NAUTILUS.get(), ZombieNautilusRenderer::new);
        event.registerEntityRenderer(ModEntities.CAMEL_HUSK.get(), CamelHuskRenderer::new);
        event.registerEntityRenderer(ModEntities.PARCHED.get(), ParchedRenderer::new);
    }
}
