package net.yufinn.mountsofmayhem.client;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.CamelRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.camel.Camel;
import net.yufinn.mountsofmayhem.MountsOfMayhem;

public class CamelHuskRenderer extends CamelRenderer {
    private static final ResourceLocation TEXTURE = MountsOfMayhem.id("textures/entity/camel/camel_husk.png");

    public CamelHuskRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.CAMEL);
        this.addLayer(new CamelHuskSaddleLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(Camel camel) {
        return TEXTURE;
    }
}
