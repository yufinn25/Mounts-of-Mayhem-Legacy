package net.yufinn.mountsofmayhem.entity;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.yufinn.mountsofmayhem.MountsOfMayhem;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MountsOfMayhem.MOD_ID);
    public static final RegistryObject<SoundEvent> CAMEL_HUSK_AMBIENT = sound("entity.camel_husk.ambient");
    public static final RegistryObject<SoundEvent> CAMEL_HUSK_HURT = sound("entity.camel_husk.hurt");
    public static final RegistryObject<SoundEvent> CAMEL_HUSK_DEATH = sound("entity.camel_husk.death");
    public static final RegistryObject<SoundEvent> CAMEL_HUSK_SIT = sound("entity.camel_husk.sit");
    public static final RegistryObject<SoundEvent> CAMEL_HUSK_STAND = sound("entity.camel_husk.stand");
    public static final RegistryObject<SoundEvent> CAMEL_HUSK_EAT = sound("entity.camel_husk.eat");
    public static final RegistryObject<SoundEvent> CAMEL_HUSK_DASH = sound("entity.camel_husk.dash");
    public static final RegistryObject<SoundEvent> CAMEL_HUSK_DASH_READY = sound("entity.camel_husk.dash_ready");
    public static final RegistryObject<SoundEvent> CAMEL_HUSK_STEP = sound("entity.camel_husk.step");
    public static final RegistryObject<SoundEvent> CAMEL_HUSK_STEP_SAND = sound("entity.camel_husk.step_sand");
    public static final RegistryObject<SoundEvent> PARCHED_AMBIENT = sound("entity.parched.ambient");
    public static final RegistryObject<SoundEvent> PARCHED_HURT = sound("entity.parched.hurt");
    public static final RegistryObject<SoundEvent> PARCHED_DEATH = sound("entity.parched.death");
    public static final RegistryObject<SoundEvent> PARCHED_STEP = sound("entity.parched.step");
    public static final RegistryObject<SoundEvent> ITEM_SPEAR_ATTACK = sound("item.spear.attack");
    public static final RegistryObject<SoundEvent> ITEM_SPEAR_HIT = sound("item.spear.hit");
    public static final RegistryObject<SoundEvent> ITEM_SPEAR_LUNGE = sound("item.spear.lunge");
    public static final RegistryObject<SoundEvent> ITEM_SPEAR_USE = sound("item.spear.use");
    public static final RegistryObject<SoundEvent> ITEM_SPEAR_WOOD_ATTACK = sound("item.spear_wood.attack");
    public static final RegistryObject<SoundEvent> ITEM_SPEAR_WOOD_HIT = sound("item.spear_wood.hit");
    public static final RegistryObject<SoundEvent> ITEM_SPEAR_WOOD_USE = sound("item.spear_wood.use");
    public static final RegistryObject<SoundEvent> NAUTILUS_AMBIENT = sound("entity.nautilus.ambient");
    public static final RegistryObject<SoundEvent> NAUTILUS_AMBIENT_LAND = sound("entity.nautilus.ambient_land");
    public static final RegistryObject<SoundEvent> NAUTILUS_SWIM = sound("entity.nautilus.swim");
    public static final RegistryObject<SoundEvent> NAUTILUS_DASH = sound("entity.nautilus.dash");
    public static final RegistryObject<SoundEvent> NAUTILUS_DASH_LAND = sound("entity.nautilus.dash_land");
    public static final RegistryObject<SoundEvent> NAUTILUS_DASH_READY = sound("entity.nautilus.dash_ready");
    public static final RegistryObject<SoundEvent> NAUTILUS_DASH_READY_LAND = sound("entity.nautilus.dash_ready_land");
    public static final RegistryObject<SoundEvent> NAUTILUS_HURT = sound("entity.nautilus.hurt");
    public static final RegistryObject<SoundEvent> NAUTILUS_HURT_LAND = sound("entity.nautilus.hurt_land");
    public static final RegistryObject<SoundEvent> NAUTILUS_DEATH = sound("entity.nautilus.death");
    public static final RegistryObject<SoundEvent> NAUTILUS_DEATH_LAND = sound("entity.nautilus.death_land");
    public static final RegistryObject<SoundEvent> NAUTILUS_EAT = sound("entity.nautilus.eat");
    public static final RegistryObject<SoundEvent> NAUTILUS_RIDING = sound("entity.nautilus.riding");
    public static final RegistryObject<SoundEvent> SADDLE_EQUIP = sound("item.nautilus_saddle_equip");
    public static final RegistryObject<SoundEvent> SADDLE_UNDERWATER_EQUIP = sound("item.nautilus_saddle_underwater_equip");
    public static final RegistryObject<SoundEvent> SADDLE_UNEQUIP = sound("item.saddle.unequip");
    public static final RegistryObject<SoundEvent> ARMOR_EQUIP_NAUTILUS = sound("item.armor.equip_nautilus");
    public static final RegistryObject<SoundEvent> ARMOR_UNEQUIP_NAUTILUS = sound("item.armor.unequip_nautilus");
    public static final RegistryObject<SoundEvent> ZOMBIE_AMBIENT = sound("entity.zombie_nautilus.ambient");
    public static final RegistryObject<SoundEvent> ZOMBIE_AMBIENT_LAND = sound("entity.zombie_nautilus.ambient_land");
    public static final RegistryObject<SoundEvent> ZOMBIE_SWIM = sound("entity.zombie_nautilus.swim");
    public static final RegistryObject<SoundEvent> ZOMBIE_DASH = sound("entity.zombie_nautilus.dash");
    public static final RegistryObject<SoundEvent> ZOMBIE_DASH_LAND = sound("entity.zombie_nautilus.dash_land");
    public static final RegistryObject<SoundEvent> ZOMBIE_DASH_READY = sound("entity.zombie_nautilus.dash_ready");
    public static final RegistryObject<SoundEvent> ZOMBIE_DASH_READY_LAND = sound("entity.zombie_nautilus.dash_ready_land");
    public static final RegistryObject<SoundEvent> ZOMBIE_HURT = sound("entity.zombie_nautilus.hurt");
    public static final RegistryObject<SoundEvent> ZOMBIE_HURT_LAND = sound("entity.zombie_nautilus.hurt_land");
    public static final RegistryObject<SoundEvent> ZOMBIE_DEATH = sound("entity.zombie_nautilus.death");
    public static final RegistryObject<SoundEvent> ZOMBIE_DEATH_LAND = sound("entity.zombie_nautilus.death_land");
    public static final RegistryObject<SoundEvent> ZOMBIE_EAT = sound("entity.zombie_nautilus.eat");

    private static RegistryObject<SoundEvent> sound(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(MountsOfMayhem.id(name)));
    }
}
