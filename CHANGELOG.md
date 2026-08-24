# AkitosDrugs Changelog

## 21.2.2

* Modernized all player-facing messages to Adventure `Component`s across every command, effect, and menu file (`MainCommand`, `AddictionCommand`, `AddictionManager`, all 6 effect classes, `EffectEngine`, `DrugEffectListener`, `DrugMenu`, `DrugMenuListener`).
* The drug menu's hidden per-item identity tag (used internally to detect which drug an item represents) now round-trips correctly through the new `Component`-based item lore.
* `EffectEngine`'s config-sourced flavor-text messages (`messages-low`/`messages-high`/`messages-cleanse`) are decoded from their `&`-coded `settings.yml` strings at the point they're sent, so admin-authored color codes still render correctly.
* Replaced the deprecated `PotionEffectType.getByName(String)` lookup in `SettingsManager` with the stable `Registry.EFFECT.get(NamespacedKey)`.
* Bumped `paper-api` to `1.21.11-R0.1-SNAPSHOT`.
* Switched from separate `maven.compiler.source`/`target` properties to `maven.compiler.release`.

## Earlier versions

Not yet documented. This changelog starts at `21.2.2`, the first version with a full write-up.
