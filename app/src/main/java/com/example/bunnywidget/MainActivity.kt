package com.example.bunnywidget

import android.Manifest
import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 64, 40, 40)
        }
        val title = TextView(this).apply { text = "Bunny Widget"; textSize = 26f }
        val info = TextView(this).apply {
            text = "1×1 transparent animated bunny widget. Add it to your home screen, then tap Start. The notification is needed for continuous animation."
            textSize = 16f
            setPadding(0, 20, 0, 30)
        }
        val add = Button(this).apply {
            text = "Add 1×1 widget"
            setOnClickListener {
                val manager = getSystemService(AppWidgetManager::class.java)
                val component = ComponentName(this@MainActivity, BunnyWidgetProvider::class.java)
                if (manager.isRequestPinAppWidgetSupported) {
                    manager.requestPinAppWidget(component, null, null)
                } else Toast.makeText(this@MainActivity, "Long-press home screen → Widgets → Bunny Widget", Toast.LENGTH_LONG).show()
            }
        }
        val start = Button(this).apply {
            text = "Start animation"
            setOnClickListener {
                if (BunnyWidgetProvider.widgetIds(this@MainActivity).isEmpty()) {
                    Toast.makeText(this@MainActivity, "Add the widget first", Toast.LENGTH_SHORT).show()
                } else {
                    if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 20)
                    }
                    try {
                        startForegroundService(Intent(this@MainActivity, AnimationService::class.java))
                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, "Cannot start: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        val stop = Button(this).apply {
            text = "Stop animation"
            setOnClickListener { stopService(Intent(this@MainActivity, AnimationService::class.java)) }
        }
        layout.addView(title); layout.addView(info); layout.addView(add); layout.addView(start); layout.addView(stop)
        setContentView(layout)
    }
}
