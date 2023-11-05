package baguchan.better_ai.mixin;

import baguchan.better_ai.IPath;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.world.pathfinder.Node;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.include.com.google.common.collect.Lists;

import java.util.List;

@Mixin(Entity.class)
public class EntityMixin implements IPath {
	public List<Node> nodeList = Lists.newArrayList();

	@Override
	public void setCurrentPath(List<Node> node) {
		nodeList = node;
	}

	@Override
	public List<Node> getCurrentPath() {
		return nodeList;
	}
}
