package me.xjqsh.fallout.api.entity;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;

/**
 * 代表一个可以预设路径点的实体
 */
public interface IWayPointEntity {
    Queue<BlockPos> getWaypoints();

    @Nullable
    default BlockPos getNextWaypoint() {
        return getWaypoints().peek();
    }

    default void addWaypoint(BlockPos pos) {
        getWaypoints().add(pos);
    }
}
