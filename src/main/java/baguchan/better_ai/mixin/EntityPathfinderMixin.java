package baguchan.better_ai.mixin;

import baguchan.better_ai.IPath;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.EntityPathfinder;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pathfinder.Node;
import net.minecraft.core.world.pathfinder.Path;
import net.minecraft.core.world.pathfinder.PathFinder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.include.com.google.common.collect.Lists;

import java.util.List;

@Mixin(value = EntityPathfinder.class, remap = false)
public abstract class EntityPathfinderMixin extends EntityLiving implements IPath {
	public List<Node> nodeList = Lists.newArrayList();
	public PathFinder pathFinder;

	public int refreshTime;

	@Shadow
	protected Entity entityToAttack;
	@Shadow
	protected Path pathToEntity;

	public EntityPathfinderMixin(World world) {
		super(world);
	}

	@Override
	protected void init() {
		super.init();
		pathFinder = new PathFinder(world);
	}

	@Inject(method = "updatePlayerActionState", at = @At("TAIL"))
	protected void updatePlayerActionState(CallbackInfo callbackInfo) {
		float sightRadius = 16.0F;
		if (this.entityToAttack != null) {
			if (this.refreshTime >= 3) {
				this.pathToEntity = this.world.getPathToEntity(this, this.entityToAttack, sightRadius);
				this.refreshTime = 0;
			} else if (this.horizontalCollision) {
				++this.refreshTime;
			}
		}
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
	public void setPathFinder(PathFinder path) {
		this.pathFinder = path;
	}

	@Override
	public PathFinder getPathFinder() {
		return this.pathFinder;
	}

	@Override
	public boolean canSwimLava() {
		return false;
	}
}
