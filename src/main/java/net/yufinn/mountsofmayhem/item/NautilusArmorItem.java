package net.yufinn.mountsofmayhem.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import java.util.UUID;
import net.minecraft.world.item.Item;
import net.yufinn.mountsofmayhem.MountsOfMayhem;

public class NautilusArmorItem extends Item {
    private final int defense;
    private final float toughness;
    private final float knockbackResistance;
    private final ResourceLocation texture;
    private static final UUID MODIFIER_ID = UUID.fromString("b3f0d5a2-6c1e-4a57-9a3e-5d7a1c2e8f40");

    public NautilusArmorItem(String material, int defense, float toughness, float knockbackResistance, Properties properties) {
        super(properties.stacksTo(1));
        this.defense = defense;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.texture = new ResourceLocation(MountsOfMayhem.MOD_ID, "textures/entity/nautilus_armor/" + material + ".png");
    }

    public int getDefense() {
        return defense;
    }

    public float getToughness() {
        return toughness;
    }

    public float getKnockbackResistance() {
        return knockbackResistance;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot != EquipmentSlot.CHEST) return super.getDefaultAttributeModifiers(slot);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ARMOR, new AttributeModifier(MODIFIER_ID, "Nautilus armor", defense, AttributeModifier.Operation.ADDITION));
        if (toughness > 0) builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(MODIFIER_ID, "Nautilus armor toughness", toughness, AttributeModifier.Operation.ADDITION));
        if (knockbackResistance > 0) builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(MODIFIER_ID, "Nautilus armor knockback resistance", knockbackResistance, AttributeModifier.Operation.ADDITION));
        return builder.build();
    }
}
