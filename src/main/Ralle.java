package main;

import java.awt.*;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class Ralle {
    public static void main(String[] args) throws AWTException, InterruptedException {

        Robot robot = new Robot();
        SystemTray tray = SystemTray.getSystemTray();

        Image image = Toolkit.getDefaultToolkit().getImage(Ralle.class.getResource("/icon.png"));

        TrayIcon trayIcon = new TrayIcon(image, "RALLE is running");
        trayIcon.setImageAutoSize(true);

        PopupMenu popup = new PopupMenu();
        MenuItem exitItem = new MenuItem("Stop RALLE");
        exitItem.addActionListener(e -> System.exit(0));
        popup.add(exitItem);
        trayIcon.setPopupMenu(popup);

        tray.add(trayIcon);

        System.out.println("Starting RALLE.");
        while (true) {
            Point currentPosition = MouseInfo.getPointerInfo().getLocation();
            int x = currentPosition.x;
            int y = currentPosition.y;

            LocalTime currentTime = LocalTime.now();
            int currentHour = currentTime.getHour();

            if (currentHour >= 17) {
                break;
            }
            if (currentHour == 12) {
                System.out.println("It's noon! Taking a break.");
                TimeUnit.MINUTES.sleep(30);
            }
            robot.mouseMove(x + 1, y + 1);
            TimeUnit.MINUTES.sleep(4);
        }

        System.out.println("Ending RALLE.");
        System.exit(0);
    }
}