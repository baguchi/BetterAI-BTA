package baguchan.better_ai.api.path;

import baguchan.better_ai.path.BetterNode;
import baguchan.better_ai.path.BetterPathFinder;
import baguchan.better_ai.util.BlockPath;
import net.minecraft.core.entity.EntityLiving;
import org.spongepowered.include.com.google.common.collect.Lists;

import java.util.List;

public interface IPathGetter {
	default void setCurrentPath(EntityLiving entityLiving, List<BetterNode> node) {
		if (entityLiving instanceof IPath) {
			((IPath) entityLiving).setCurrentPath(node);
		}
	}


	default List<BetterNode> getCurrentPath(EntityLiving entityLiving) {
		if (entityLiving instanceof IPath) {
			return ((IPath) entityLiving).getCurrentPath();
		}
		return Lists.newArrayList();
	}


	default void setPathFinder(EntityLiving entityLiving, BetterPathFinder path) {
		if (entityLiving instanceof IPath) {
			((IPath) entityLiving).setPathFinder(path);
		}
	}

	default BetterPathFinder getPathFinder(EntityLiving entityLiving) {
		if (entityLiving instanceof IPath) {
			return ((IPath) entityLiving).getPathFinder();
		}
		return null;
	}


	default boolean canMoveIt(BlockPath blockPath) {
		return blockPath != BlockPath.LAVA && blockPath != BlockPath.DANGER && blockPath != BlockPath.FIRE;
	}

	default float getPathfindingMalus(EntityLiving entityLiving, BlockPath p_21440_) {
		if (entityLiving instanceof IPath) {
			return ((IPath) entityLiving).getPathfindingMalus(p_21440_);
		}
		return 0;
	}


	default void setPathfindingMalus(EntityLiving entityLiving, BlockPath p_21442_, float p_21443_) {
		if (entityLiving instanceof IPath) {
			((IPath) entityLiving).setPathfindingMalus(p_21442_, p_21443_);
		}
	}


	default boolean canMoveDirect() {
		return false;
	}

}
