package main;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser;

public class IdleTime {

    public static long getIdleMillis() {
        WinUser.LASTINPUTINFO lastinputinfo = new WinUser.LASTINPUTINFO();
        User32.INSTANCE.GetLastInputInfo(lastinputinfo);
        int currentTick = Kernel32.INSTANCE.GetTickCount();
        return currentTick - lastinputinfo.dwTime;
    }
}
