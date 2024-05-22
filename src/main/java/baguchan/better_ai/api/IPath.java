package baguchan.better_ai.api;

import baguchan.better_ai.path.BetterNode;
import baguchan.better_ai.path.BetterPath;
import baguchan.better_ai.path.BetterPathFinder;
import baguchan.better_ai.util.BlockPath;
import net.minecraft.core.world.pathfinder.Path;

import javax.annotation.Nullable;
import java.util.List;

//DON'T USE THIS WHEN YOU WANT TO ADD MOB WITH GOOD PATHFINDER
public interface IPath {
	@Deprecated
	void setCurrentPath(List<BetterNode> node);

	@Deprecated
	List<BetterNode> getCurrentPath();

	@Deprecated
	void setPathFinder(BetterPathFinder path);

	@Deprecated
	BetterPathFinder getPathFinder();

	@Deprecated
	float getPathfindingMalus(BlockPath p_21440_);

	@Deprecated
	void setPathfindingMalus(BlockPath p_21442_, float p_21443_);

	@Deprecated
	void setTargetPath(BetterPath betterPath);

	@Deprecated
	@Nullable
	Path getTargetPath();
}
