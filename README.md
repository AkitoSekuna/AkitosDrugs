# AkitosDrugs

Drug mechanics plugin with per-drug addiction, withdrawal, and unique effects. Nearly fully configurable through `settings.yml`; new drugs can be added without touching code.

## Requirements

- Paper 1.21.1+
- Java 21+
- AkitosCore v21.2.0+

## Installation

1. Install AkitosCore first.
2. Drop `AkitosDrugs.jar` into your `plugins/` folder.
3. Restart the server.
4. Configure `plugins/AkitosPlugins/AkitosDrugs/settings.yml`.

## Features

- 12 drugs across 3 categories: Legal, Gray-zone, Illegal
- Per-drug addiction system with decay and withdrawal effects
- Unique mechanics: dissociation, peace, trip, aura, teleport, inventory shuffle
- Fully configurable; new drugs can be added purely through `settings.yml`
- Admin commands for managing player addiction

## Drugs

| Drug | Category | Addiction |
|---|---|---|
| Meth | Illegal | Extreme |
| Heroin | Illegal | Extreme |
| Cocaine | Illegal | High |
| Ecstasy | Illegal | Moderate |
| Ketamine | Illegal | Moderate |
| Weed | Gray-zone | Low |
| Salvia | Gray-zone | Minimal |
| Antidepressants | Gray-zone | Low |
| LSD | Gray-zone | Minimal |
| Shrooms | Gray-zone | Minimal |
| Nicotine | Legal | High |
| Painkillers | Legal | Low |
| Melatonin | Legal | Minimal |
| Herbal Cigarette | Legal | None |
| Vitamins | Legal | None |

[NOTE: these are in-game item effects only. AkitosDrugs does not model or reference real-world substance use.]

## Commands

| Command | Description | Permission |
|---|---|---|
| `/addiction` | Check your own addiction levels | none |
| `/ad drugs` | Open the drug admin menu | `akitosdrugs.admin.drugs` |
| `/ad reload` | Reload config | `akitosdrugs.admin.reload` |
| `/ad addiction <player>` | View a player's addiction levels | `akitosdrugs.admin.addiction` |
| `/ad addiction reset <player>` | Reset a player's addiction | `akitosdrugs.admin.addiction` |
| `/ad addiction set <player> <drug> <amount>` | Set a player's addiction score for a drug | `akitosdrugs.admin.addiction` |

Alias: `/akitosdrugs` maps to `/ad`.

## Permissions

| Permission | Description | Default |
|---|---|---|
| `akitosdrugs.admin` | Base permission required to use `/ad` | op |
| `akitosdrugs.admin.drugs` | Access to the drug admin menu | op |
| `akitosdrugs.admin.addiction` | View and modify player addiction | op |
| `akitosdrugs.admin.reload` | Reload AkitosDrugs config | op |

## Configuration

`plugins/AkitosPlugins/AkitosDrugs/settings.yml` defines each drug as an entry under `drugs:`. Example:

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
```

| Key | Type | Description |
|---|---|---|
| `display-name` | string | Name shown in menus and on the item |
| `item` | string | Bukkit material used as the drug's item |
| `category` | string | `legal`, `gray-zone`, or `illegal` |
| `lore` | list | Lore lines shown on the item |
| `addiction-per-use` | double | Addiction points added per use |
| `use-cooldown-seconds` | integer | Seconds a player must wait between uses |

[NOTE: effect-specific keys, such as dissociation strength or teleport range, are documented as comments directly in `settings.yml` and are not repeated here.]

## Adding Custom Drugs

Add a new entry under `drugs:` in `settings.yml`. No code changes or recompilation are required.

## Part of the Akitos Plugin Network

- [AkitosCore](https://github.com/AkitoSekuna/AkitosCore) (required)
