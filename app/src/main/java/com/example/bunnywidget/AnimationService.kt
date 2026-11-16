package com.example.bunnywidget

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import android.widget.RemoteViews

class AnimationService : Service() {
    private val handler = Handler(Looper.getMainLooper())
    private val frames = mutableListOf<Bitmap>()
    private var running = false
    private var index = 0
    private var nextFrameAt = 0L
    private val frameDelay = 125L // 8 fps, moderate battery use

    private val tick = object : Runnable {
        override fun run() {
            if (!running) return
            val ids = BunnyWidgetProvider.widgetIds(this@AnimationService)
            if (ids.isEmpty()) {
                stopSelf()
                return
            }
            if (frames.isNotEmpty()) {
                val views = RemoteViews(packageName, R.layout.widget_bunny)
                views.setImageViewBitmap(R.id.bunny_image, frames[index])
                AppWidgetManager.getInstance(this@AnimationService).updateAppWidget(ids, views)
                index = (index + 1) % frames.size
            }
            nextFrameAt += frameDelay
            handler.postDelayed(this, (nextFrameAt - SystemClock.uptimeMillis()).coerceAtLeast(10L))
        }
    }

    override fun onCreate() {
        super.onCreate()
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(NotificationChannel("bunny_animation", "Bunny animation", NotificationManager.IMPORTANCE_LOW))
        val launch = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
        val notification = Notification.Builder(this, "bunny_animation")
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Bunny Widget")
            .setContentText("Animated widget is running")
            .setContentIntent(launch)
            .setOngoing(true).build()
        startForeground(1001, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "STOP") { stopSelf(); return START_NOT_STICKY }
        if (frames.isEmpty()) {
            val names = assets.list("frames")?.filter { it.endsWith(".png") }?.sorted().orEmpty()
            for (name in names) {
                assets.open("frames/$name").use { frames.add(BitmapFactory.decodeStream(it)) }
            }
        }
        if (!running) {
            running = true
            nextFrameAt = SystemClock.uptimeMillis()
            handler.post(tick)
        }
        return START_STICKY
    }

    override fun onDestroy() {
        running = false
        handler.removeCallbacks(tick)
        frames.forEach { it.recycle() }
        frames.clear()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
