# SumerWars

> **Note:** The project might be renamed to "Babylon Wars" or "Rise of Babylon".

## 1. Overview

SumerWars is a real-time strategy (MMORTS) game set in ancient Mesopotamia. Players govern a city-state, manage scarce
resources, trade for survival, and wage war for supremacy. The game emphasizes logistical constraints (wood scarcity)
and historical accuracy.

---

## 2. Resources & Economy

The economy is designed around **scarcity** and **interdependence**.

| Resource         | Type      | Source            | Usage                                            |
|:-----------------|:----------|:------------------|:-------------------------------------------------|
| **BARLEY**       | Common    | `Barley Fields`   | Food, Unit Upkeep, Currency for basic trade      |
| **CLAY**         | Common    | `Clay Pit`        | Buildings, Walls, Basic Units (Slingers)         |
| **BRONZE**       | Strategic | `Bronze Foundry`  | Weapons, Armor, Research                         |
| **WOOD**         | Rare      | Trade Only (Souk) | Advanced Units (Archers, Chariots), Navy, Palace |
| **FAVOR**        | Magic     | `Ziggurat`        | Myth Units, Spells                               |
| **LAPIS-LAZULI** | Prestige  | PvE, Quests       | City Expansion (New Settlements)                 |

---

## 3. Buildings

Buildings have a geometric cost progression (Cost = Base * 1.2^Level).

### Production

| Building           | Function                        | Base Cost           |
|:-------------------|:--------------------------------|:--------------------|
| **BARLEY_FIELDS**  | Produces Food                   | 50 Clay             |
| **CLAY_PIT**       | Extracts Clay                   | 50 Barley           |
| **BRONZE_FOUNDRY** | Produces Bronze                 | 100 Clay, 50 Barley |
| **WAREHOUSE**      | Protects resources from looting | 200 Clay, 100 Wood  |

### Military & Defense

| Building           | Function                             | Base Cost            |
|:-------------------|:-------------------------------------|:---------------------|
| **BARRACKS**       | Trains Infantry & Cavalry            | 200 Clay, 100 Bronze |
| **RIVER_SHIPYARD** | Builds Naval units. Needs Water tile | 150 Clay, 200 Wood   |
| **CITY_WALLS**     | Increases City Defense %             | 500 Clay             |
| **WATCHTOWER**     | Provides Intel on incoming attacks   | 300 Clay, 50 Wood    |

### Civil & Religious

| Building         | Function                                         | Base Cost             |
|:-----------------|:-------------------------------------------------|:----------------------|
| **DWELLING**     | Increases Population Cap, Speeds up Construction | 80 Clay, 20 Barley    |
| **SOUK**         | Allows buying Wood & Spying                      | 150 Clay, 50 Barley   |
| **ZIGGURAT**     | Generates Favor. Unlocks God                     | 1000 Clay, 200 Bronze |
| **TABLET_HOUSE** | Unlocks Research (Tech Tree)                     | 400 Clay, 100 Wood    |

### Special

| Building         | Function                   | Base Cost |
|:-----------------|:---------------------------|:----------|
| **ROYAL_PALACE** | HQ. Required for Expansion | -         |

---

## 4. Units & Combat System

Combat follows a **Rock-Paper-Scissors** logic:

* **Spears** beat **Cavalry/Chariots**
* **Cavalry/Chariots** beat **Ranged/Light Inf**
* **Ranged** beats **Spears**

> **Legend:**
> * **ATK**: Damage dealt
> * **DEF**: Damage taken (Health + Armor)
> * **SPD**: Movement speed (1=Slow, 20=Fast)
> * **TIME**: Training time
> * **SIZE**: Transport space occupied by 1 unit
> * **CARRY**: Transport capacity (Total Size allowed)

## 4. Units & Combat System

Combat follows a **Rock-Paper-Scissors** logic:

* **Spears** beat **Cavalry/Chariots**
* **Cavalry/Chariots** beat **Ranged/Light Inf**
* **Ranged** beats **Spears**

> **Legend:**
> * **ATK**: Damage dealt
> * **DEF**: Damage taken (Health + Armor)
> * **SPD**: Movement speed (1=Slow, 20=Fast)
> * **SIZE**: Transport space occupied by 1 unit
> * **TIME**: Base training time (Decreases with Building Level)

