package baguchan.better_ai.path;

import com.google.common.collect.Lists;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.pathfinder.Node;
import net.minecraft.core.world.pathfinder.Path;

import javax.annotation.Nullable;
import java.util.List;

public class BetterPath extends Path {
	private BetterNode[] nodes;
	private int index;

	public BetterPath(BetterNode[] apathpoint) {
		super(apathpoint);
		this.nodes = apathpoint;
	}

	public BetterNode[] getNodes() {
		return nodes;
	}

	public void truncateNodes(int p_77389_) {
		if (this.nodes.length > p_77389_) {
			List<BetterNode> betterNodeList = Lists.newArrayList(this.nodes);
			betterNodeList.subList(p_77389_, this.nodes.length).clear();
			this.nodes = betterNodeList.stream().toArray(BetterNode[]::new);
			if (this.index >= this.nodes.length && this.nodes.length > 0) {
				this.index = p_77389_ - 1;
			}
		}
	}

	public void next() {
		++this.index;
	}

	public boolean isDone() {
		return this.index >= this.nodes.length;
	}

	public Node last() {
		return this.length > 0 ? this.nodes[this.length - 1] : null;
	}

	public Vec3 getPos(Entity entity) {
		if (this.isDone()) {
			return Vec3.getPermanentVec3(entity.x - 0.5, entity.y, entity.z - 0.5);
		}

		double x = (double) this.nodes[this.index].x + (double) ((int) (entity.bbWidth + 2.0F)) * 0.5;
		double y = (double) this.nodes[this.index].y;
		double z = (double) this.nodes[this.index].z + (double) ((int) (entity.bbWidth + 2.0F)) * 0.5;
		return Vec3.getPermanentVec3(x - 0.5, y, z - 0.5);
	}

	public boolean sameAs(@Nullable BetterPath p_77386_) {
		if (p_77386_ == null) {
			return false;
		} else if (p_77386_.nodes.length != this.nodes.length) {
			return false;
		} else {
			for (int i = 0; i < this.nodes.length; i++) {
				BetterNode node = this.nodes[i];
				BetterNode node1 = this.nodes[i];
				if (node.x != node1.x || node.y != node1.y || node.z != node1.z) {
					return false;
				}
			}

			return true;
		}
	}
}
