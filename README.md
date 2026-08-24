# AkitosDrugs

A comprehensive drug mechanics plugin with addiction, withdrawal, and unique per-drug effects (dissociation, trip, aura, teleport, inventory shuffle, peace). Fully configurable via `settings.yml`, new drugs can be added without touching any code.

## Requirements

* Paper 1.21.11+
* Java 21+
* AkitosCore v21.2.0+

## Installation

1. Install AkitosCore first.
2. Drop `AkitosDrugs-21.2.2.jar` into your `plugins/` folder.
3. Restart the server.
4. Configure `AkitosPlugins/AkitosDrugs/settings.yml`.

## Drugs

Values below are pulled directly from the default `settings.yml`, `Addiction/use` is the score added per use (higher means faster addiction buildup), `Cooldown` is the minimum time between uses of that specific drug.

| Drug | Category | Addiction/use | Cooldown | Notable effect |
| --- | --- | --- | --- | --- |
| Heroin | Illegal | 6.0 | 20s | Teleport effect (camera rotation, blindness) |
| Meth | Illegal | 5.5 | 15s | |
| Cocaine | Illegal | 4.5 | 5s | |
| Ketamine | Illegal | 2.5 | 15s | Dissociation |
| Ecstasy | Illegal | 1.5 | 10s | Peace, aura |
| Weed | Gray-zone | 2.0 | 11s | Peace |
| Antidepressants | Gray-zone | 2.0 | 5s | |
| LSD | Gray-zone | 0.5 | 20s | Trip, inventory shuffle |
| Shrooms | Gray-zone | 0.5 | 20s | Trip |
| Salvia | Gray-zone | 0.5 | 30s | Teleport effect |
| Cigarette | Legal | 4.0 | 5s | |
| Melatonin | Legal | 1.5 | 5s | |
| Painkillers | Legal | 1.0 | 5s | |
| Herbal Cigarette | Legal | 0.0 | 5s | No effect, cosmetic only |
| Vitamins | Legal | 0.0 | 5s | No effect, cosmetic only |

Each drug also defines its own particle effect, a set of positive and negative potion effects applied on use, and three pools of flavor-text messages (`messages-low`, `messages-high`, `messages-cleanse`) sent to the player as their addiction score crosses each drug's own `message-threshold`.

Withdrawal and decay behavior is global, set once under `addiction:` in `settings.yml` (`decay-rate`, `decay-interval-seconds`, `withdrawal-interval-seconds`, plus scaling factors for how addiction severity affects effect strength), not per drug.

## Commands

| Command | Description | Permission |
| --- | --- | --- |
| `/addiction` | View your own current addiction levels | none |
| `/ad` | Show the help menu | `akitosdrugs.admin` |
| `/ad reload` | Reload `settings.yml` | `akitosdrugs.admin` + `akitosdrugs.admin.reload` |
| `/ad drugs` | Open the drug admin menu (browse/give any drug) | `akitosdrugs.admin` + `akitosdrugs.admin.drugs` |
| `/ad addiction <player>` | View another player's addiction levels | `akitosdrugs.admin` + `akitosdrugs.admin.addiction` |
| `/ad addiction reset <player>` (or `/ad reset <player>`) | Reset a player's addiction to every drug | `akitosdrugs.admin` + `akitosdrugs.admin.addiction` |
| `/ad addiction set <player> <drug> <amount>` (or `/ad set <player> <drug> <amount>`) | Set a player's addiction score for one drug directly | `akitosdrugs.admin` + `akitosdrugs.admin.addiction` |

`akitosdrugs.admin` gates the `/ad` command itself (declared in `plugin.yml`), the four sub-permissions below it give finer control over which admin subcommands a given staff member can use once they have base access. `/addiction` is a separate top-level command with no permission requirement, any player can check their own addiction levels.

## Permissions

| Permission | Description | Default |
| --- | --- | --- |
| `akitosdrugs.admin` | Base access to the `/ad` command | op |
| `akitosdrugs.admin.drugs` | Open the drug admin menu | op |
| `akitosdrugs.admin.addiction` | View and modify player addictions | op |
| `akitosdrugs.admin.reload` | Reload config | op |

## The drug menu

`/ad drugs` opens a 54-slot GUI with three category tabs (Legal, Gray-zone, Illegal) and a cycling sort button with three modes: most recently added, alphabetical, and most addictive first.

## Adding custom drugs

Add a new entry under `drugs:` in `settings.yml`, no recompilation needed:

```yaml
drugs:
  mydrug:
    display-name: "&aMy Drug"
    item: SUGAR
    category: legal
    lore:
      - "&7A custom drug."
      - "&7Not addictive."
    addiction-per-use: 1.0
    use-cooldown-seconds: 30
    message-threshold: 50
    particles:
      type: HAPPY_VILLAGER
      count: 10
      spread: 0.5
      speed: 0.05
    positive-effects:
      - REGENERATION,0,100
    negative-effects: []
    messages-low:
      - "&aFeeling good."
    messages-high:
      - "&cNeed more."
    messages-cleanse:
      - "&aClear headed again."
```

`category` must be one of `legal`, `grayzone`, or `illegal`. Effects are listed as `POTION_EFFECT,amplifier,duration-ticks`. The unique mechanics (`dissociation`, `teleport-effect`, `peace`, `aura`, `trip`, `inv-shuffle`) are all optional per-drug blocks, omit them entirely for a drug that shouldn't have one.

## Part of the Akitos Plugin Network

* [AkitosCore](https://github.com/AkitoSekuna/AkitosCore) (required)
