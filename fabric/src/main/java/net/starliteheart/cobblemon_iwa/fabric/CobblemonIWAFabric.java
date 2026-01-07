package net.starliteheart.cobblemon_iwa.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.starliteheart.cobblemon_iwa.common.CobblemonIWA;

public final class CobblemonIWAFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        CobblemonIWA.INSTANCE.initialize();
    }
}
