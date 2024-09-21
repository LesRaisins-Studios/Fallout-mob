package me.xjqsh.fallout.entity.goal;

import me.xjqsh.fallout.api.entity.IWayPointEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.Queue;

public class WayPointGoal<E extends PathfinderMob & IWayPointEntity> extends Goal {
    private final E mob;
    private final int within;
    private final double speedModifier;
    private double wantedX;
    private double wantedY;
    private double wantedZ;

    public WayPointGoal(E mob, int within, double speedModifier) {
        this.mob = mob;
        this.within = within;
        this.speedModifier = speedModifier;
    }

    @Override
    public boolean canUse() {
        BlockPos target = null;
        Queue<BlockPos> waypoints = this.mob.getWaypoints();
        while (!waypoints.isEmpty()) {
            int dist = (int)waypoints.peek().distSqr(this.mob.blockPosition());
            if (dist <= 4 || dist >= this.within * this.within) {
                waypoints.poll();
            } else {
                target = waypoints.peek();
                break;
            }
        }
        if (target == null) {
            return false;
        } else if (target.distSqr(this.mob.blockPosition()) > (double)(this.within * this.within)) {
            return false;
        } else {
            Vec3 vec3 = target.getCenter();
            this.wantedX = vec3.x;
            this.wantedY = vec3.y;
            this.wantedZ = vec3.z;
            return true;
        }
    }

    public void start() {
        Path path = this.mob.getNavigation().createPath(this.wantedX, this.wantedY, this.wantedZ, 0);
        this.mob.getNavigation().moveTo(path, this.speedModifier);
    }

    @Override
    public void tick() {
        if (this.mob.getNavigation().getPath() == null || this.mob.getNavigation().getPath().isDone()) {
            Path path = this.mob.getNavigation().createPath(this.wantedX, this.wantedY, this.wantedZ, 0);
            this.mob.getNavigation().moveTo(path, this.speedModifier);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
