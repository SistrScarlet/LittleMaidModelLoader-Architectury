# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
- Multi-platform Minecraft mod (Fabric/Forge) using Architectury
- Core: Multi-model system for entity rendering with dynamic external resource loading
- Client: Custom GUI system for model/sound selection with filtering and scrolling capabilities
- External resource loading from `LMMLResources/` folder in game directory

## Development Guidelines
- Optionalは返り値にのみ使用する
- nullableな場合は@Nullableを使用する（org.jetbrains.annotations.Nullable）
- gradlewコマンドは使用禁止

## Key Directories
- **common/**: Shared platform code using Architectury
- **fabric/**: Fabric-specific implementation and entry points
- **forge/**: Forge-specific implementation and entry points
