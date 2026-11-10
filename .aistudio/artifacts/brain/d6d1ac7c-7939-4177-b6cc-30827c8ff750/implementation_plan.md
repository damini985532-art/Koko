# Catering Display Manager

A production-ready Catering Inventory and Display Management Android application designed for catering managers, event planners, and banquet teams to effortlessly organize display counters, track display equipment and food supplies, pack checklists with real-time status indicators, and share inventory across team devices.

## User Review & Critical Decisions

> [!IMPORTANT]
> The application will faithfully replicate the exact workflows and visual styling captured in the reference videos: the start screen with the orange "ऐप शुरू करें" button, the dark navy/almost black aesthetic, the top statistics summary (Counters, Total Items, Total Qty), the interactive Counter grid, the two-box action panel on the Counter Detail page ("फोटो डालें" and "+ सामान जोड़ें"), and the comprehensive Add/Edit Item sheet.

- **Storage & Team Collaboration**: We adopt a robust local-first architecture powered by Android Room Database with reactive Kotlin Coroutines & Flow. To satisfy team sharing without requiring Google or email logins, an offline-friendly Team Code & Device Registration mechanism is implemented along with JSON database backup/export and QR/Code-based roster sync.
- **Language & Visuals**: Full Hindi interface as demonstrated in the reference videos (with English subtitles/labels for units and technical names), dark luxury catering theme (Dark Navy `#0D1117`, Deep Card `#161B22` / `#1E2638`, Bright Orange `#F26522` / `#FF6B00`, Packed Green `#2EA043`, Danger Red `#DA3633`).
- **Initial Sample Data**: Ships with rich pre-loaded starter counters (Pizza Van, Sushi Van, Mexican Counter, Lebanese Counter, Dimsum Counter, Teppanyaki Counter) and real equipment items (Channel Light 10ft, Support Stand, Apple Box, Extension Wire, etc.) so the app is instantly engaging and testable.

---

## 1. Overview & Core Concept

Catering setups require rapid on-site tracking of both decorative display elements (warm lights, stands, risers, signage) and kitchen equipment across multiple simultaneous food stations. 

- **Target Persona**: Catering event managers, warehouse staff, banquet supervisors, and counter display designers.
- **Key Value**: Eliminates lost equipment and delayed food truck setups by giving teams an instant, visual, photo-backed checklist for every counter.
- **Zero Friction**: No mandatory login or password prompts on launch. Tapping "ऐप शुरू करें" enters the dashboard immediately.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Start Screen (वेलकम स्क्रीन)**:
   - Atmospheric catering banquet photo backdrop with subtle vignette.
   - Chef hat golden icon + "Catering Display Manager" bold display typography.
   - Paging dots indicator.
   - Prominent bottom pill button: orange **"ऐप शुरू करें"**.
   - Direct smooth transition to the Home Page.

2. **Home Page (होम पेज – मेरे Counters)**:
   - Top Bar: Chef hat brand icon, app title, quick search icon, notification bell, and team sharing icon.
   - **Metrics Row**: Three distinct rounded metric badges showing **कुल Counters**, **कुल Items**, and **Total Quantity**.
   - **Counter Grid**: 2-column responsive grid of counter cards (e.g. Pizza Van, Sushi Van, Mexican Counter, Dimsum Counter), displaying photo, counter name, item count, and quick tap interaction.
   - **Sticky Bottom Action**: Full-width high-contrast orange button: **"+ नया Counter जोड़ें"**.

3. **New Counter Sheet / Modal (नया Counter जोड़ें)**:
   - Header with back navigation and title "नया Counter जोड़ें".
   - Large dashed camera upload container: "Counter की फोटो जोड़ें" supporting camera capture, gallery picker, or sample catering presets.
   - Form fields: "Counter का नाम *" (e.g., Pizza Van) and "विवरण (वैकल्पिक)".
   - Save button: "Counter सेव करें", returning to the Home Page and immediately highlighting the new counter.

