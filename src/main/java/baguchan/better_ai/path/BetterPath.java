package baguchan.better_ai.path;

import net.minecraft.core.world.pathfinder.Path;

public class BetterPath extends Path {
	private final BetterNode[] nodes;

	public BetterPath(BetterNode[] apathpoint) {
		super(apathpoint);
		this.nodes = apathpoint;
	}

	public BetterNode[] getNodes() {
		return nodes;
	}

}
