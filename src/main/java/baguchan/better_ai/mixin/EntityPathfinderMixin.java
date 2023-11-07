package baguchan.better_ai.mixin;

import baguchan.better_ai.IHead;
import baguchan.better_ai.IPath;
import baguchan.better_ai.path.BetterNode;
import baguchan.better_ai.path.BetterPathFinder;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.EntityPathfinder;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.include.com.google.common.collect.Lists;

import java.util.List;

@Mixin(value = EntityPathfinder.class, remap = false)
public abstract class EntityPathfinderMixin extends EntityLiving implements IPath, IHead {
	public List<BetterNode> nodeList = Lists.newArrayList();
	public BetterPathFinder pathFinder;

	protected float xHeadRot;
	protected float yHeadRot;

	protected float xHeadRotO;
	protected float yHeadRotO;

	public EntityPathfinderMixin(World world) {
		super(world);
	}

	@Override
	protected void init() {
		super.init();
		pathFinder = new BetterPathFinder(world);
	}

	@Override
	public void tick() {
		this.xHeadRotO = this.xHeadRot;
		this.yHeadRotO = this.yHeadRot;
		this.xHeadRot = this.xRot;
		this.yHeadRot = this.yRot;
		super.tick();
	}

	public void faceEntity(Entity entity, float f, float f1) {
		if (this instanceof IPath) {
			double d1;
			double d = entity.x - this.x;
			double d2 = entity.z - this.z;
			if (entity instanceof EntityLiving) {
				EntityLiving entityliving = (EntityLiving) entity;
				d1 = this.y + (double) this.getHeadHeight() - (entityliving.y + (double) entityliving.getHeadHeight());
			} else {
				d1 = (entity.bb.minY + entity.bb.maxY) / 2.0 - (this.y + (double) this.getHeadHeight());
			}
			double d3 = MathHelper.sqrt_double(d * d + d2 * d2);
			float f2 = (float) (Math.atan2(d2, d) * 180.0 / 3.1415927410125732) - 90.0f;
			float f3 = (float) (-(Math.atan2(d1, d3) * 180.0 / 3.1415927410125732));
			this.xHeadRot = -this.updateRotation(this.xHeadRot, f3, f1);
			this.yHeadRot = this.updateRotation(this.yHeadRot, f2, f);
		} else {
			super.faceEntity(entity, f, f1);
		}
	}

	private float updateRotation(float f, float f1, float f2) {
		float f3;
		for (f3 = f1 - f; f3 < -180.0f; f3 += 360.0f) {
		}
		while (f3 >= 180.0f) {
			f3 -= 360.0f;
		}
		if (f3 > f2) {
			f3 = f2;
		}
		if (f3 < -f2) {
			f3 = -f2;
		}
		return f + f3;
	}

	@Override
	public float getXHeadRot() {
		return this.xHeadRot;
	}

	@Override
	public float getYHeadRot() {
		return this.yHeadRot;
	}

	@Override
	public float getXHeadRotO() {
		return this.xHeadRotO;
	}

	@Override
	public float getYHeadRotO() {
		return this.yHeadRotO;
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
	public boolean canSwimLava() {
		return false;
	}
}