4. **Counter Detail Page (Counter विवरण – Pizza Van)**:
   - Header: Back button, Counter title, edit/delete actions, and counter-specific item badge ("कुल सामान: X Items").
   - **Hero Display Banner**: High-resolution image of the counter station.
   - **Dual Action Box Layout (Directly from Reference Video)**:
     - **Left Box**: Square photo upload box with camera icon and "फोटो डालें" / "Add Upload".
     - **Right Box**: Large dark blue square button with distinct high-contrast white Plus (`+`) icon and text **"सामान जोड़ें"**.
   - **Section & Search Bar**: Quick search inside the counter's items and optional section pills (Lights, Disposables, Wires, Connectors, Dry Store).
   - **Packing Status Bar**: Filter chips (All, Packed, Pending) with real-time tally counters and a "Reset All" option for a new event.
   - **Item Cards List**: Dedicated list of items belonging strictly to this Counter ID.

5. **Add / Edit Item Form (नया सामान जोड़ें)**:
   - Title: "नया सामान जोड़ें – [Counter Name]".
   - Photo picker with camera/gallery options and live thumbnail preview.
   - Item Name field (e.g., "Channel Light 10ft").
   - Numeric Quantity input with quick increment/decrement buttons.
   - Unit dropdown (Pcs, Box, Set, Meter, Feet, Kg, Gram, Pair, Roll, Packet, Dozen, Litre, Other).
   - Notes / description field (e.g., "Warm Light, Display के लिए").
   - Packed checkbox toggle.
   - Primary orange button: **"सामान सेव करें"**.

6. **Team Sharing & Management (टीम शेयरिंग)**:
   - Admin can create a team name and generate a 6-character team invitation code.
   - Team members join with device name + invitation code without Google/email login.
   - Quick export/import of inventory package (.json) for offline cross-device sync.

### Visual Styling Tokens

```
Background:           #0D1117 (Dark Navy / Black)
Surface / Card:       #161B22 (Deep Charcoal Blue)
Card Elevated:        #212836 (Lighter Contrast Blue)
Primary Accent:       #F26522 (Catering Vibrant Orange)
Primary Hover/Focus:  #FF7A38
Text Primary:         #F0F6FC (Crisp White)
Text Secondary:       #8B949E (Light Slate Grey)
Packed Success:       #2EA043 (Vibrant Emerald Green)
Pending Warning:      #D29922 (Amber Gold)
Delete / Danger:      #DA3633 (Crimson Red)
Border / Divider:     #30363D
```

---

## 3. Key Product Decisions & Trade-Offs

- **Local-First Room Database + StateFlow**:
  - *Why*: Catering crews frequently work in underground hotel banquet halls, outdoor wedding lawns, and banquet basements with weak or zero cellular connectivity. Local Room persistence guarantees 100% zero-latency interaction, zero crashes on network drops, and immediate state persistence.
  - *Data Isolation*: Foreign key relationships strictly enforce that items belong to a single `counterId`. Queries use `SELECT * FROM items WHERE counterId = :counterId` to ensure complete isolation between counters (e.g., Pizza Van items never leak into Sushi Van).
- **In-App Device Photo Storage**:
  - Photos captured or selected are copied into the app's secure internal storage directory (`filesDir/counter_images/` and `filesDir/item_images/`), ensuring images persist across device restarts and app upgrades without external URL expiration.
- **Multi-Event Packing Reset**:
  - A dedicated action allows the manager to reset all item packing checkboxes to "Pending" when preparing for a new catering event, while preserving inventory names and quantities.

---

## 4. Technical Architecture & Data Strategy

### System Architecture Diagram

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Jetpack Compose UI Layer                        │
├─────────────────┬──────────────────┬─────────────────┬─────────────────┤
│  WelcomeScreen  │    HomeScreen    │  CounterDetail  │   ItemFormSheet │
│  - Hero splash  │  - Counters grid │  - Hero banner  │  - Photo picker │
│  - Start button │  - Stat badges   │  - 2-Box Action │  - Name & Qty   │
│                 │  - Search & FAB  │  - Packing tabs │  - Unit dropdown│
└────────┬────────┴────────┬─────────┴────────┬────────┴────────┬────────┘
         │                 │                  │                 │
         ▼                 ▼                  ▼                 ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        CateringViewModel                               │
