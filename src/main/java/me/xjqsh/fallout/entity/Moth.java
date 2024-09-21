package me.xjqsh.fallout.entity;

import me.xjqsh.fallout.api.entity.IWayPointEntity;
import me.xjqsh.fallout.entity.goal.MothMeleeGoal;
import me.xjqsh.fallout.entity.goal.WayPointGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Stack;

public class Moth extends Monster implements GeoEntity, IWayPointEntity {
    public static final EntityType<Moth> TYPE = EntityType.Builder.of(Moth::new, MobCategory.MONSTER)
            .sized(1.6F, 2.85F)
            .build("fallout_monster:moth");

    protected static final RawAnimation FLY_ATTACK = RawAnimation.begin().thenPlay("attack");
    protected static final RawAnimation FLY_IDLE = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation DIE = RawAnimation.begin().thenPlayAndHold("die");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private final Queue<BlockPos> waypoints = new ArrayDeque<>();

    private RandomStrollGoal randomStrollGoal;

    public Moth(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.goalSelector.addGoal(2, new MothMeleeGoal(this, 1.0D, false));
        this.goalSelector.addGoal(3, new MoveTowardsTargetGoal(this, 1.1D, 32.0F));
        this.goalSelector.addGoal(4, new WayPointGoal<>(this, 32, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, true));
    }

    public static AttributeSupplier.Builder createMothAttribute() {
        return Monster.createMonsterAttributes().add(Attributes.KNOCKBACK_RESISTANCE, 0.9D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(ForgeMod.ENTITY_GRAVITY.get(), 0.06D)
                .add(Attributes.MAX_HEALTH, 250.0D)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0D);
    }


    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Flying", 5, this::flyAnimController));
    }

    protected <E extends Moth> PlayState flyAnimController(final AnimationState<E> event) {
        if (this.isDeadOrDying())
            return event.setAndContinue(DIE);

        if (this.swinging){
            return event.setAndContinue(FLY_ATTACK);
        }

        return event.setAndContinue(FLY_IDLE);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public Queue<BlockPos> getWaypoints() {
        return waypoints;
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= 70 && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(Entity.RemovalReason.KILLED);
        }

    }

    @Override
    public void aiStep() {
        super.aiStep();

        Vec3 vec3 = this.getDeltaMovement();
        if (!this.onGround() && vec3.y < 0.0D) {
            this.setDeltaMovement(vec3.multiply(1.0D, 0.75D, 1.0D));
        }

        if(this.level().isClientSide()) return;

        if (!getWaypoints().isEmpty()) {
            if (this.randomStrollGoal != null) {
                this.goalSelector.removeGoal(this.randomStrollGoal);
                this.randomStrollGoal = null;
            }

            BlockPos start = this.blockPosition();
            for (BlockPos pos : getWaypoints()) {
                drawParticleLine(start, pos, ParticleTypes.FLAME, (ServerLevel) this.level());
                start = pos;
            }
        } else {
            if (this.randomStrollGoal == null) {
                this.randomStrollGoal = new RandomStrollGoal(this, 0.7D);
                this.goalSelector.addGoal(7, this.randomStrollGoal);
            }
        }
    }

    public void drawParticleLine(BlockPos pos1, BlockPos pos2, ParticleOptions particle, ServerLevel serverLevel) {
        double distance = Math.sqrt(pos1.distSqr(pos2));
        int particleCount = (int) Math.round(distance); // One particle per block

        double x1 = pos1.getX() + 0.5;
        double y1 = pos1.getY() + 0.5;
        double z1 = pos1.getZ() + 0.5;
        double x2 = pos2.getX() + 0.5;
        double y2 = pos2.getY() + 0.5;
        double z2 = pos2.getZ() + 0.5;

        for (int i = 0; i <= particleCount; i++) {
            double ratio = (double) i / particleCount;
            double x = x1 * (1 - ratio) + x2 * ratio;
            double y = y1 * (1 - ratio) + y2 * ratio;
            double z = z1 * (1 - ratio) + z2 * ratio;

            serverLevel.sendParticles(particle, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0);
        }
    }

    @Override
    protected void updateSwingTime() {
        int i = 18;
        if (this.swinging) {
            ++this.swingTime;
            if (this.swingTime >= i) {
                this.swingTime = 0;
                this.swinging = false;
            }
        }

        this.attackAnim = (float)this.swingTime / (float)i;
    }

    @Override
    public void swing(@NotNull InteractionHand pHand, boolean pUpdateSelf) {
        ItemStack stack = this.getItemInHand(pHand);
        if (!stack.isEmpty() && stack.onEntitySwing(this)) return;
        if (!this.swinging) {
            this.swingTime = 0;
            this.swinging = true;
            this.swingingArm = pHand;
            if (this.level() instanceof ServerLevel) {
                ClientboundAnimatePacket clientboundanimatepacket = new ClientboundAnimatePacket(this, pHand == InteractionHand.MAIN_HAND ? 0 : 3);
                ServerChunkCache serverchunkcache = ((ServerLevel)this.level()).getChunkSource();
                if (pUpdateSelf) {
                    serverchunkcache.broadcastAndSend(this, clientboundanimatepacket);
                } else {
                    serverchunkcache.broadcast(this, clientboundanimatepacket);
                }
            }
        }

    }

    @Override
    public boolean isPushable() {
        return super.isPushable();
    }
}
