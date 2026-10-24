package net.yufinn.mountsofmayhem.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.HorseArmorItem;
import java.util.UUID;

public class NetheriteHorseArmorItem extends HorseArmorItem {
    private static final UUID TOUGHNESS = UUID.fromString("5e1b0a64-7d0c-4a52-9a8e-3b3c2f6a1d01");
    private static final UUID KNOCKBACK = UUID.fromString("5e1b0a64-7d0c-4a52-9a8e-3b3c2f6a1d02");
    private final Multimap<Attribute, AttributeModifier> modifiers = ImmutableMultimap.of(
            Attributes.ARMOR_TOUGHNESS, new AttributeModifier(TOUGHNESS, "Horse armor toughness", 3.0D, AttributeModifier.Operation.ADDITION),
            Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(KNOCKBACK, "Horse armor knockback resistance", 0.1D, AttributeModifier.Operation.ADDITION));

    public NetheriteHorseArmorItem(Properties properties) {
        super(19, "netherite", properties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.CHEST ? modifiers : super.getDefaultAttributeModifiers(slot);
    }
}
