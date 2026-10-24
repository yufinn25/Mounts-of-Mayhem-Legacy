package net.yufinn.mountsofmayhem.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.yufinn.mountsofmayhem.MountsOfMayhem;
import net.yufinn.mountsofmayhem.entity.NautilusEntity;
import net.yufinn.mountsofmayhem.entity.ZombieNautilusEntity;

public class ZombieNautilusRenderer extends NautilusRenderer {
    private static final ResourceLocation TEMPERATE = MountsOfMayhem.id("textures/entity/nautilus/zombie_nautilus.png");
    private static final ResourceLocation CORAL = MountsOfMayhem.id("textures/entity/nautilus/zombie_nautilus_coral.png");
    private final NautilusModel coralModel;

    public ZombieNautilusRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.coralModel = new NautilusModel(context.bakeLayer(NautilusModel.CORAL_LAYER));
    }

    @Override
    protected NautilusModel modelFor(NautilusEntity entity) {
        return entity instanceof ZombieNautilusEntity zombie && zombie.isCoral() ? this.coralModel : this.adultModel;
    }

    @Override
    public ResourceLocation getTextureLocation(NautilusEntity entity) {
        return entity instanceof ZombieNautilusEntity zombie && zombie.isCoral() ? CORAL : TEMPERATE;
    }
}
