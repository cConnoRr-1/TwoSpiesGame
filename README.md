# Undercover — Two Spies Game: Project Description

This document gives a detailed description of the **Undercover** two-player spy game: how the game works, what frameworks and tools are used, and how the project is structured and coded.

---

## 1. Project Overview

**Undercover** is a turn-based, two-player strategy game where each player controls a spy on a world map. The goal is to eliminate the opponent’s spy (by attacking when on the same location) or to win three “rounds” (missions) before the other player. The application is split into:

- **Server application** — Runs the game logic, keeps the authoritative state, and sends updates to the client.
- **Client application** — Connects to the server, displays the map and UI, and sends the player’s actions.

Both are **desktop JavaFX applications** that communicate over **TCP sockets** using a simple text-based protocol. The server hosts one game and accepts a single client (1v1).

---

## 2. How the Game Works

### 2.1 Setup and Connection

1. **Server** is started first. On the **Profile** tab, the host sets a **port** (e.g. 2015) and clicks **Connect**. The server then listens for one client connection.
2. **Client** is started. On the **Profile** tab, the player enters **host** (e.g. `localhost`) and the **same port**, then clicks **Connect**.
3. After a successful TCP connection, both switch to the **game tab** (“Untitled Tab 1”) and see a **Ready?** screen.
4. When **both** have clicked **Ready?**, the game initializes: the map loads, both spies are placed at random cities, and the first turn is chosen at random.

### 2.2 The Map and Locations

The game uses a **world map** with **11 cities** (positions):

- New York, London, Moscow, Madrid, Berlin, Chicago, San Francisco, Cancun, Paris, Geneva, Nigeria.

Cities are connected in a **graph**: from each city you can only move to certain neighboring cities (e.g. New York ↔ London, New York ↔ Madrid). Movement is restricted to these edges so players cannot jump arbitrarily.

### 2.3 Turn System

- **Turns** alternate between **Player 1 (MP — “My Player” on server)** and **Player 2 (OP — “Other Player,” the client)**.
- The server uses a **turn timer** (e.g. 20 seconds per turn), implemented with a JavaFX `AnimationTimer` that ticks roughly every second. When time runs out, the turn switches to the other player and their timer resets.
- The server decides whose turn it is (`playerTurn` counter) and only accepts actions from the active player; the client is told when it’s “Player1 turn” or “Player2 turn” and can see an overlay when it’s not their turn.

### 2.4 Resources: Energy and Intel Points

- **Energy** — Each turn, the active player has a limited amount of energy (e.g. 2). Moving and other actions cost energy. When energy reaches 0, the turn ends and switches to the other player; energy is refilled at the start of the next turn.
- **Intel Points** — Earned over time (e.g. +10 for a move, plus possible bonuses). Used to pay for **special abilities** (Locate, Plan, etc.). If the player doesn’t have enough intel, the ability is refused and a message is shown.

### 2.5 Actions

Players can perform these actions (when it’s their turn):

| Action   | Cost (Intel) | Description |
|----------|----------------|-------------|
| **Move** | 0             | Move to an adjacent city on the map. Uses energy. |
| **Control** | 0          | Claim the current city. If the other player controlled it, control is taken from them. The city button is colored (e.g. red for P1, green for P2). |
| **Wait** | 0              | End turn without moving; still uses energy and triggers turn switch when energy is 0. |
| **Attack** | 0             | If both spies are in the **same city**, the opponent is eliminated for this round and the attacker gets a win. Otherwise the attack fails and a message is sent. |
| **Locate** | 20            | Reveal the opponent’s current position to the server (and the server can inform the client “You have been located!”). |
| **Plan**  | 40            | Spend intel to “plan” an attack (e.g. bonus or narrative effect). |
| **Hide**  | 30            | Defined in abilities list but usage in logic may be limited. |

All authoritative checks (movement allowed, same location, control, intel/energy) are done on the **server**. The client sends the chosen action (e.g. move to city 3, attack, control) and the server validates, updates state, and broadcasts results.

### 2.6 Round and Match Victory

- **Round** — When one spy **eliminates** the other (Attack while on the same city), that round ends. The eliminator gets **+1 win**. The result screen shows “Win” or “Lost” and current win counts. Players can **Continue** to the next round (spies respawn at random cities) or **Quit** to return to the Ready screen.
- **Match** — First player to win **3 rounds** wins the whole match. A **final result** screen shows “Winner” or “Lost,” and missions/games won. After that, players can click **Okay** to go back to the Ready screen and play again (new connection/session as designed).

### 2.7 Chat and Messages

- **Chat** — Players can type in a text field and send messages. The server relays them with a `message` prefix so the other side sees them in a “Messages received” / list area.
- **System messages** — The server also sends prefixed messages for events like “You have been located!”, “Attack has been attempted!”, “Not enough intelPoints!”, etc.

