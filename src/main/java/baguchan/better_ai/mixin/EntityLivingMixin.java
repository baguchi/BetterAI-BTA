package baguchan.better_ai.mixin;

import baguchan.better_ai.IPath;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityLiving.class, remap = false)
public abstract class EntityLivingMixin extends Entity {

	@Shadow
	protected float lookYaw;

	@Shadow
	protected float lookPitch;

	public EntityLivingMixin(World world) {
		super(world);
	}

	@Inject(method = "faceEntity", at = @At("HEAD"), cancellable = true)
	public void faceEntity(Entity entity, float f, float f1, CallbackInfo ci) {
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
			this.lookPitch = -this.updateRotation(this.lookPitch, f3, f1);
			this.lookYaw = this.updateRotation(this.lookYaw, f2, f);
			ci.cancel();
		}
	}

	@Shadow
	private float updateRotation(float rot, float f2, float f) {
		return 0;
	}
}
