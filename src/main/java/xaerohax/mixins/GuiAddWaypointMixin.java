package xaerohax.mixins;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xaero.common.AXaeroMinimap;
import xaero.common.MinimapLogs;
import xaero.common.gui.GuiAddWaypoint;
import xaero.common.gui.GuiWaypointSets;
import xaero.common.gui.GuiWaypointWorlds;
import xaero.common.gui.ScreenBase;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointSet;
import xaero.common.minimap.waypoints.WaypointWorld;
import xaero.common.minimap.waypoints.WaypointsManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

@Debug(export = true)
@Mixin(value = GuiAddWaypoint.class, remap = false)
public abstract class GuiAddWaypointMixin extends ScreenBase {

	@Shadow
	private ArrayList<Waypoint> waypointsEdited;

	@Shadow
	private GuiWaypointWorlds worlds;
	@Shadow
	private WaypointsManager waypointsManager;

	@Shadow
	private GuiWaypointSets sets;

	@Unique
	private final Pattern isInNetherRegex = Pattern.compile("/dim%-1");
	@Unique
	private final Pattern isInOverworldRegex = Pattern.compile("/dim%0");

	protected GuiAddWaypointMixin(AXaeroMinimap modMain, Screen parent, Screen escape, Text titleIn) {
		super(modMain, parent, escape, titleIn);
	}

	@Inject(
		method = "lambda$init$3(Lnet/minecraft/client/gui/widget/ButtonWidget;)V",
		at = @At(
			value = "INVOKE",
			target = "xaero/common/settings/ModSettings.saveWaypoints(Lxaero/common/minimap/waypoints/WaypointWorld;)V",
			ordinal = 0,
			remap = false
		),
		locals = LocalCapture.CAPTURE_FAILHARD,
		remap = true
	)
	private void onSave(
		ButtonWidget b,
		CallbackInfo ci,
		boolean creatingAWaypoint,
		double dimDiv,
		int initialEditedWaypointsSize,
		WaypointWorld sourceWorld,
		WaypointSet sourceSet,
		String[] destinationWorldKeys,
		String destinationSetKey,
		WaypointWorld destinationWorld,
		WaypointSet destinationSet
	) {
		Waypoint firstWayPoint = this.waypointsEdited.get(this.waypointsEdited.size() - 1);
		String currentWorldKey = this.worlds.getCurrentKeys()[0];

		boolean isNether = isInNetherRegex.matcher(currentWorldKey).find();
		double divider = isNether ? 1.0D / 8.0D : isInOverworldRegex.matcher(currentWorldKey).find() ? 8.0D : 1.0D;
		if (divider == 1.0D) {
			// exit if its the end
			return;
		}
		Waypoint wp = new Waypoint(firstWayPoint.getX(divider),
			firstWayPoint.getY(),
			firstWayPoint.getZ(divider),
			firstWayPoint.getName(),
			firstWayPoint.getSymbol(),
			firstWayPoint.getColor(),
			firstWayPoint.getWaypointType(),
			firstWayPoint.isTemporary(),
			firstWayPoint.isYIncluded());

		wp.setYaw(firstWayPoint.getYaw());
		wp.setVisibilityType(firstWayPoint.getVisibilityType());
		wp.setDisabled(firstWayPoint.isDisabled());

		WaypointWorld world = waypointsManager.getWorld(worlds.getCurrentKeys()[0].replaceFirst("dim%(-1|0)", "dim%" + (isNether ? "0" : "-1")), worlds.getCurrentKeys()[1]);

		world.getSets().get(this.sets.getCurrentSetKey()).getList().add(wp);

		try {
			this.modMain.getSettings().saveWaypoints(world);
		} catch (IOException e) {
			MinimapLogs.LOGGER.error("suppressed exception", e);
		}
	}
}
