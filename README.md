# BTweaks: Modular HUD & UI Utilities
<p align="center" style="text-align: center;">
  <a href="https://github.com/IFirstTeaLover/btweaks"><img src="https://raw.githubusercontent.com/IFirstTeaLover/icons/refs/heads/main/modrinth.discord.png" alt="Github" style="margin: 5px 10px; height: 64px;"></a>
  <a href="https://discord.gg/HSfM6xXse"><img src="https://raw.githubusercontent.com/IFirstTeaLover/icons/refs/heads/main/modrinth.github.png" alt="Discord" style="margin: 5px 10px; height: 64px;"></a>
</p>

BTweaks is a comprehensive general utility mod designed specifically for Fabric 1.21.11 that introduces a suite of customizable UI widgets and functional features. The project specifically adds a modular rendering system capable of displaying real-time HUD elements and information widgets, which are seamlessly managed through a custom, integrated configuration window.

You should download btweaks if you want a highly personalizable interface that allows you to toggle and position HUD elements to fit your specific playstyle. Whether you are looking for better data visibility or a more organized screen layout.

# List of Current Widgets:
1. FPS Display - Shows your FPS without F3
2. RAM Usage Display - Shows your memory usage without F3
3. Keystrokes
4. Ping Display - Shows your ping
5. Coordinates Display - Shows your position without F3
6. GPU Utilization Display*
7. Hit Detector - Shows can/can't hit on living entities if pointing at them within 10 blocks radius

# Additional Information:

Version Compatibility: This mod is currently developed only for Fabric 1.21.11.

Development Status: The project is in its very early development stages; expect bugs, frequent updates and potential changes to features.

# Setup

To fork/contribute:

1. Open a terminal and clone both repos side by side:
   git clone https://github.com/IFirstTeaLover/btweaks.git
   git clone https://github.com/IFirstTeaLover/brapi.git

2. Open run.bat and update the paths to match where you cloned them.

3. Run run.bat to launch the dev client. You will only need to do that once to build dependents, after that just use ./gradlew runClient

4. To build: ./gradlew build

Requirements: JDK 21, Git

## License
    btweaks, foss tweaks to minecraft client
    Copyright (C) 2026 BSprout

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