### Infantry (Barracks)

| Unit            | Role            | Cost                   | ATK | DEF | SPD | SIZE  |    TIME     |
|:----------------|:----------------|:-----------------------|:---:|:---:|:---:|:-----:|:-----------:|
| **SLINGER**     | Tier 1 Ranged   | 40 Barley, 60 Clay     |  6  |  4  |  6  | **1** | **2m 30s**  |
| **SPEARMAN**    | Tier 1 Anti-Cav | 80 Barley, 40 Bronze   | 10  | 25  |  4  | **1** | **4m 00s**  |
| **AXEMAN**      | Tier 2 Shock    | 120 Barley, 80 Bronze  | 30  | 10  |  5  | **1** | **6m 00s**  |
| **ROYAL_GUARD** | Tier 3 Tank     | 300 Barley, 200 Bronze | 35  | 80  |  3  | **2** | **15m 00s** |

### Ranged & Cavalry (Barracks)

| Unit         | Role         | Cost                   | ATK | DEF | SPD | SIZE  |    TIME     |
|:-------------|:-------------|:-----------------------|:---:|:---:|:---:|:-----:|:-----------:|
| **ARCHER**   | Tier 2 DPS   | 100 Barley, 50 Wood    | 20  |  5  |  6  | **1** | **8m 00s**  |
| **WAR_CART** | Tier 1 Tank  | 200 Barley, 150 Bronze | 35  | 40  |  8  | **5** | **12m 00s** |
| **CHARIOT**  | Tier 3 Shock | 400 Barley, 200 Wood   | 80  | 25  | 18  | **5** | **25m 00s** |

### Naval (River Shipyard)

| Unit                | Role            | Cost                           | ATK | DEF | SPD |  CARRY  |    TIME     |
|:--------------------|:----------------|:-------------------------------|:---:|:---:|:---:|:-------:|:-----------:|
| **LIGHT_BARQUE**    | Scout / Courier | 100 Clay, 50 Wood              |  5  | 10  | 15  | **10**  | **20m 00s** |
| **TRANSPORT_BARGE** | Transport       | 300 Clay, 200 Wood             |  0  | 100 |  8  | **200** | **45m 00s** |
| **WAR_BARGE**       | Combat          | 200 Clay, 150 Wood, 100 Bronze | 60  | 60  | 10  | **50**  | **1h 00m**  |
| **ASSAULT_SHIP**    | Siege           | 400 Clay, 300 Wood, 200 Bronze | 120 | 80  |  6  | **20**  | **1h 30m**  |

### Special Units

| Unit                | Function      | Cost                          | SPD |  SIZE  |    TIME    |
|:--------------------|:--------------|:------------------------------|:---:|:------:|:----------:|
| **INFORMANT**       | Invisible Spy | 200 Barley                    | 20  | **1**  | **5m 00s** |
| **SETTLER_CARAVAN** | Expansion     | 10k Barley, 10k Clay, 5k Wood |  2  | **50** | **4h 00m** |

---

## 5. Mythology & Deities

Players choose a patron Deity. This choice defines their playstyle (Unit-focused or Spell-focused).

| Deity       | Domain       | Type       | Unique Unit (Ziggurat)                                  | Divine Powers (Cost Favor)                                                                             |
|:------------|:-------------|:-----------|:--------------------------------------------------------|:-------------------------------------------------------------------------------------------------------|
| **MARDUK**  | Order        | Guardian   | **LAMASSU**<br>*(Flying Tank, high HP, defensive aura)* | -                                                                                                      |
| **SHAMASH** | Sun/Justice  | Guardian   | **SCORPION_MAN**<br>*(High damage, poison)*             | -                                                                                                      |
| **NERGAL**  | Death        | Guardian   | **PAZUZU**<br>*(Wind Demon, debuffs enemy attack)*      | -                                                                                                      |
| **ISHTAR**  | War/Love     | Intervener | -                                                       | **Battle Frenzy**: +20% Attack for 1h<br>**Betrayal**: Steals 5% of attacking troops                   |
| **ENKI**    | Water/Wisdom | Intervener | -                                                       | **Great Flood**: Fills warehouses (Barley/Clay)<br>**Divine Inspiration**: Finishes Research instantly |
| **ADAD**    | Storm        | Intervener | -                                                       | **Thunderbolt**: Damages buildings or walls<br>**Mud Slide**: Slows army movement by 50%               |

