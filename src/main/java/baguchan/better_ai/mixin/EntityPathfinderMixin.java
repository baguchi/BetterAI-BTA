package baguchan.better_ai.mixin;

import baguchan.better_ai.IPath;
import baguchan.better_ai.util.BetterPathFinder;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.EntityPathfinder;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pathfinder.Node;
import net.minecraft.core.world.pathfinder.Path;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.include.com.google.common.collect.Lists;

import java.util.List;

@Mixin(value = EntityPathfinder.class, remap = false)
public abstract class EntityPathfinderMixin extends EntityLiving implements IPath {
	@Shadow
	protected Path pathToEntity;
	@Shadow
	protected Entity entityToAttack;
	public List<Node> nodeList = Lists.newArrayList();
	public BetterPathFinder pathFinder;

	public int stuckTick;

	public EntityPathfinderMixin(World world) {
		super(world);
	}

	@Override
	protected void init() {
		super.init();
		pathFinder = new BetterPathFinder(world);
	}

	@Override
	public void setCurrentPath(List<Node> node) {
		nodeList = node;
	}

	@Override
	public List<Node> getCurrentPath() {
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
