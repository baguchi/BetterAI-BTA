package baguchan.better_ai;

import baguchan.better_ai.util.BetterPathFinder;
import net.minecraft.core.world.pathfinder.Node;

import java.util.List;

public interface IPath {
	void setCurrentPath(List<Node> node);

	List<Node> getCurrentPath();

	void setPathFinder(BetterPathFinder path);

	BetterPathFinder getPathFinder();

	boolean canSwimLava();
}
