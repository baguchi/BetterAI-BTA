package baguchan.better_ai.mixin;

import baguchan.better_ai.api.IPath;
import baguchan.better_ai.api.IPathGetter;
import baguchan.better_ai.path.BetterNode;
import baguchan.better_ai.path.BetterPath;
import baguchan.better_ai.path.BetterPathFinder;
import baguchan.better_ai.util.BlockPath;
import com.google.common.collect.Maps;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.EntityPathfinder;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.Vec3d;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pathfinder.Path;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.include.com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

@Mixin(value = EntityPathfinder.class, remap = false)
public abstract class EntityPathfinderMixin extends EntityLiving implements IPath, IPathGetter {
	public List<BetterNode> nodeList = Lists.newArrayList();
	public BetterPathFinder pathFinder;
	@Shadow
	protected Entity entityToAttack;
	@Shadow
	protected boolean hasAttacked = false;
	@Shadow
	private Path pathToEntity;

	private Map<BlockPath, Float> pathfindingMalus = Maps.newHashMap();

	public EntityPathfinderMixin(World world) {
		super(world);
	}

	@Override
	protected void init() {
		super.init();
		pathFinder = new BetterPathFinder(world);
	}

	@Inject(method = "updatePlayerActionState", at = @At("TAIL"))
	public void tick(CallbackInfo ci) {
		if (this.pathToEntity != null) {
			//rewrite moving
			Vec3d coordsForNextPath = this.pathToEntity.getPos(this);
			int i = MathHelper.floor_double(this.bb.minY + 0.5);
			if (coordsForNextPath != null) {

				float f3;
				double x1 = coordsForNextPath.xCoord - this.x;
				double z1 = coordsForNextPath.zCoord - this.z;
				double y1 = coordsForNextPath.yCoord - (double) i;
				float f2 = (float) (Math.atan2(z1, x1) * 180.0 / 3.1415927410125732) - 90.0f;
				this.moveForward = this.moveSpeed;
				for (f3 = f2 - this.yRot; f3 < -180.0f; f3 += 360.0f) {
				}
				while (f3 >= 180.0f) {
					f3 -= 360.0f;
				}
				if (f3 > 30.0f) {
					f3 = 30.0f;
				}
				if (f3 < -30.0f) {
					f3 = -30.0f;
				}
				this.yRot += f3;
				if (this.hasAttacked && this.entityToAttack != null) {
					double d4 = x1 - this.x;
					double d5 = z1 - this.z;
					float f5 = this.yRot;
					this.yRot = (float) (Math.atan2(d5, d4) * 180.0 / 3.1415927410125732) - 90.0f;
					float f4 = (f5 - this.yRot + 90.0f) * 3.141593f / 180.0f;
					this.moveStrafing = -MathHelper.sin(f4) * this.moveForward * 1.0f;
					this.moveForward = MathHelper.cos(f4) * this.moveForward * 1.0f;
				}
				if (y1 > 0.0) {
					this.isJumping = true;
				}
			}
		}
	}

	@Redirect(method = "updatePlayerActionState", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/util/phys/Vec3d;squareDistanceTo(DDD)D"))
	public double modifiredSqr(Vec3d instance, double d, double d1, double d2) {
		Vec3d coordsForNextPath = this.pathToEntity.getPos(this);
		return coordsForNextPath.squareDistanceTo(this.x, canMoveDirect() ? this.y : coordsForNextPath.yCoord, this.z);
	}

	@Override
	public void setCurrentPath(List<BetterNode> node) {
		nodeList = node;
	}

	@Override
	public List<BetterNode> getCurrentPath() {
		return nodeList;
	}

	@Override
	public void setPathFinder(BetterPathFinder path) {
		this.pathFinder = path;
	}

	@Override
	public BetterPathFinder getPathFinder() {
		return this.pathFinder;
	}

	@Override
	public Path getTargetPath() {
		return pathToEntity;
	}

	@Override
	public void setTargetPath(BetterPath betterPath) {
		this.pathToEntity = betterPath;
	}

	public float getPathfindingMalus(BlockPath p_21440_) {
		Float f = this.pathfindingMalus.get(p_21440_);
		return f == null ? p_21440_.getMalus() : f;
	}

	public void setPathfindingMalus(BlockPath p_21442_, float p_21443_) {
		this.pathfindingMalus.put(p_21442_, p_21443_);
	}
}
