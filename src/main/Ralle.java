package main;

import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Ralle {
    public static void main(String[] args) throws AWTException, InterruptedException {

        Robot robot = new Robot();
        SystemTray tray = SystemTray.getSystemTray();

        Image image = Toolkit.getDefaultToolkit().getImage(Ralle.class.getResource("/icon.png"));

        TrayIcon trayIcon = new TrayIcon(image, "RALL-E is running");
        trayIcon.setImageAutoSize(true);

        PopupMenu popup = new PopupMenu();
        trayIcon.setPopupMenu(popup);
        tray.add(trayIcon);

        AtomicBoolean lunchMode = new AtomicBoolean(true);
        boolean tookLunchBreak = false;

        CheckboxMenuItem lunchModeItem = new CheckboxMenuItem("Lunch Mode", lunchMode.get());
        lunchModeItem.addItemListener(e -> lunchMode.set(lunchModeItem.getState()));

        // Lunch Mode Duration
        AtomicInteger lunchDurationMinutes = new AtomicInteger(30);
        Menu durationMenu = new Menu("Lunch Mode Duration");
        int[] durationOptions = { 15, 30, 45, 60 };
        CheckboxMenuItem[] durationItems = new CheckboxMenuItem[durationOptions.length];

        for (int i = 0; i < durationOptions.length; i++) {
            int minutes = durationOptions[i];
            // second constructor argument pre-checks the box only if this option matches the current default (30)
            CheckboxMenuItem item = new CheckboxMenuItem(minutes + " min", minutes == lunchDurationMinutes.get());
            item.addItemListener(e -> {
                lunchDurationMinutes.set(minutes);
                for (CheckboxMenuItem other : durationItems) {
                    other.setState(other == item);
                }
            });
            durationItems[i] = item;
            durationMenu.add(item);
        }

        // End Time
        AtomicInteger endHour = new AtomicInteger(17);
        Menu endTimeMenu = new Menu("End Time");
        int[] endHourOptions = { 14, 15, 16, 17 };
        CheckboxMenuItem[] endHourItems = new CheckboxMenuItem[endHourOptions.length];

        for (int i = 0; i < endHourOptions.length; i++) {
            int hour = endHourOptions[i];
            String label = LocalTime.of(hour, 0).format(DateTimeFormatter.ofPattern("h:mm a"));
            CheckboxMenuItem item = new CheckboxMenuItem(label, hour == endHour.get());
            item.addItemListener(e -> {
                endHour.set(hour);
                for (CheckboxMenuItem other : endHourItems) {
                    other.setState(other == item);
                }
            });
            endHourItems[i] = item;
            endTimeMenu.add(item);
        }

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));

        // RALl-E menu options
        popup.add(lunchModeItem);
        popup.add(durationMenu);
        popup.add(endTimeMenu);
        popup.addSeparator();
        popup.add(exitItem);

        while (true) {
            Point currentPosition = MouseInfo.getPointerInfo().getLocation();
            int x = currentPosition.x;
            int y = currentPosition.y;

            LocalTime currentTime = LocalTime.now();
            int currentHour = currentTime.getHour();

            if (currentHour >= endHour.get()) {
                break;
            }

            // if lunchMode is activated -> RALL-E will take a break
            if (currentHour == 12 && lunchMode.get() && !tookLunchBreak) {
                tookLunchBreak = true;
                TimeUnit.MINUTES.sleep(lunchDurationMinutes.get());
            }

            robot.mouseMove(x + 1, y + 1);
            TimeUnit.MINUTES.sleep(4);
        }
        System.exit(0);
    }
}