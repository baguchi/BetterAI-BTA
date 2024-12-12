package baguchan.better_ai.mixin;

import baguchan.better_ai.api.IPathGetter;
import net.minecraft.core.entity.monster.MobMonster;
import net.minecraft.core.entity.monster.MobSkeleton;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MobSkeleton.class)
public abstract class EntitySkeletonMixin extends MobMonster implements IPathGetter {

	public EntitySkeletonMixin(World world) {
		super(world);
	}

	@Override
	public boolean canHideFromSkyLight() {
		return !this.world.canBlockSeeTheSky(MathHelper.floor(this.x), MathHelper.floor(this.y), MathHelper.floor(this.z));
	}

	@Override
	protected void roamRandomPath() {
		if (this.world != null) {
			if (this.world.canBlockSeeTheSky(MathHelper.floor(this.x), MathHelper.floor(this.y), MathHelper.floor(this.z)) && this.world.isDaytime()) {
				boolean canMoveToPoint = false;
				int x = -1;
				int y = -1;
				int z = -1;
				float bestPathWeight = -99999.0F;

				for (int l = 0; l < 10; ++l) {
					int x1 = MathHelper.floor(this.x + (double) this.random.nextInt(13) - (double) 6.0F);
					int y1 = MathHelper.floor(this.y + (double) this.random.nextInt(7) - (double) 3.0F);
					int z1 = MathHelper.floor(this.z + (double) this.random.nextInt(13) - (double) 6.0F);
					float currentPathWeight = this.getBlockPathWeight(x1, y1, z1);
					if (currentPathWeight > bestPathWeight && !this.world.canBlockSeeTheSky(MathHelper.floor(x1), MathHelper.floor(y1), MathHelper.floor(z1))) {
						bestPathWeight = currentPathWeight;
						x = x1;
						y = y1;
						z = z1;
						canMoveToPoint = true;
					}
				}

				if (canMoveToPoint) {
					this.pathToEntity = this.world.getEntityPathToXYZ(this, x, y, z, 16.0F);
				}
			} else {
				super.roamRandomPath();
			}


		}
	}
}
