package xaerohax.mixins;

import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.State;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.map.MapWriter;
import xaero.map.region.MapBlock;

@Mixin(value = MapWriter.class, remap = false)
public class MapWriterMixin {
	@ModifyConstant(
		method = "writeChunk",
		constant = @Constant(intValue = 1),
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/world/chunk/WorldChunk;sampleHeightmap(Lnet/minecraft/world/Heightmap$Type;II)I",
				remap = true
			),
			to = @At(
				value = "INVOKE",
				target = "Lxaero/map/region/MapTile;isLoaded()Z",
				remap = false
			)
		),
		remap = false
	)
	private int modifyHeight(int height) {
		return 1;
	}

	@Inject(
		method = "loadPixelHelp",
		at = @At("HEAD"),
		cancellable = true,
		remap = false
	)
	public void stopOverlay(
		MapBlock pixel,
		MapBlock currentPixel,
		World world,
		BlockState state,
		byte light,
		WorldChunk bchunk,
		int insideX,
		int insideZ,
		int h,
		boolean canReuseBiomeColours,
		boolean cave,
		FluidState fluidFluidState,
		Registry<Biome> biomeRegistry,
		int transparentSkipY,
		boolean shouldExtendTillTheBottom,
		boolean flowers,
		CallbackInfoReturnable<Boolean> cir
	) {
		if (state.isOf(Blocks.GLASS)) {
			cir.setReturnValue(false);
		}
	}
}
