package me.xjqsh.fallout.init;

import me.xjqsh.fallout.MobMod;
import me.xjqsh.fallout.entity.Moth;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MobMod.MOD_ID);
    public static final RegistryObject<EntityType<Moth>> MOTH = ENTITY_TYPES.register("moth", () -> Moth.TYPE);

    @SubscribeEvent
    public static void addEntityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(Moth.TYPE, Moth.createMothAttribute().build());
    }
}
