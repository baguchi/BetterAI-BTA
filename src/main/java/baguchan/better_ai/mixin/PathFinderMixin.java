package baguchan.better_ai.mixin;

import baguchan.better_ai.IPath;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.world.pathfinder.BinaryHeap;
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

@Mixin(value = PathFinder.class, remap = false)
public class PathFinderMixin {
	@Shadow
	@Final
	private BinaryHeap openSet;

	@Inject(method = "findPath(Lnet/minecraft/core/entity/Entity;DDDF)Lnet/minecraft/core/world/pathfinder/Path;", at = @At("RETURN"))
	private void findPath(Entity entity, double xt, double yt, double zt, float distance, CallbackInfoReturnable<Path> callbackInfoReturnable) {
		if (entity instanceof IPath && openSet instanceof BinaryHeapMixin) {
			List<Node> nodeList = Lists.newArrayList();
			nodeList.addAll(Arrays.asList(((BinaryHeapMixin) openSet).getheap()));
			((IPath) entity).setCurrentPath(nodeList);
		}
	}

	@Inject(method = "getNode(Lnet/minecraft/core/entity/Entity;IIILnet/minecraft/core/world/pathfinder/Node;I)Lnet/minecraft/core/world/pathfinder/Node;", at = @At("HEAD"), cancellable = true)
	private void getNode(Entity entity, int x, int y, int z, Node pathpoint, int l, CallbackInfoReturnable<Node> cir) {
		if (entity instanceof IPath) {
			if (!((IPath) entity).canSwimLava()) {
				if (this.isFree(entity, x, y - 1, z, pathpoint) == -2) {
					cir.setReturnValue(null);
				}
			}
		}
	}

	@Shadow
	private int isFree(Entity entity, int x, int i, int z, Node pathpoint) {
		return 0;
	}
}
