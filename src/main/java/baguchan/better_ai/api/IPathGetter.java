package baguchan.better_ai.api;

import baguchan.better_ai.path.BetterNode;
import baguchan.better_ai.path.BetterPath;
import baguchan.better_ai.path.BetterPathFinder;
import baguchan.better_ai.util.BlockPath;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.world.pathfinder.Path;
import org.spongepowered.include.com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.List;

public interface IPathGetter {
	default void setCurrentPath(Mob entityLiving, List<BetterNode> node) {
		if (entityLiving instanceof IPath) {
			((IPath) entityLiving).setCurrentPath(node);
		}
	}


	default List<BetterNode> getCurrentPath(Mob entityLiving) {
		if (entityLiving instanceof IPath) {
			return ((IPath) entityLiving).getCurrentPath();
		}
		return Lists.newArrayList();
	}


	default void setPathFinder(Mob entityLiving, BetterPathFinder path) {
		if (entityLiving instanceof IPath) {
			((IPath) entityLiving).setPathFinder(path);
		}
	}

	default BetterPathFinder getPathFinder(Mob entityLiving) {
		if (entityLiving instanceof IPath) {
			return ((IPath) entityLiving).getPathFinder();
		}
		return null;
	}

	default float getPathfindingMalus(Mob entityLiving, BlockPath p_21440_) {
		if (entityLiving instanceof IPath) {
			return ((IPath) entityLiving).getPathfindingMalus(p_21440_);
		}
		return 0;
	}


	default void setPathfindingMalus(Mob entityLiving, BlockPath p_21442_, float p_21443_) {
		if (entityLiving instanceof IPath) {
			((IPath) entityLiving).setPathfindingMalus(p_21442_, p_21443_);
		}
	}

	default void setTargetPath(Mob entityLiving, BetterPath betterPath) {
		if (entityLiving instanceof IPath) {
			((IPath) entityLiving).setTargetPath(betterPath);
		}
	}

	@Nullable
	default Path getTargetPath(Mob entityLiving) {
		if (entityLiving instanceof IPath) {
			return ((IPath) entityLiving).getTargetPath();
		}
		return null;
	}


	default boolean canMoveDirect() {
		return false;
	}

	default boolean canHideFromSkyLight() {
		return false;
	}
}
