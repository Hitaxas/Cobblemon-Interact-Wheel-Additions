package net.starliteheart.cobblemon_iwa.common.mixin.client;

import com.cobblemon.mod.common.client.gui.interact.wheel.InteractWheelGuiFactoryKt;
import com.cobblemon.mod.common.client.gui.interact.wheel.InteractWheelOption;
import com.cobblemon.mod.common.client.gui.interact.wheel.Orientation;
import com.google.common.collect.Multimap;
import net.starliteheart.cobblemon_iwa.common.util.IWAUtilsKt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = InteractWheelGuiFactoryKt.class)
public abstract class InteractWheelGUIFactoryMixin {
    /**
     * This mixin takes the options passed to createPokemonInteractGui and reorders them based on the ordering in the config.
     *
     * @return the reordered options
     */
    @ModifyArg(
            method = "createPokemonInteractGui",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/cobblemon/mod/common/client/gui/interact/wheel/InteractWheelGUI;<init>(Lcom/google/common/collect/Multimap;Lnet/minecraft/network/chat/Component;)V"
            )
    )
    private static Multimap<Orientation, InteractWheelOption> reorderPokemonOptions(Multimap<Orientation, InteractWheelOption> options) {
        return IWAUtilsKt.reorderOptions(options, IWAUtilsKt.getConfig().getPokemonOrderedOptions());
    }

    /**
     * This mixin takes the options passed to createPlayerInteractGui and reorders them based on the ordering in the config.
     *
     * @return the reordered options
     */
    @ModifyArg(
            method = "createPlayerInteractGui",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/cobblemon/mod/common/client/gui/interact/wheel/InteractWheelGUI;<init>(Lcom/google/common/collect/Multimap;Lnet/minecraft/network/chat/Component;)V"
            )
    )
    private static Multimap<Orientation, InteractWheelOption> reorderPlayerOptions(Multimap<Orientation, InteractWheelOption> options) {
        return IWAUtilsKt.reorderOptions(options, IWAUtilsKt.getConfig().getPlayerOrderedOptions());
    }
}