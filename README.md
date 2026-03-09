# JavaFX 2D Game Engine (Pokémon Inspired)

A high-performance, top-down 2D game engine built from scratch using **JavaFX**. This project demonstrates advanced software engineering principles applied to game development, including custom rendering pipelines, state management, and entity-component-like architectures.

## 📺 Gameplay Demo

![Demo](https://github.com/Staz0r/JavaFX-2D-Game-Engine/raw/refs/heads/main/Demo.mp4)

<div align="center">
  <video src="https://github.com/Staz0r/JavaFX-2D-Game-Engine/raw/refs/heads/main/Demo.mp4" width="100%" controls></video>
</div>

## 🛠️ Technical Highlights

### 1. High-Performance Game Loop
Implemented a fixed-timestep game loop using `javafx.animation.AnimationTimer`. 
- **Delta Timing:** Ensures consistent game logic updates (60 FPS) independent of the rendering frame rate.
- **Pixel-Perfect Rendering:** Utilizes `GraphicsContext` with `setImageSmoothing(false)` to maintain crisp, high-fidelity pixel art aesthetics at scaled resolutions.

### 2. Robust State Machine
Managed through a centralized `gameState` controller in `GamePanel.java`. The engine seamlessly transitions between:
- `TITLE_STATE`: Main menu and splash screens.
- `PLAY_STATE`: Active world exploration and physics updates.
- `DIALOGUE_STATE`: Interacting with NPCs via a dedicated `DialogueHandler`.
- `PAUSE_STATE` & `MENU_STATE`: UI overlays and game configuration.

### 3. Dynamic Camera & World System
- **Camera Decoupling:** Implemented a `Camera` class that translates world coordinates to screen space, allowing for massive world maps (50x50 tiles and beyond).
- **Floor Management:** A `FloorManager` system that handles multi-layered environments and transitions.
- **Tile Mapping:** Efficient tile-based rendering with collision data integrated into the `TileManager`.

### 4. Entity & Interaction Systems
- **Inheritance-Based Entities:** An extensible `Entity` class system for Players and NPCs, featuring automated movement patterns and state-based animations.
- **Collision Detection:** A specialized `CollisionHandler` that manages axis-aligned bounding box (AABB) checks between the player, NPCs, and the environment.
- **Event System:** An `EventHandler` that triggers specific game logic (like healing or map transitions) based on player coordinates.

## 🚀 Learning Outcomes
- **Advanced OOP:** Heavily utilized inheritance and polymorphism to create a reusable entity system.
- **Resource Management:** Developed custom loaders for audio (`javax.sound.sampled`), fonts, and spritesheets.
- **UI/UX Engineering:** Built a custom UI layer (`GameUI`) from scratch, including dynamic text typing effects and menu navigation.

## 📂 Project Structure
- `src/application`: JavaFX entry point and Stage configuration.
- `src/entity`: Player and NPC logic.
- `src/game`: Core engine components (Loop, Camera, Collision, Sound).
- `src/tile`: World-building and map management.
- `src/ui`: Game overlays and heads-up display.
- `res/`: Asset pipeline for textures, audio, and fonts.

## ⚖️ Disclaimer
This project is an **educational study** in game engine architecture. Pokémon assets (sprites, audio, and branding) are the intellectual property of Nintendo/The Pokémon Company. This project is intended as a non-commercial portfolio piece to demonstrate technical proficiency in Java.
