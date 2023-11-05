package baguchan.better_ai.mixin;

import baguchan.better_ai.IPath;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.world.pathfinder.Node;
import net.minecraft.core.world.pathfinder.Path;
import net.minecraft.core.world.pathfinder.PathFinder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.include.com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

@Mixin(PathFinder.class)
public class PathFinderMixin {
	@Shadow(remap = false)
	@Final
	private Node[] neighbors;

	@Inject(method = "findPath(Lnet/minecraft/core/entity/Entity;DDDF)Lnet/minecraft/core/world/pathfinder/Path;", at = @At("HEAD"), remap = false)
	private void findPath(Entity entity, double xt, double yt, double zt, float distance, CallbackInfoReturnable<Path> callbackInfoReturnable) {
		if (entity instanceof IPath) {
			List<Node> nodeList = Lists.newArrayList();
			nodeList.addAll(Arrays.asList(neighbors));
			((IPath) entity).setCurrentPath(nodeList);
		}
	}
}