---

## 3. Frameworks and Technologies

### 3.1 Language and Runtime

- **Java 17** — Used for both client and server (compiler `source`/`target` set to 17 in the Maven POMs).

### 3.2 UI: JavaFX

- **JavaFX 17** — Used for all graphical interfaces:
  - **javafx-controls** — Buttons, labels, text fields, list views, etc.
  - **javafx-fxml** — Layout and structure are defined in **FXML** files (`hello-view.fxml`), and the **controller** classes (`HelloController`) handle events and bindings.

So the framework for the desktop UI is **JavaFX** (controls + FXML), not Swing or a web stack.

### 3.3 Build: Maven

- **Apache Maven** — Build and dependency management:
  - **maven-compiler-plugin** — Compiles Java 17.
  - **javafx-maven-plugin** — Runs the apps with `mvn javafx:run` (or `./mvnw javafx:run` with the wrapper).

Each of **ClientFinalProject** and **ServerFinalProject** is a separate Maven module (its own `pom.xml`). There is no parent POM; they are independent projects that share the same repo.

### 3.4 Maven Wrapper

- **Maven Wrapper** (`mvnw` + `.mvn/wrapper/`) — Included in both client and server so the project can be built and run with `./mvnw compile` and `./mvnw javafx:run` without installing Maven globally.

### 3.5 Networking

- **Java Sockets (java.net)** — No extra framework:
  - **Server:** `ServerSocket` to accept one client; then a single `Socket` for that client.
  - **Client:** `Socket` + `InetSocketAddress` to connect to the server.

Communication is **plain TCP** with **line-based text** (each message is a line of characters, newline-terminated). There is no HTTP, WebSockets, or external networking library.

### 3.6 Testing (Declared)

- **JUnit 5** (Jupiter) — Declared in the POMs for unit tests (`junit-jupiter-api`, `junit-jupiter-engine`). The project is set up for testing even if few or no tests are present in the repo.

### 3.7 Module System

- **Java Platform Module System (JPMS)** — Both apps use `module-info.java`:
  - **Client module:** `com.example.clientfinalproject` — requires `javafx.controls`, `javafx.fxml`, `java.desktop`, `java.logging`; opens the controller package to JavaFX FXML.
  - **Server module:** `com.example.serverfinalproject` — same JavaFX and logging; same idea for FXML.

So the project is coded as **modular Java** with explicit module boundaries.

---

## 4. How the Project Is Coded

### 4.1 High-Level Architecture

- **Client** and **Server** are two separate runnable applications. Each has:
  - A **main class** (`HelloApplication`) that loads the primary FXML and shows the window.
  - A **controller** (`HelloController`) that holds UI references, game state (on server), and network callbacks.
  - **Socket layer** — On both sides, a small **socketfx** package provides:
    - **GenericSocket** — Abstract base: background threads for connect/accept and for reading lines; `sendMessage(String)`; `SocketListener` callbacks.
    - **FxSocketClient** / **FxSocketServer** — Concrete implementations that use `Platform.runLater(...)` so all `onMessage` and `onClosedStatus` callbacks run on the **JavaFX thread**, avoiding UI updates from worker threads.

So the structure is: **JavaFX UI ↔ Controller ↔ Socket layer (GenericSocket / FxSocketClient or FxSocketServer) ↔ TCP**.

### 4.2 Client-Side Structure

- **Packages:**
  - `com.example.clientfinalproject` — `HelloApplication`, `HelloController`, `Player` (simplified DTO for display).
  - `socketfx` — `FxSocketClient`, `GenericSocket`, `SocketListener`, `Constants`.

- **HelloController** responsibilities:
  - **Profile tab:** Connect (host/port), connect button handler.
  - **Game tab:** Ready button; map and city buttons (move); control, wait, attack, locate, plan; chat; result/continue/quit/okay.
  - **FxSocketListener** (inner class): Implements `SocketListener`. In `onMessage(String line)` it parses **server-to-client** protocol (e.g. `POPCLIn3`, `controlMP`, `resultWin`, `message...`) and updates UI and local `Player` state. All parsing is string-based (prefixes and substrings).

- **Player (client)** — Holds only what the client needs to display: name, color, image path, position index, energy, intel, wins, etc. No game rules; state is driven by server messages.

### 4.3 Server-Side Structure

- **Packages:**
  - `com.example.serverfinalproject` — `HelloApplication`, `HelloController`, `Player`, `Position`, `Ability`.
  - `socketfx` — `FxSocketServer`, `GenericSocket`, `SocketListener`, `Constants`.

- **HelloController** responsibilities:
  - **Profile tab:** Start server (port), connect button.
  - **Game tab:** Ready; full game state (two `Player` objects, list of `Position`s, list of `Ability`s); turn and timer; handling of move, control, wait, attack, locate, plan; round/match outcome; sending all updates to the client via `safeSend(...)`.

