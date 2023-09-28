<p align="center">
 <img src="src/main/resources/minkswitch.png" height="500" width="500"/>

<h1 align="center">Mink Switcher</h1>

<p align="center"> Mink Switcher is a Minecraft Addon for the popular <a href="https://www.curseforge.com/minecraft/mc-mods/mine-mine-no-mi"> Mine Mine no Mi Mod</a> mod.</p>

## Installation

```
git clone https://github.com/rathmerdominik/MineMineNoMiMinkSwitcher.git
cd MineMineNoMiMinkSwitcher
curl -L https://www.curseforge.com/api/v1/mods/78726/files/4682386/download -o mine-mine-no-mi-1.16.5-0.9.5.jar
mv mine-mine-no-mi-1.16.5-0.9.5.jar libs
./gradlew build
```

From there you can take the jar out of the `build/libs` folder

## Configuration Options

```
#Defines if a player can switch his mink race again after he switched once.
"Allow switching again" = false
```