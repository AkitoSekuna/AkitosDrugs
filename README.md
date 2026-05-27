# AkitosDrugs

A comprehensive drug mechanics plugin with addiction, withdrawal and unique effects. Nearly fully configurable via `settings.yml`; add new drugs without touching any code.

## Requirements
- Paper 1.21.11+
- Java 21+
- [AkitosCore](https://github.com/AkitoSekuna/AkitosCore)

## Installation
1. Install [AkitosCore](https://github.com/AkitoSekuna/AkitosCore) first
2. Drop `AkitosDrugs.jar` into your `plugins/` folder
3. Restart your server
4. Configure `plugins/AkitosPlugins/AkitosDrugs/settings.yml`

## Features
- 12 drugs across 3 categories (Legal, Gray-zone, Illegal)
- Per-drug addiction system with decay and withdrawal effects
- Unique mechanics: dissociation, peace, trip, aura, teleport, inventory shuffle
- Fully configurable — add new drugs purely via `settings.yml`
- Admin commands for managing player addictions

## Drugs
| Drug | Category | Addiction |
|------|----------|-----------|
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

## Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/addiction` | Check your addiction levels | none |
| `/ad drugs` | Open drug admin menu | `akitosdrugs.admin.drugs` |
| `/ad reload` | Reload config | `akitosdrugs.admin.reload` |
| `/ad addiction <player>` | View player addiction | `akitosdrugs.admin.addiction` |
| `/ad addiction reset <player>` | Reset player addiction | `akitosdrugs.admin.addiction` |
| `/ad addiction set <player> <drug> <amount>` | Set addiction score | `akitosdrugs.admin.addiction` |

## Adding Custom Drugs
Simply add a new entry to `settings.yml`:
```yaml
drugs:
  mydrug:
    display-name: "§aMy Drug"
    item: SUGAR
    category: legal
    lore:
      - "§7A custom drug."
      - "§7Not addictive."
      - "§7My custom drug."
      - "§7Category: §aLegal"
    addiction-per-use: 1.0
    use-cooldown-seconds: 30
    ...
```

## Part of the Akito's Plugin Network
- [AkitosCore](https://github.com/AkitoSekuna/AkitosSekuna/AkitosCore)
