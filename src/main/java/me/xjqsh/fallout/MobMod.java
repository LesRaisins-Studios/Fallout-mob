package me.xjqsh.fallout;

import com.mojang.logging.LogUtils;
import me.xjqsh.fallout.init.ModEntities;
import me.xjqsh.fallout.init.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(MobMod.MOD_ID)
public class MobMod {
    public static final String MOD_ID = "fallout_monster";
    public static final Logger LOGGER = LogUtils.getLogger();

    @SuppressWarnings("removal")
    public MobMod() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.ITEMS.register(eventBus);
        ModEntities.ENTITY_TYPES.register(eventBus);

    }

}
