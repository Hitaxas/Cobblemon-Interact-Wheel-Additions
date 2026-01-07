package net.starliteheart.cobblemon_iwa.common.config

import com.cobblemon.mod.common.client.gui.interact.wheel.Orientation
import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.annotation.ConfigEntry
import net.minecraft.resources.ResourceLocation
import net.starliteheart.cobblemon_iwa.common.CobblemonIWA
import org.jetbrains.annotations.ApiStatus

@Config(name = CobblemonIWA.MOD_ID)
@ApiStatus.Internal
class IWAConfig : ConfigData {
    @ConfigEntry.Gui.Tooltip(count = 2)
    var pokemonOrderedOptions: List<OrderedOption> = listOf(
        OrderedOption("cobblemon:textures/gui/interact/interact_wheel_icon_held_item.png", Orientation.NORTH),
        OrderedOption("cobblemon:textures/gui/interact/interact_wheel_icon_cosmetic_item.png", Orientation.NORTHEAST),
        OrderedOption("cobblemon:textures/gui/interact/interact_wheel_icon_ride.png", Orientation.WEST),
        OrderedOption("cobblemon:textures/gui/interact/interact_wheel_icon_shoulder.png", Orientation.NORTHWEST)
    )

    @ConfigEntry.Gui.Tooltip(count = 2)
    var playerOrderedOptions: List<OrderedOption> = listOf(
        OrderedOption("cobblemon:textures/gui/interact/interact_wheel_icon_battle.png", Orientation.NORTH),
        OrderedOption("cobblemon:textures/gui/interact/interact_wheel_icon_trade.png", Orientation.NORTHEAST),
    )

    @ConfigEntry.Gui.Tooltip(count = 2)
    var autofillUnlisted: Boolean = true

    override fun validatePostLoad() {
        val filteredPokemonOrderedOptions = pokemonOrderedOptions.filter { (resource) ->
            ResourceLocation.tryParse(resource) != null
        }
        if (filteredPokemonOrderedOptions.size < pokemonOrderedOptions.size) {
            CobblemonIWA.LOGGER.warn("Some resources listed in pokemonOrderedOptions could not be found. Invalid options will be ignored.")
            pokemonOrderedOptions = filteredPokemonOrderedOptions
        }
        val filteredPlayerOrderedOptions = playerOrderedOptions.filter { (resource) ->
            ResourceLocation.tryParse(resource) != null
        }
        if (filteredPlayerOrderedOptions.size < playerOrderedOptions.size) {
            CobblemonIWA.LOGGER.warn("Some resources listed in playerOrderedOptions could not be found. Invalid options will be ignored.")
            playerOrderedOptions = filteredPlayerOrderedOptions
        }
    }

    data class OrderedOption(
        var resource: String = "",
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        var orientation: Orientation = Orientation.NORTH,
    )
}