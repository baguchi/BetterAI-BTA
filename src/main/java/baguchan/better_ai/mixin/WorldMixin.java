package baguchan.better_ai.mixin;

import baguchan.better_ai.api.IPath;
import baguchan.better_ai.api.IPathGetter;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pathfinder.Path;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = World.class, remap = false)
public class WorldMixin {
	@Inject(method = "getPathToEntity", at = @At("HEAD"), cancellable = true)
	public void getPathToEntity(Entity entity, Entity entityToTravelTo, float distance, CallbackInfoReturnable<Path> cir) {
		if (entity instanceof IPath && entity instanceof IPathGetter) {
			if (((IPath) entity).getPathFinder() != null) {
				cir.setReturnValue(((IPath) entity).getPathFinder().findPath(entity, entityToTravelTo, distance));
			}
		}
	}

	@Inject(method = "getEntityPathToXYZ", at = @At("HEAD"), cancellable = true)
	public void getEntityPathToXYZ(Entity entity, int i, int j, int k, float f, CallbackInfoReturnable<Path> cir) {
		if (entity instanceof IPath && entity instanceof IPathGetter) {
			if (((IPath) entity).getPathFinder() != null) {
				cir.setReturnValue(((IPath) entity).getPathFinder().findPath(entity, i, j, k, f));
			}
		}
	}
}
