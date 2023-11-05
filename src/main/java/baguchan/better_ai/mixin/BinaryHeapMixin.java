package baguchan.better_ai.mixin;

import net.minecraft.core.world.pathfinder.BinaryHeap;
import net.minecraft.core.world.pathfinder.Node;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BinaryHeap.class, remap = false)
public interface BinaryHeapMixin {
	@Accessor("heap")
	public Node[] getheap();
}