> **Note:** `ZIGGURAT_GUARDIAN` is a generic temple guard available to all gods, regardless of the choice

### 5.1 Mythical Unit Stat

Details for the "Guardian" Gods.

| Unit                  | Deity   | Cost                           | ATK | DEF | SPD |  SIZE  |    TIME     | Special Ability                    |
|:----------------------|:--------|:-------------------------------|:---:|:---:|:---:|:------:|:-----------:|:-----------------------------------|
| **ZIGGURAT_GUARDIAN** | All     | 500 Clay, 300 Bronze, 50 Favor | 50  | 50  |  5  | **2**  | **30m 00s** | -                                  |
| **LAMASSU**           | Marduk  | 1000 Barley, 400 Favor         | 100 | 300 |  4  | **20** | **2h 00m**  | **Aura**: +10% Def to allies       |
| **SCORPION_MAN**      | Shamash | 1000 Barley, 350 Favor         | 250 | 80  | 12  | **10** | **1h 45m**  | **Poison**: DoT (Damage over Time) |
| **PAZUZU**            | Nergal  | 1000 Barley, 300 Favor         | 150 | 100 | 20  | **10** | **1h 45m**  | **Terror**: -10% Atk to enemies    |

### 5.2 Divine Spell Costs

Details for the "Intervener" Gods.

| Deity      | Spell Name           |   Cost    | Cooldown | Effect                                    |
|:-----------|:---------------------|:---------:|:--------:|:------------------------------------------|
| **ISHTAR** | *Battle Frenzy*      | 150 Favor |    4h    | Army ATK +20% for next battle             |
|            | *Betrayal*           | 350 Favor |   12h    | 5% of attacking units defect to your side |
| **ENKI**   | *Great Flood*        | 200 Favor |    8h    | Fills 30% of Warehouse capacity instantly |
|            | *Divine Inspiration* | 400 Favor |   24h    | Instantly finishes current Research       |
| **ADAD**   | *Thunderbolt*        | 250 Favor |    6h    | Kills 10-15 random units in target army   |
|            | *Mud Slide*          | 100 Favor |    2h    | Target army movement speed -50% for 2h    |

## 6. Technology Tree (Tablet House)

The Tablet House allows two types of research:

1. **UNLOCKS (Max Level 1):** One-time research to unlock units or mechanics.
2. **PASSIVES (Max Level 10):** Upgradable technologies providing incremental bonuses.

> **Passive Progression Logic:**
> * Cost increases with level.
> * **Level 1:** Base Bonus (e.g., +3%).
> * **Level 10:** Max Bonus (e.g., +30%).

### Economy & Construction

| Technology               | Type    | Max Lvl | Effect per Level                      | Prerequisite         |
|:-------------------------|:--------|:-------:|:--------------------------------------|:---------------------|
| **BRICK_FIRING**         | Passive |   10    | **-3%** Construction Time             | -                    |
| **CANAL_DREDGING**       | Passive |   10    | **+3%** Barley Production             | -                    |
| **DEEP_MINING**          | Passive |   10    | **+3%** Clay Production               | -                    |
| **STANDARDIZED_WEIGHTS** | Unlock  |    1    | Unlocks **SOUK** trading capabilities | Canal Dredging Lvl 1 |
| **CARAVAN_LOGISTICS**    | Passive |   10    | **+5%** Merchant Speed & Capacity     | Standardized Weights |

### Military: Infantry (Barracks)

| Technology             | Type    | Max Lvl | Effect per Level                   | Prerequisite            |
|:-----------------------|:--------|:-------:|:-----------------------------------|:------------------------|
| **BRONZE_METALLURGY**  | Unlock  |    1    | Unlocks **Spearman** & **Axeman**  | Deep Mining Lvl 1       |
| **PHALANX_FORMATION**  | Passive |   10    | **+3% DEF** for Spearmen           | Bronze Metallurgy       |
| **LARGE_SHIELDS**      | Unlock  |    1    | Unlocks **Royal Guard**            | Phalanx Formation Lvl 5 |
| **METALLURGY_MASTERY** | Passive |   10    | **+2% ATK** for all Melee Infantry | Bronze Metallurgy       |

