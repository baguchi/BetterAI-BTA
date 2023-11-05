package baguchan.better_ai.util;

import net.minecraft.core.world.pathfinder.Node;

public class Target extends Node {
	public Node bestNode;
	public float bestHeuristic;

	public Target(int x, int y, int z) {
		super(x, y, z);
	}

	public void updateBest(float p_77504_, Node p_77505_) {
		if (p_77504_ < this.bestHeuristic) {
			this.bestHeuristic = p_77504_;
			this.bestNode = p_77505_;
		}
	}

}
