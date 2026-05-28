# 🚀 HORUS-21 Mission Control System
### OrbitSim v1.0 — Java SE Final Project · Epicode Institute of Technology 2026

> *"Per aspera ad astra"* — Through hardships to the stars

A command-line mission control simulator for spacecraft **HORUS-21**, built as a Java SE final project. The application demonstrates advanced object-oriented design through real-world patterns, secure programming practices, and a fully interactive CLI that guides the user through every phase of a space mission.

---

## 📑 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Architecture](#-architecture)
- [Design Patterns](#-design-patterns)
- [Technologies](#-technologies)
- [Security](#-security)
- [Setup & Execution](#-setup--execution)
- [CLI Commands](#-cli-commands)
- [Mission Flow](#-mission-flow)
- [Project Structure](#-project-structure)
- [Testing](#-testing)
- [Known Limitations](#-known-limitations)
- [Future Work](#-future-work)

---

## 🌍 Overview

HORUS-21 Mission Control is an interactive CLI application simulating the control room of a spacecraft mission. The operator can launch the vehicle, monitor telemetry, trigger orbital maneuvers, respond to system anomalies, and guide the crew through reentry and splashdown — all from a terminal.

The project was designed to showcase **clean OOP architecture**, **multiple design patterns working together**, and **secure-by-default programming** in pure Java SE.

---

## ✨ Features

| Feature | Description |
|---|---|
| 🚀 **Mission Phases** | Full lifecycle: Pre-Launch → Launch → Ascent → Orbital → Reentry → Splashdown |
| ⚠️ **Anomaly Injection** | 6 injectable fault types processed through a Chain of Responsibility pipeline |
| 📡 **Live Telemetry** | Altitude, velocity, fuel, reactor temperature, life support status |
| 🛰️ **System Scan** | Iterator-based full scan of all 7 spacecraft subsystems |
| 📸 **Snapshots** | Memento-based PRE/POST state capture around anomaly events |
| 📋 **Black Box Report** | Complete mission event log available at mission end |
| 🔒 **Secure Design** | Input sanitization, exception shielding, no hardcoded secrets |
| 📝 **Persistent Logging** | File-based logging with auto-directory creation |

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    MissionCLI (Entry Point)              │
│  - Command parsing & routing                            │
│  - Phase lifecycle management                           │
│  - Observer wiring                                      │
└──────────┬──────────────────────────┬───────────────────┘
           │                          │
    ┌──────▼──────┐           ┌───────▼────────┐
    │  Spacecraft  │           │  MissionEventBus│
    │  (Originator)│           │  (Publisher)    │
    │  - Composite │           └───┬─────────┬──┘
    │    hierarchy │               │         │
    │  - Telemetry │    ┌──────────▼─┐  ┌────▼──────────┐
    │  - Memento   │    │ ConsoleAlert│  │ BlackBox      │
    └──────────────┘    │ Observer   │  │ Observer      │
                        └────────────┘  └───────────────┘
           │
    ┌──────▼──────────────────────────────────┐
    │           Composite Tree                 │
    │  SpacecraftModule "HORUS-21 Systems"    │
    │  ├── SpacecraftModule "Propulsion"      │
    │  │     ├── Subsystem "Main Engine"      │
    │  │     ├── Subsystem "Reactor Core"     │
    │  │     └── Subsystem "RCS Thrusters"    │
    │  ├── SpacecraftModule "Life Support"    │
    │  │     ├── Subsystem "O2 Recycler"      │
    │  │     └── Subsystem "CO2 Scrubber"     │
    │  └── SpacecraftModule "Avionics"        │
    │        ├── Subsystem "Navigation Comp"  │
    │        └── Subsystem "Comm Array"       │
    └─────────────────────────────────────────┘
```

---

## 🎨 Design Patterns

### Required Patterns

#### 🏭 Factory — `AnomalyFactory`
Creates `AnomalyContext` objects for each fault type, hiding construction logic from the CLI.

```
CLI ──► AnomalyFactory.create("REACTOR")
              │
              └──► new AnomalyContext("Reactor temperature spike", "REACTOR_CORE", 4)
```

> **Why Factory over direct instantiation?**  
> The CLI should not know which severity a REACTOR anomaly carries, or what system ID it maps to. That is business logic — it belongs in the factory. Adding a new anomaly type (`SOLAR_STORM`) requires changing only one file.

---

#### 🌿 Composite — `SpacecraftComponent` tree
Treats individual subsystems (leaves) and modules (composites) uniformly. `getStatus()` propagates up the tree automatically.

```
root.getStatus()
  └── Propulsion.getStatus()          → CRITICAL  ◄── worst child wins
        ├── Main Engine   → NOMINAL
        ├── Reactor Core  → CRITICAL
        └── RCS Thrusters → NOMINAL
```

> **Why Composite?**  
> The CLI calls `root.getStatus()` without knowing how deep the tree is. Adding a new module (`Scientific Instruments`) requires zero changes to existing code — Open/Closed Principle.

---

#### 🔗 Iterator — `Spacecraft.systemIterator()`
Traverses all leaf subsystems without exposing the internal tree structure.

```java
Iterator<SpacecraftComponent> it = spacecraft.systemIterator();
while (it.hasNext()) {
    SpacecraftComponent sys = it.next(); // always a leaf
    System.out.print(sys.getStatusReport());
}
```

> **Why Iterator?**  
> The SCAN command needs to visit every physical subsystem. The Composite tree's recursive structure is an implementation detail — the caller should not navigate it manually.

---

#### 🛡️ Exception Shielding — throughout
Internal exceptions are caught, logged, and translated into user-friendly messages. Stack traces never reach the operator.

```
AnomalyFactory.create("???")
       │
       └──► OrbitSimException("Unknown anomaly type: '???'...")
                    │
                    └──► CLI catch block
                              │
                              └──► "[MISSION CONTROL] Unknown anomaly type..."
                                       (no stack trace visible)
```

---

### Optional Patterns

#### 🎯 Strategy — `MissionPhase` interface
Each mission phase encapsulates its own available commands, narrative messages, and valid transitions. The CLI delegates everything to `currentPhase`.

```
currentPhase = new OrbitalPhase();
currentPhase.availableCommands()  → ["STATUS","TELEMETRY","MANEUVER","SCAN",...]
currentPhase.canTransitionTo(new ReentryPhase()) → true
currentPhase.canTransitionTo(new LaunchPhase())  → false
```

**Phase state machine:**
```
PRE-LAUNCH ──LAUNCH──► LAUNCH ──► ASCENT ──► ORBITAL ──► REENTRY ──► SPLASHDOWN
                          │          │           │
                          └──ABORT───┴───ABORT───┴──ABORT──► ABORT
```

---

#### 👁️ Observer — `MissionEventBus`
Decouples event producers from consumers. When an anomaly occurs, the bus notifies all subscribers automatically.

```
injectAnomaly() ──► eventBus.publish(MissionEvent)
                          │
                          ├──► ConsoleAlertObserver.onEvent()  → prints to terminal
                          └──► BlackBoxObserver.onEvent()      → stores in log
```

> **Why Observer?**  
> Without it, `injectAnomaly()` would need a direct reference to every consumer. Adding a future `EmailAlertObserver` would require modifying the anomaly code — violating Open/Closed.

---

#### ⛓️ Chain of Responsibility — `AnomalyHandler` pipeline
Each handler has exactly one responsibility. The context object is enriched as it passes through the chain.

```
AnomalyContext
    │
    ▼
DetectionHandler   → logs anomaly type and initial severity
    │
    ▼
AssessmentHandler  → elevates severity if REACTOR or LIFE_SUPPORT
    │
    ▼
IsolationHandler   → isolates system if severity ≥ 3
    │
    ▼
NotificationHandler → alerts Mission Control; crew if severity ≥ 4
    │
    ▼
EscalationHandler  → triggers mission abort if severity = 5
```

> **Template Method embedded:** `AnomalyHandler.handle()` is `final` — it always calls `process()` then passes to `next`. The structure never changes; only the content does.

---

#### 📸 Memento — `SpacecraftMemento` + `MissionCaretaker`
Captures immutable spacecraft state snapshots before and after anomaly events.

```
INJECT_ANOMALY REACTOR
      │
      ├──► caretaker.save( spacecraft.saveMemento("PRE-REACTOR") )
      │
      ├──► [anomaly pipeline runs]
      │
      └──► caretaker.save( spacecraft.saveMemento("POST-REACTOR") )

REPORT ──► caretaker.getAll() → prints PRE/POST diff
```

> **Encapsulation preserved:** `SpacecraftMemento`'s constructor is package-private — only `Spacecraft` (the Originator) can create snapshots. `MissionCaretaker` stores them without accessing their internals.

---

## 🔧 Technologies

| Technology | Usage | Justification |
|---|---|---|
| **Collections Framework** | `ArrayList`, `ArrayDeque`, `List` | Phase command lists, observer registry, memento history |
| **Generics** | `List<T>`, `Iterator<T>`, `Optional<T>` | Type-safe collections throughout |
| **Java I/O** | `FileHandler`, `Files.createDirectories()` | Persistent mission log with auto-directory creation |
| **Logging** | `java.util.logging` | Structured log levels (INFO/WARNING/SEVERE) to file |
| **JUnit 5** | 5 test classes, 24 test methods | Unit tests for Factory, Chain, Composite, Spacecraft, BlackBox |
| **Stream API** | `stream().filter().count()` | Event counting in BlackBoxObserver |
| **Records** | `MissionEvent` as `record` | Immutable event objects with compact syntax |

---

## 🔒 Security

| Practice | Implementation |
|---|---|
| **Input Sanitization** | `type.toUpperCase()` before switch; `isBlank()` guard on all user args |
| **No Hardcoded Secrets** | Log path is relative (`logs/`); no credentials anywhere |
| **Exception Shielding** | All exceptions caught at CLI boundary; user sees only message, never stack trace |
| **Controlled Propagation** | `OrbitSimException` and `IllegalStateException` caught separately in main loop |
| **Resilient Observer Bus** | Each observer wrapped in try-catch; one failing observer doesn't block others |
| **Null Safety** | `currentPhase` null-checked before every access; `caretaker.getLast()` returns null-safe |

---

## ⚙️ Setup & Execution

### Prerequisites
- Java 21+
- Maven 3.8+

### Run
```bash
git clone https://github.com/your-username/missionCLI.git
cd missionCLI
mvn compile
mvn exec:java -Dexec.mainClass="orbitsim.app.MissionCLI"
```

### Run Tests
```bash
mvn test
```

### Log file
Generated automatically at `logs/HORUS-21-mission.log` in the project root.

---

## 🖥️ CLI Commands

| Command | Phase | Description |
|---|---|---|
| `LAUNCH` | Pre-Launch | Start the mission sequence |
| `STATUS` | All | Mission and phase overview |
| `TELEMETRY` | Ascent+ | Live sensor readings |
| `SYSTEMS` | Ascent+ | Full Composite hierarchy report |
| `SCAN` | Orbital | Iterator scan of all 7 subsystems |
| `MANEUVER HOHMANN` | Orbital | Execute Hohmann transfer burn |
| `MANEUVER REBOOST` | Orbital | Execute reboost burn |
| `INJECT_ANOMALY <type>` | Orbital | Trigger anomaly through Chain pipeline |
| `SNAPSHOT` | Orbital | Save manual Memento snapshot |
| `REENTRY` | Orbital | Begin reentry sequence |
| `ABORT` | Launch/Ascent/Orbital/Reentry | Emergency abort |
| `REPORT` | Splashdown/Abort | Full Black Box + Snapshot history |
| `HELP` | All | Contextual command list |
| `EXIT` | All | Shutdown Mission Control |

**Anomaly types:** `REACTOR` · `PRESSURE` · `NAVIGATION` · `LIFE_SUPPORT` · `COMMS` · `THRUSTER`

---

## 🛸 Mission Flow

```
  System loading >>>>>>>>>>>>>>>>>>>>>>>>
  
  ╔═══════════════════════════════════════════════════╗
  ║         HORUS-21 MISSION CONTROL SYSTEM           ║
  ║         OrbitSim v1.0 — Java SE Edition           ║
  ╠═══════════════════════════════════════════════════╣
  ║               "PER ASPERA AD ASTRA"               ║
  ╚═══════════════════════════════════════════════════╝

  MISSION CONTROL> LAUNCH
  [countdown 10...1]
  Liftoff! We are taking off!
  >>> ASCENT >>> ORBITAL

  MISSION CONTROL> INJECT_ANOMALY REACTOR
  🚨 [EMERGENCY] REACTOR_CORE — Reactor temperature spike — Severity 4
  ╔══════ ANOMALY RESPONSE PROTOCOL ══════╗
    [CHAIN 1/5] DETECTION
    [CHAIN 2/5] ASSESSMENT  → severity elevated to 5
    [CHAIN 3/5] ISOLATION   → system isolated
    [CHAIN 4/5] NOTIFICATION → crew alerted
    [CHAIN 5/5] ESCALATION  → MISSION ABORT

  MISSION CONTROL> REPORT
  ╔════════════════════════════════════════════════════════════════════════╗
  ║                    HORUS-21 MISSION BLACK BOX                         ║
  ╠════════════════════════════════════════════════════════════════════════╣
  ║  Total events recorded: 6                                             ║
  ...
```

---

## 📁 Project Structure

```
missionCLI/
├── src/
│   ├── main/java/orbitsim/
│   │   ├── app/
│   │   │   └── MissionCLI.java          # Entry point, CLI loop
│   │   ├── mission/
│   │   │   ├── MissionPhase.java        # Strategy interface
│   │   │   ├── LaunchPhase.java
│   │   │   ├── AscentPhase.java
│   │   │   ├── OrbitalPhase.java
│   │   │   ├── ReentryPhase.java
│   │   │   ├── SplashdownPhase.java
│   │   │   └── AbortPhase.java
│   │   ├── patterns/
│   │   │   ├── chain/                   # Chain of Responsibility
│   │   │   │   ├── AnomalyHandler.java  # Abstract handler (+ Template Method)
│   │   │   │   ├── AnomalyContext.java
│   │   │   │   ├── DetectionHandler.java
│   │   │   │   ├── AssessmentHandler.java
│   │   │   │   ├── IsolationHandler.java
│   │   │   │   ├── NotificationHandler.java
│   │   │   │   └── EscalationHandler.java
│   │   │   ├── composite/               # Composite
│   │   │   │   ├── SpacecraftComponent.java
│   │   │   │   ├── SpacecraftModule.java
│   │   │   │   ├── Subsystem.java
│   │   │   │   └── SystemStatus.java
│   │   │   ├── factory/                 # Factory
│   │   │   │   └── AnomalyFactory.java
│   │   │   ├── memento/                 # Memento
│   │   │   │   ├── SpacecraftMemento.java
│   │   │   │   └── MissionCaretaker.java
│   │   │   └── observer/               # Observer
│   │   │       ├── MissionObserver.java
│   │   │       ├── MissionEventBus.java
│   │   │       ├── MissionEvent.java
│   │   │       ├── ConsoleAlertObserver.java
│   │   │       └── BlackBoxObserver.java
│   │   ├── spacecraft/
│   │   │   └── Spacecraft.java          # Originator, Composite root
│   │   ├── exception/
│   │   │   ├── OrbitSimException.java
│   │   │   ├── SystemFaultException.java
│   │   │   └── IllegalStateException.java
│   │   └── util/
│   │       └── LogManager.java
│   └── test/java/orbitsim/
│       ├── AnomalyFactoryTest.java      # 5 tests
│       ├── AnomalyChainTest.java        # 5 tests
│       ├── CompositeTest.java           # 5 tests
│       ├── SpacecraftTest.java          # 5 tests
│       ├── BlackBoxObserverTest.java    # 4 tests
│       └── MementoTest.java            # 4 tests
├── logs/
│   └── HORUS-21-mission.log            # auto-generated
└── pom.xml
```

---

## 🧪 Testing

```
Tests run: 28, Failures: 0, Errors: 0, Skipped: 0
```

| Test Class | Coverage |
|---|---|
| `AnomalyFactoryTest` | Factory creation, input normalization, exception shielding |
| `AnomalyChainTest` | Pipeline flow, severity escalation, abort trigger, action log |
| `CompositeTest` | Status propagation, leaf/node distinction |
| `SpacecraftTest` | State transitions, iterator count, degrade/restore |
| `BlackBoxObserverTest` | Event recording, report generation |
| `MementoTest` | Snapshot creation, caretaker ordering, null safety |

---

## ⚠️ Known Limitations

- **No real-time telemetry update** — `Spacecraft.tick()` exists but is not called on a timer; telemetry values update only on explicit phase transitions.
- **Single-threaded** — the event bus is not thread-safe (`ArrayList` instead of `CopyOnWriteArrayList`). Suitable for single-user CLI; would need refactoring for concurrent use.
- **In-memory Memento** — snapshots are lost when the application exits; no persistence to disk.
- **No undo/restore** — Memento snapshots are read-only; restoring spacecraft state from a snapshot is not implemented.
- **Fixed anomaly types** — adding a new anomaly type requires modifying `AnomalyFactory` source code; a configuration-driven approach would be more extensible.

---

## 🔭 Future Work

- **Multithreading** — background `tick()` thread for live telemetry updates
- **Memento persistence** — serialize snapshots to JSON/disk for post-mission analysis
- **Undo/restore** — allow rolling back spacecraft state to a previous snapshot
- **Inversion of Control** — dependency injection container for observer wiring
- **Stream API expansion** — anomaly statistics using `Collectors.groupingBy()`
- **Mockito tests** — mock `MissionEventBus` to verify observer interactions in isolation
- **Configuration file** — anomaly types and severity rules loaded from external properties file

---

## 👤 Author

**Gianluca Zappalà**  
Epicode Institute of Technology · Java SE · 2026

---

*Built with ☕ Java 21 · Tested with JUnit 5 · Documented with love*
