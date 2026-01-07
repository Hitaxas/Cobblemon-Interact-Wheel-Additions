package net.starliteheart.cobblemon_iwa.neoforge;

import me.shedaniel.autoconfig.AutoConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.starliteheart.cobblemon_iwa.common.CobblemonIWA;
import net.starliteheart.cobblemon_iwa.common.config.IWAConfig;

@Mod(value = CobblemonIWA.MOD_ID, dist = Dist.CLIENT)
public final class CobblemonIWANeoForge {
    public CobblemonIWANeoForge() {
        // Run our common setup.
        CobblemonIWA.INSTANCE.initialize();

        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (container, parent) ->
                AutoConfig.getConfigScreen(IWAConfig.class, parent).get()
        );
    }
}
