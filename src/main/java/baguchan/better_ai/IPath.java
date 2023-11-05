package baguchan.better_ai;

import net.minecraft.core.world.pathfinder.Node;

import java.util.List;

public interface IPath {
	void setCurrentPath(List<Node> node);

	List<Node> getCurrentPath();
}
