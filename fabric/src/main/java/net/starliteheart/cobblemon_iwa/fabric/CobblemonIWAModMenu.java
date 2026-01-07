package net.starliteheart.cobblemon_iwa.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import net.starliteheart.cobblemon_iwa.common.config.IWAConfig;

public class CobblemonIWAModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> AutoConfig.getConfigScreen(IWAConfig.class, screen).get();
    }
}
