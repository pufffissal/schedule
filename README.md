# ⏳ Schedule

Minimalista Android alkalmazás események kezeléséhez, kezdőképernyő widgettel.

---

## Képernyőképek

| Főképernyő | Hozzáadás |
|---|---|
| Eseménylista élő visszaszámlálással | Esemény neve, dátum, ikon, szín, értesítés |

---

## Funkciók

- Visszaszámláló események létrehozása névvel, dátummal, ikonnal és szín akcentussal
- Élő visszaszámlálás: napok / órák / percek / másodpercek (tabular numbers, layout shift nélkül)
- Esemény szerkesztése és törlése (swipe-to-delete + hosszú nyomás menü)
- Megerősítő dialógus törlés előtt
- Rendezés legközelebbi határidő vagy létrehozás dátuma szerint
- Opcionális értesítés: 1 nappal, 1 órával vagy egyéni időponttal előre
- Befejezett esemény állapot kezelése
- Üres állapot illusztráció, ha nincs egyetlen esemény sem
- Kezdőképernyő widget (3 méretben)

---

## Technológiai stack

| Réteg | Technológia |
|---|---|
| UI | Jetpack Compose |
| Widget | androidx.glance:glance-appwidget |
| Adatbázis | Room |
| DI | Hilt |
| Async | Coroutines + Flow |
| Háttérfeladatok | WorkManager |
| Értesítések | AlarmManager (setExactAndAllowWhileIdle) |
| Build | Gradle (Kotlin DSL) |

---

## Rendszerkövetelmények

- **Min SDK:** 26 (Android 8.0 Oreo)
- **Target SDK:** 34 (Android 14)
- **Nyelv:** Kotlin
- **Build tool:** Android Studio Hedgehog vagy újabb / Gradle 8+

---

## Build utasítások

### 1. Projekt klónozása

```bash
git clone https://github.com/pufffissal/schedule.git
cd schedule
```

### 2. Debug APK buildelése

```bash
./gradlew assembleDebug
```

A kész APK elérési útja:
```
app/build/outputs/apk/debug/app-debug.apk
```

### 3. Telepítés csatlakoztatott eszközre

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 4. Release APK buildelése (aláírással)

Hozz létre egy `keystore.properties` fájlt a projekt gyökerében:

```properties
storeFile=sajat_kulcs.jks
storePassword=JELSZO
keyAlias=ALIAS
keyPassword=KULCS_JELSZO
```

Majd:

```bash
./gradlew assembleRelease
```

---

## Widget beállítása

1. Hosszan nyomj a kezdőképernyőn → **Widgetek**
2. Keresd meg a **Visszaszámláló** widgetet
3. Válaszd ki a kívánt méretet:
   - **2×2** — egy esemény, nagy napon szám
   - **4×1** — egy esemény, teljes visszaszámlálás sorban
   - **4×2** — két legközelebbi esemény
4. A 2×2 és 4×1 widget konfiguráló képernyőn kiválaszthatod, melyik eseményt jelenítse meg
5. A widget 60 másodpercenként frissül automatikusan

> **Megjegyzés:** Android 12+ rendszeren az alkalmazásnak szükséges az `SCHEDULE_EXACT_ALARM` engedély a pontos frissítésekhez. Az alkalmazás kérni fogja ezt az engedélyt az első indításkor; ha nem adod meg, a widget WorkManager alapú tartalék frissítéssel működik tovább.

---

## Engedélyek

| Engedély | Mire kell |
|---|---|
| `RECEIVE_BOOT_COMPLETED` | Widget és értesítések újraindítása eszköz-újraindítás után |
| `SCHEDULE_EXACT_ALARM` | Pontos widget frissítés és értesítések |
| `POST_NOTIFICATIONS` | Esemény emlékeztetők (Android 13+) |
| `VIBRATE` | Haptic feedback törléskor és mentéskor |

---

## Projekt struktúra

```
app/
├── data/
│   ├── db/             # Room adatbázis, DAO, entitások
│   └── repository/     # EventRepository
├── di/                 # Hilt modulok
├── ui/
│   ├── events/         # Főképernyő (lista, üres állapot)
│   ├── addevent/       # Hozzáadás / szerkesztés képernyő
│   └── theme/          # Színek, tipográfia, alakzatok
├── widget/
│   ├── small/          # 2×2 Glance widget
│   ├── wide/           # 4×1 Glance widget
│   └── medium/         # 4×2 Glance widget
└── notifications/      # AlarmManager, értesítés kezelő
```

---

## Ismert korlátozások

- Az alkalmazás csak helyi adatokat tárol — felhő szinkronizáció nincs
- Doze módban az értesítések késhetnek, ha az `SCHEDULE_EXACT_ALARM` engedély nincs megadva
- A widget minimum frissítési ideje 60 másodperc (rendszerkorlát)

---