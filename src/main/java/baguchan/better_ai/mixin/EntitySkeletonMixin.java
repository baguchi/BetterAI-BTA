package baguchan.better_ai.mixin;

import baguchan.better_ai.api.IPathGetter;
import net.minecraft.core.entity.monster.EntityMonster;
import net.minecraft.core.entity.monster.EntitySkeleton;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntitySkeleton.class)
public abstract class EntitySkeletonMixin extends EntityMonster implements IPathGetter {

	public EntitySkeletonMixin(World world) {
		super(world);
	}

	@Override
	public boolean canHideFromSkyLight() {
		return !this.world.canBlockSeeTheSky((int) this.x, (int) this.y, (int) this.z);
	}
}