- **Player (server)** — Full model: name, color, image path, current/previous position (object + index), energy, intel, eliminated flag, wins, controlled positions list, turn timer. Used for all game logic.

- **Position** — Represents a city: name and list of **neighboring** positions (`placesCanGo`). The graph of cities is built in the controller (e.g. New York ↔ London, Madrid, etc.).

- **Ability** — Name and intel cost (e.g. Move 0, Locate 20, Plan 40). Stored in a list and used to check cost and apply effects.

- **FxSocketListener** on the server parses **client-to-server** protocol (e.g. `ready`, `chOP4`, `controlOP`, `attackOP`, `continue`, `quitOP`) and calls the appropriate handler (e.g. `changePositionOP`, `controlC`, `attackC`). All game rules and state changes run on the server.

### 4.4 Communication Protocol (Conceptual)

Messages are **strings**, one per line, with no formal schema. Convention is prefix + payload:

- **Session / flow:** `ready`, `continue`, `quitOP` / `quitMP`.
- **State sync:** `POPCLIn`/`PMPCLIn` + position index; `POPImNam`/`PMPImNam`, `POPNam`/`PMPNam`, `PMPCol`/`POPCol`; `POPE`/`PMPE`, `POPIP`/`PMPIP`; `POPWins`/`PMPWins`.
- **Actions:** `chOP` + index (client move); `controlOP`/`controlMP`; `attackOP`; `waitOP`; `locateOP`; `planOP`.
- **Display / feedback:** `disPOP`/`disPMP` (refresh position); `controlMP`/`controlOP`; `message` + text; `result` + Win/Lost; `finalResult` + Winner/Lose; `showTimeMP`/`showTimeOP`; `samePlace`; `update`; `battleWonOP`; etc.

The **server** is the single source of truth: it validates moves, applies abilities, resolves combat, and pushes updates to the client so both UIs stay in sync.

### 4.5 Concurrency and Threading

- **Socket threads:** `GenericSocket` uses:
  - A **setup thread** — Connects (client) or accepts (server), then creates `BufferedReader`/`BufferedWriter` and notifies the reader thread.
  - A **reader thread** — Blocks on `readLine()`. For each line received, it calls `onMessage(line)`.

- **JavaFX thread:** `FxSocketClient` and `FxSocketServer` wrap `onMessage` and `onClosedStatus` in `Platform.runLater(...)` so the controller only updates UI and state from the JavaFX thread. No shared mutable state is exposed to the socket threads beyond the listener callback.

### 4.6 UI Layout (FXML)

- Each app has **one main FXML** (`hello-view.fxml`) under `src/main/resources` in the app’s package.
  - **Tab 1 — Profile:** Connect button, port (and host on client), labels, intro text.
  - **Tab 2 — Game:** Map `ImageView`, city buttons, player pin ImageViews, control buttons (Control, Wait, Attack), special buttons (Locate, Plan), chat area, list view for messages, energy/intel labels, turn overlay. Overlapping **AnchorPanes** are used for: ready screen, result screen (win/lose, continue/quit), final result screen (match over, okay). Visibility of these panes is toggled in the controller (e.g. `readyAP.setVisible(true/false)`).

- **Controller binding:** FXML uses `fx:controller="...HelloController"` and `fx:id` on nodes so the controller can inject references and use `onAction="#handleReady"`, `#handleConnectButton`, etc.

### 4.7 Resource and Error Handling

- **Images:** Map and player pins are loaded from paths like `src/main/resources/images/worldMap.jpg` and `playerRed.png` / `playerGreen.png`. Loading uses try-with-resources for `FileInputStream` and catches `IOException`.
- **Sockets:** Controllers use a `safeSend(String)` helper that checks `socket != null` (and on server, calls `socket.sendMessage(msg)`) to avoid null pointer exceptions and to avoid sending before the connection is established.
- **Port/host:** Port parsing uses try-catch for `NumberFormatException` and re-enables the connect button or logs a warning if invalid.

---

## 5. Summary Table

| Aspect            | Detail |
|-------------------|--------|
| **Game type**     | Turn-based 1v1 spy strategy on a map; first to 3 round wins. |
| **UI framework**  | JavaFX (controls + FXML). |
| **Build**         | Maven; Java 17; javafx-maven-plugin; Maven Wrapper. |
| **Networking**    | TCP sockets; line-based text protocol; server is authoritative. |
| **Module system** | JPMS (`module-info.java`) for client and server. |
| **Code layout**   | Controller-centric: one main controller per app, inner `SocketListener`, shared socketfx layer; server holds full game model (Player, Position, Ability). |

This should give a clear picture of how the game works, what framework is used (JavaFX + Maven + raw sockets), and how the project is coded (modular Java, FXML UI, controller + socket layer, text protocol).
