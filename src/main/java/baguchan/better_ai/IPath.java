package baguchan.better_ai;

import baguchan.better_ai.path.BetterNode;
import baguchan.better_ai.path.BetterPathFinder;

import java.util.List;

public interface IPath {
	void setCurrentPath(List<BetterNode> node);

	List<BetterNode> getCurrentPath();

	void setPathFinder(BetterPathFinder path);

	BetterPathFinder getPathFinder();

	boolean canSwimLava();
}
