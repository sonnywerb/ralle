RALLE:
Ready And Looking LivE

Ralle is a lightweight Windows tray app that keeps your PC active so you don't have to think about it.

## Installation

1. Download latest version of the installer `.exe` from Releases.
2. After downloading, run the installer
3. Select whether you want to create start menu/desktop shortcut
4. Ralle will be installed in `C:\Users\<user>\AppData\Local\RALLE`

> **Note:** if you have a version older than 1.1.0 already installed, uninstall it first
> (Settings > Apps > Ralle > Uninstall) before installing the new version. Versions
> before 1.1.0 don't share the upgrade identifier used by newer installers, so the
> new installer can't automatically detect/replace them. From 1.1.0 onward, upgrades
> will be detected and applied automatically.

## Usage

- Run `Ralle.exe` to start the program
- There will be a system tray icon present if it's running; hover over it to see the
  current status (Active, Paused, On Lunch Break, or Stopped for the day)
- Right-click the tray icon for options:
    - **Work Mode** - only nudges the mouse when the system has been idle for a while,
      instead of nudging constantly, so it doesn't interfere while you're actively working
    - **Lunch Mode** - take a break around noon instead of nudging the mouse
    - **Lunch Mode Duration** - how long the lunch break lasts (15/30/45/60 min)
    - **End Time** - what time Ralle stops for the day (2:00 PM - 5:00 PM)
    - **Exit** - stop the program
- Settings are remembered between runs