### Military: Ranged & Cavalry (Barracks)

| Technology           | Type    | Max Lvl | Effect per Level                         | Prerequisite           |
|:---------------------|:--------|:-------:|:-----------------------------------------|:-----------------------|
| **SLING_AMMUNITION** | Passive |   10    | **+3% ATK** for Slingers                 | -                      |
| **COMPOSITE_BOW**    | Unlock  |    1    | Unlocks **Archer**                       | Sling Ammunition Lvl 3 |
| **SPOKED_WHEELS**    | Unlock  |    1    | Unlocks **War Cart**                     | Bronze Metallurgy      |
| **REINFORCED_AXLE**  | Unlock  |    1    | Unlocks **Chariot**                      | Spoked Wheels          |
| **SCYTHED_BLADES**   | Passive |   10    | **+2% Bonus DMG** vs Infantry (Chariots) | Reinforced Axle        |

### Naval (Shipyard)

| Technology           | Type    | Max Lvl | Effect per Level                    | Prerequisite         |
|:---------------------|:--------|:-------:|:------------------------------------|:---------------------|
| **WATERPROOFING**    | Unlock  |    1    | Unlocks **River Shipyard** building | Canal Dredging Lvl 1 |
| **NAVAL_RAM**        | Passive |   10    | **+3% ATK** for Ships               | Waterproofing        |
| **RIVER_NAVIGATION** | Passive |   10    | **+3% SPD** for Ships               | Waterproofing        |

### Intelligence & Religion

| Technology             | Type    | Max Lvl | Effect per Level                          | Prerequisite             |
|:-----------------------|:--------|:-------:|:------------------------------------------|:-------------------------|
| **SIGNAL_FIRE_SYSTEM** | Passive |   10    | **+5%** Watchtower Range/Warning time     | Brick Firing Lvl 1       |
| **MILITARY_CIPHER**    | Unlock  |    1    | Watchtower reveals **exact unit numbers** | Signal Fire Lvl 5        |
| **BORDER_PATROLS**     | Passive |   10    | **+5% Chance** to catch enemy Spies       | Military Cipher          |
| **ZIGGURAT_ASTRONOMY** | Passive |   10    | **+3%** Favor Generation                  | -                        |
| **THEOCRACY**          | Unlock  |    1    | **+5%** Global Resource Prod (Loyalty)    | Ziggurat Astronomy Lvl 5 |

### Expansion (End Game)

| Technology                  | Type   | Max Lvl | Effect per Level                 | Prerequisite                        |
|:----------------------------|:-------|:-------:|:---------------------------------|:------------------------------------|
| **DYNASTIC_ADMINISTRATION** | Unlock |    1    | Unlocks **SETTLER_CARAVAN** unit | Theocracy + Caravan Logistics Lvl 5 |

---

## 7. Key Mechanics

### Espionage System

* **Offense:** Send `INFORMANT` unit.
* **Defense:** `WATCHTOWER` Level + `BORDER_PATROLS` (Tech Lvl) + Your own Spies.
* **Outcome:**
    * **Success:** You see buildings and troops.
    * **Failure:** Your spies are killed, enemy receives a notification "Attack prevented".

### Expansion (Fibonacci Sequence)

To build a new city, a player must pay a **Lapis Lazuli** cost that scales following the Fibonacci sequence.
*Hard Limit:* 50 Cities max per player.

> **Requirements:**
> 1. `DYNASTIC_ADMINISTRATION` Researched.
> 2. `SETTLER_CARAVAN` Unit trained.
> 3. Enough **Lapis Lazuli** in stock.

| City Slot # | Lapis Cost  |
|:------------|:------------|
| **City 1**  | 0           |
| **City 2**  | **400**     |
| **City 3**  | **900**     |
| **City 4**  | **1600**    |
| **City 5**  | **2 500**   |
| **City 6**  | **3 600**   |
| **City 7**  | **4 900**   |
| **City 8**  | **6 400**   |
| *...*       | *...*       |
| **City 50** | **250 000** |

> **Calculation:** 100*(City Count^2)

