# Bunny Widget — Android

A simple **transparent 1×1 home-screen widget** displaying frames from the supplied bunny animation. The frames are bundled, so no video picker is needed.

## Build an APK

1. Install Android Studio (recent version, with Android SDK 35).
2. Open this `BunnyWidget` folder as a project and allow Gradle sync.
3. Select **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
4. Install `app/build/outputs/apk/debug/app-debug.apk` on your Android phone.
5. Open Bunny Widget → **Add 1×1 widget** → **Start animation**.

If the pin-widget dialog is unavailable: long-press home screen → Widgets → Bunny Widget. You may need to manually resize it to 1×1 depending on launcher behavior.

## How it works

Android `RemoteViews` widgets cannot directly render transparent VP9 WebM video. Instead, this project extracts **8 PNG frames per second** from the supplied WebM, preserves their alpha channel, and updates the widget via a foreground service. The loop lasts roughly 14.5 seconds. No network access is required.

**Important limitations:** Widget frame updates are more battery-intensive than static widgets. Some Samsung/One UI versions throttle frequent RemoteViews updates or enforce minimum widget cell sizes. An ongoing notification appears while animation runs. Disable battery optimization for Bunny Widget if the system stops the animation. This project is source code, not a precompiled APK, and has not been tested on a physical device.
