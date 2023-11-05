package baguchan.better_ai;

import net.minecraft.core.world.pathfinder.Node;
import net.minecraft.core.world.pathfinder.PathFinder;

import java.util.List;

public interface IPath {
	void setCurrentPath(List<Node> node);

	List<Node> getCurrentPath();

	void setPathFinder(PathFinder path);

	PathFinder getPathFinder();

	boolean canSwimLava();
}
