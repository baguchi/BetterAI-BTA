package baguchan.better_ai.util;

import net.minecraft.core.world.pathfinder.Node;
import net.minecraft.core.world.pathfinder.Path;

public class BetterPath extends Path {
	private final Node[] nodes;

	public BetterPath(Node[] apathpoint) {
		super(apathpoint);
		this.nodes = apathpoint;
	}

	public Node[] getNodes() {
		return nodes;
	}
}
