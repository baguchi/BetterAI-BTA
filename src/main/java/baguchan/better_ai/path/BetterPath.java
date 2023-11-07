package baguchan.better_ai.path;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.util.phys.Vec3d;
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


	public Vec3d getPos(Entity entity) {
		return super.getPos(entity).addVector(-0.5F, 0, -0.5F);
	}
}