│  - uiState: StateFlow<CateringUiState>                                 │
│  - searchCounters(), selectCounter(), saveCounter(), deleteCounter()   │
│  - saveItem(), updateItem(), deleteItem(), togglePackingStatus()       │
│  - createTeam(), joinTeam(), resetPackingForEvent()                    │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        CateringRepository                              │
│  - Coordinates Room Database, Asset Pre-seeding & File Storage         │
└───────────────┬────────────────────────────────────────┬───────────────┘
                │                                        │
                ▼                                        ▼
┌────────────────────────────────┐     ┌─────────────────────────────────┐
│     Room Database (SQLite)     │     │      Local Image Storage        │
│  - CounterEntity               │     │  - Context.filesDir/images      │
│  - ItemEntity (FK: counterId)  │     │  - Camera URI & Gallery Picker  │
│  - PackingRecordEntity         │     │  - Bundled Food & Truck Assets  │
│  - TeamEntity & TeamMember     │     └─────────────────────────────────┘
└────────────────────────────────┘
```

### Data Models & Entities

```kotlin
// Room Entities:
@Entity(tableName = "counters")
data class CounterEntity(
    @PrimaryKey val counterId: String,
    val name: String,
    val description: String = "",
    val imageUri: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(
            entity = CounterEntity::class,
            parentColumns = ["counterId"],
            childColumns = ["counterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("counterId")]
)
data class ItemEntity(
    @PrimaryKey val itemId: String,
    val counterId: String,
    val name: String,
    val quantity: Double,
    val unit: String,
    val notes: String = "",
    val isPacked: Boolean = false,
    val section: String = "",
    val imageUri: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "team_profiles")
data class TeamProfileEntity(
    @PrimaryKey val teamId: String,
    val teamName: String,
    val inviteCode: String,
    val memberName: String,
    val isAdmin: Boolean = true
)
```

### Step-by-Step Execution Plan

1. **Resource & Icon Setup**:
   - Update `strings.xml` and `metadata.json` with proper app title ("Catering Display Manager").
   - Create custom adaptive app icon with catering chef hat / food truck motif.
   - Configure theme colors (`Color.kt`, `Theme.kt`) matching the reference video dark navy and vibrant orange palette.
2. **Database & Repository Layer**:
   - Create Room Entities, DAOs (`CounterDao`, `ItemDao`, `TeamDao`), and `CateringDatabase`.
   - Implement `CateringRepository` with initial seed data (Pizza Van, Sushi Van, Mexican Counter, Dimsum Counter, Teppanyaki Counter, and realistic catering equipment items).
3. **ViewModel & State Management**:
   - Build `CateringViewModel` with reactive state flows, filtering by counter, search query handling, and packing state manipulation.
4. **UI Screen Implementation**:
   - **Start Screen (`WelcomeScreen.kt`)**: Replicating the splash from video 2 with "ऐप शुरू करें".
   - **Home Screen (`HomeScreen.kt`)**: Metrics cards, search bar, 2-column counter cards grid, and "+ नया Counter जोड़ें" button.
   - **Counter Modal / Screen (`AddEditCounterScreen.kt`)**: Photo capture/upload, title, description, and validation.
   - **Counter Detail Screen (`CounterDetailScreen.kt`)**: Hero banner, Left photo box ("फोटो डालें"), Right Add button ("+ सामान जोड़ें"), packing chips, search, and separate isolated item list.
   - **Add/Edit Item Sheet (`AddEditItemScreen.kt`)**: Photo upload, Item Name, numeric Quantity, Unit selector dropdown, Notes, and "सामान सेव करें".
   - **Team & Sync Dialog (`TeamSyncDialog.kt`)**: Invite code generator, team join flow, and JSON export/import.
5. **Image Picker & Camera Integration**:
   - Camera launcher via `rememberLauncherForActivityResult(TakePicturePreview())` and Gallery via `PickVisualMedia()`.
   - File saving to internal storage with clean fallback thumbnails.
6. **Verification & Testing**:
   - Run `compile_applet` to ensure error-free compilation and run unit/Robolectric tests for Counter-Item isolation.
