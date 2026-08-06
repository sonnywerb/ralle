package main;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;

public class KeepAwake {

    public static void enable() {
        Kernel32.INSTANCE.SetThreadExecutionState(
                WinBase.ES_CONTINUOUS | WinBase.ES_SYSTEM_REQUIRED | WinBase.ES_DISPLAY_REQUIRED);
    }

    public static void disable() {
        Kernel32.INSTANCE.SetThreadExecutionState(WinBase.ES_CONTINUOUS);
    }
}
