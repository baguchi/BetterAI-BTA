package baguchan.better_ai.mixin;

import baguchan.better_ai.IPath;
import baguchan.better_ai.path.BetterNode;
import baguchan.better_ai.path.BetterPathFinder;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.EntityPathfinder;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.include.com.google.common.collect.Lists;

import java.util.List;

@Mixin(value = EntityPathfinder.class, remap = false)
public abstract class EntityPathfinderMixin extends EntityLiving implements IPath {
	@Shadow
	protected Entity entityToAttack;
	public List<BetterNode> nodeList = Lists.newArrayList();
	public BetterPathFinder pathFinder;

	public EntityPathfinderMixin(World world) {
		super(world);
	}

	@Override
	protected void init() {
		super.init();
		pathFinder = new BetterPathFinder(world);
	}

	@Override
	public void setCurrentPath(List<BetterNode> node) {
		nodeList = node;
	}

	@Override
	public List<BetterNode> getCurrentPath() {
		return nodeList;
	}

	@Override
	public void setPathFinder(BetterPathFinder path) {
		this.pathFinder = path;
	}

	@Override
	public BetterPathFinder getPathFinder() {
		return this.pathFinder;
	}

	@Override
	public boolean canSwimLava() {
		return false;
	}
}
