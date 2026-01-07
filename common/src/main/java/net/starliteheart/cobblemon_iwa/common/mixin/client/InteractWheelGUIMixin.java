package net.starliteheart.cobblemon_iwa.common.mixin.client;

import com.cobblemon.mod.common.client.gui.interact.wheel.InteractWheelGUI;
import com.cobblemon.mod.common.client.gui.interact.wheel.InteractWheelOption;
import com.cobblemon.mod.common.client.gui.interact.wheel.Orientation;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.sugar.Local;
import kotlin.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.starliteheart.cobblemon_iwa.common.util.IWAUtilsKt;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InteractWheelGUI.class)
public abstract class InteractWheelGUIMixin extends Screen {
    protected InteractWheelGUIMixin(Component component) {
        super(component);
    }

    @Final
    @Shadow(remap = false)
    private Pair<Integer, Integer> getBasePosition() {
        return new Pair<>(0, 0);
    }

    @Final
    @Shadow(remap = false)
    private Multimap<Orientation, InteractWheelOption> options;

    @Inject(method = "init", at = @At("TAIL"))
    private void addReorderButtons(CallbackInfo ci) {
        Pair<Integer, Integer> pos = getBasePosition();
        String interactKey = ((TranslatableContents) title.getContents()).getKey();

        addRenderableWidget(IWAUtilsKt.createReorderButton(pos, options, interactKey));
        addRenderableWidget(IWAUtilsKt.createExitButton(pos));
    }

    @Inject(
            method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"
    )
    )
    private void renderBaseAddon(CallbackInfo ci, @Local(argsOnly = true) GuiGraphics context) {
        IWAUtilsKt.renderBaseAddon(context, getBasePosition());
    }
}