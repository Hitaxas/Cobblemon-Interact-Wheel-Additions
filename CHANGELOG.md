## Changelog

***

### v1.0.0 (Released ???)

#### Initial release!

- Added two new buttons to the Interact Wheel. One exits the menu, as an alternative to using Esc. The other opens a
  secondary Reorder Options menu.
- Added Reorder Menu, to allow for the reordering of Interact Wheel options.
    - Options can be ordered by dragging and dropping their tiles around the grid. Options are sorted by orientation and
      page.
    - Options will autofill toward the lowest numbered page for their orientation, in alignment with the base logic for
      the Interact Wheel and its options.
    - Clicking the exit button will return the user to the Interact Wheel GUI, while pressing Esc will close the menus
      altogether. Either option will save the options layout.
    - This menu manages the options of whichever menu it was opened from - the Pokemon or the Player Interact Wheel -
      and it will remember which one the player was using when returning to the Interact Wheel via the exit button.
- Added a config backend to support the above features. Options will be saved under a `cobblemon_iwa.toml` file in the
  client config folder. This config can be managed in-client under Mods in NeoForge, or using ModMenu in Fabric.
    - [Dev] Options in the config are stored as pairs of ResourceLocation "namespace:path" strings and orientations.
      Each ResourceLocation string must reference the file used as the primary "iconResource" for its respective
      InteractWheelOption to be correctly registered.
    - [Dev] Including an existing config file with the client will load that option layout upon loading in. This can be
      useful for modpack creators, who may wish to present players with a suggested default layout for interaction
      options.
- Added an "autofillUnlisted" option. Options not listed under the saved config will be automatically reoriented to fill
  empty slots in the pages. These slots will be filled in clockwise order, starting from NORTH. Setting this option to
  false will instead preserve the original orientation of any unlisted options.