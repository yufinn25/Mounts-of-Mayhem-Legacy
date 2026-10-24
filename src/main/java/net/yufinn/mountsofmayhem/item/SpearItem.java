package net.yufinn.mountsofmayhem.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.yufinn.mountsofmayhem.combat.SpearAttacks;

public class SpearItem extends TieredItem implements Vanishable {
    private final Multimap<Attribute, AttributeModifier> attributes;
    private final ChargeStats chargeStats;
    private final float attackSpeedValue;

    public SpearItem(Tier tier, float damage, float attackSpeed, ChargeStats chargeStats, Properties properties) {
        super(tier, properties);
        this.chargeStats = chargeStats;
        this.attackSpeedValue = attackSpeed;
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", damage - 1.0F, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", attackSpeed - 4.0F, AttributeModifier.Operation.ADDITION));
        this.attributes = builder.build();
    }

    public int getJabDuration() {
        return Math.round(20.0F / this.attackSpeedValue);
    }

    public ChargeStats getChargeStats() {
        return this.chargeStats;
    }

    public record ChargeStats(int delay, int engaged, int tired, int disengaged, double dismountSpeed, float multiplier) {}

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributes : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return false;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return 1.0F;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, entity -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.CUSTOM;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        if (!level.isClientSide) level.playSound(null, player.getX(), player.getY(), player.getZ(), net.yufinn.mountsofmayhem.combat.SpearAttacks.wood(stack) ? net.yufinn.mountsofmayhem.entity.ModSounds.ITEM_SPEAR_WOOD_USE.get() : net.yufinn.mountsofmayhem.entity.ModSounds.ITEM_SPEAR_USE.get(), net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remaining) {
        if (!level.isClientSide) SpearAttacks.charge(user, stack, this, this.getUseDuration(stack) - remaining);
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity user, int count) {
        if (!user.level().isClientSide) SpearAttacks.stopCharging(user);
    }

    @Override
    public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() == newStack.getItem();
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (entity instanceof ServerPlayer player) SpearAttacks.jab(player, stack);
        return false;
    }

    @Override
    public int getBurnTime(ItemStack stack, @javax.annotation.Nullable net.minecraft.world.item.crafting.RecipeType<?> recipeType) {
        return this.getTier() == net.minecraft.world.item.Tiers.WOOD ? 200 : -1;
    }

    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer) {
        consumer.accept(net.yufinn.mountsofmayhem.client.SpearClientExtensions.INSTANCE);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment != Enchantments.SWEEPING_EDGE && (enchantment.category == EnchantmentCategory.WEAPON || enchantment.category == EnchantmentCategory.BREAKABLE || super.canApplyAtEnchantingTable(stack, enchantment));
    }
}
