package main;

import static main.Defaults.END_HOUR;
import static main.Defaults.LUNCH_DURATION_MINUTES;
import static main.Defaults.LUNCH_MODE;
import static main.Defaults.WORK_MODE;

import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.prefs.Preferences;

public class Ralle {
    public static void main(String[] args) throws AWTException, InterruptedException {

        // --- Setup ---
        KeepAwake.enable();
        Robot robot = new Robot();
        SystemTray tray = SystemTray.getSystemTray();
        Preferences prefs = Preferences.userNodeForPackage(Ralle.class);

        Image image = Toolkit.getDefaultToolkit().getImage(Ralle.class.getResource("/icon.png"));
        TrayIcon trayIcon = new TrayIcon(image, "RALLE is running");
        trayIcon.setImageAutoSize(true);

        PopupMenu popup = new PopupMenu();
        trayIcon.setPopupMenu(popup);
        tray.add(trayIcon);

        // --- Persisted state ---
        AtomicBoolean lunchMode = new AtomicBoolean(prefs.getBoolean("lunchMode", LUNCH_MODE));
        AtomicInteger lunchDurationMinutes = new AtomicInteger(
                prefs.getInt("lunchDurationMinutes", LUNCH_DURATION_MINUTES));
        AtomicInteger endHour = new AtomicInteger(prefs.getInt("endHour", END_HOUR));
        AtomicBoolean workMode = new AtomicBoolean(prefs.getBoolean("workMode", WORK_MODE));
        boolean tookLunchBreak = false;

        // --- Menu items ---
        CheckboxMenuItem workModeItem = new CheckboxMenuItem("Work Mode", workMode.get());
        workModeItem.addItemListener(e -> {
            workMode.set(workModeItem.getState());
            prefs.putBoolean("workMode", workMode.get());
        });

        CheckboxMenuItem lunchModeItem = new CheckboxMenuItem("Lunch Mode", lunchMode.get());
        lunchModeItem.addItemListener(e -> {
            lunchMode.set(lunchModeItem.getState());
            prefs.putBoolean("lunchMode", lunchMode.get());
        });

        Menu durationMenu = buildRadioMenu("Lunch Mode Duration", new int[] { 15, 30, 45, 60 },
                lunchDurationMinutes.get(), minutes -> minutes + " min", minutes -> {
                    lunchDurationMinutes.set(minutes);
                    prefs.putInt("lunchDurationMinutes", minutes);
                });

        Menu endTimeMenu = buildRadioMenu("End Time", new int[] { 14, 15, 16, 17 }, endHour.get(),
                hour -> LocalTime.of(hour, 0).format(DateTimeFormatter.ofPattern("h:mm a")), hour -> {
                    endHour.set(hour);
                    prefs.putInt("endHour", hour);
                });

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> {
            KeepAwake.disable();
            prefs.putInt("endHour", END_HOUR);
            System.exit(0);
        });

        // --- Assemble menu ---
        popup.add(workModeItem);
        popup.add(lunchModeItem);
        popup.add(durationMenu);
        popup.add(endTimeMenu);
        popup.addSeparator();
        popup.add(exitItem);

        // --- Main loop ---
        while (true) {
            Point currentPosition = MouseInfo.getPointerInfo().getLocation();
            int x = currentPosition.x;
            int y = currentPosition.y;

            LocalTime currentTime = LocalTime.now();
            int currentHour = currentTime.getHour();

            if (currentHour >= endHour.get()) {
                trayIcon.setToolTip("RALLE - Stopped for the day");
                break;
            }

            // if lunchMode is activated -> RALLE will take a break
            if (currentHour == 12 && lunchMode.get() && !tookLunchBreak) {
                tookLunchBreak = true;
                LocalTime breakEnd = currentTime.plusMinutes(lunchDurationMinutes.get());
                trayIcon.setToolTip(
                        "RALLE - On Lunch Break until " + breakEnd.format(DateTimeFormatter.ofPattern("h:mm a")));
                TimeUnit.MINUTES.sleep(lunchDurationMinutes.get());
            }

            if (!workMode.get() || IdleTime.getIdleMillis() > 120_000) {
                robot.mouseMove(x + 1, y + 1);
                trayIcon.setToolTip("RALLE - Active");
            } else {
                trayIcon.setToolTip("RALLE - Paused (you're active)");
            }
            TimeUnit.SECONDS.sleep(270);
        }
        KeepAwake.disable();
        prefs.putInt("endHour", END_HOUR);
        System.exit(0);
    }

    private static Menu buildRadioMenu(String title, int[] options, int currentValue, IntFunction<String> labelFn,
            IntConsumer onSelect) {

        Menu menu = new Menu(title);
        CheckboxMenuItem[] menuItems = new CheckboxMenuItem[options.length];

        for (int i = 0; i < options.length; i++) {
            int value = options[i];
            CheckboxMenuItem item = new CheckboxMenuItem(labelFn.apply(value), value == currentValue);
            item.addItemListener(e -> {
                onSelect.accept(value);
                for (CheckboxMenuItem other : menuItems) {
                    other.setState(other == item);
                }
            });
            menuItems[i] = item;
            menu.add(item);
        }
        return menu;
    }
}