package me.xjqsh.fallout.item;

import me.xjqsh.fallout.api.entity.IWayPointEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class WayPointTool extends Item {
    public WayPointTool() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    @NotNull
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        Level level = player.level();
        if (!level.isClientSide()) {
            if (entity instanceof IWayPointEntity){
                UUID uuid = entity.getUUID();
                stack.getOrCreateTag().putUUID("entity", uuid);
                player.setItemInHand(hand, stack);
                player.sendSystemMessage(Component.literal("bind entity" + entity.getDisplayName().getString()));
            } else {
                player.sendSystemMessage(Component.literal("target is not a waypoint entity"));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    @NotNull
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            Player player = ctx.getPlayer();
            if (player == null) {
                return InteractionResult.PASS;
            }
            ItemStack stack = ctx.getItemInHand();
            if (!stack.getOrCreateTag().contains("entity", Tag.TAG_INT_ARRAY)) {
                player.sendSystemMessage(Component.literal("please bind an entity first"));
                return InteractionResult.PASS;
            }

            UUID uuid = stack.getOrCreateTag().getUUID("entity");
            if (serverLevel.getEntity(uuid) instanceof LivingEntity living && living instanceof IWayPointEntity entity) {
                if (living.isDeadOrDying()) {
                    stack.getOrCreateTag().remove("entity");
                    player.sendSystemMessage(Component.literal("entity is dead"));
                    return InteractionResult.FAIL;
                }

                BlockPos pos = ctx.getClickedPos().above();
                entity.addWaypoint(pos);
                player.sendSystemMessage(Component.literal("waypoint added: " + pos.toShortString() + ", queue size: " + entity.getWaypoints().size()));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
