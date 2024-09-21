package me.xjqsh.fallout.init;

import me.xjqsh.fallout.MobMod;
import me.xjqsh.fallout.item.WayPointTool;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MobMod.MOD_ID);
    public static final RegistryObject<Item> WAYPOINT_TOOL = ITEMS.register("waypoint_tool", WayPointTool::new);
}
