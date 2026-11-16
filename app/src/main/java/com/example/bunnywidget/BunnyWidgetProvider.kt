package com.example.bunnywidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.widget.RemoteViews

class BunnyWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        super.onUpdate(context, manager, ids)
        try {
            context.assets.open("frames/frame_001.png").use { stream ->
                val bitmap = BitmapFactory.decodeStream(stream)
                ids.forEach { id ->
                    val views = RemoteViews(context.packageName, R.layout.widget_bunny)
                    views.setImageViewBitmap(R.id.bunny_image, bitmap)
                    manager.updateAppWidget(id, views)
                }
            }
        } catch (_: Exception) { }
    }

    override fun onDisabled(context: Context) {
        context.stopService(Intent(context, AnimationService::class.java))
    }

    companion object {
        fun widgetIds(context: Context): IntArray =
            AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, BunnyWidgetProvider::class.java))
    }
}
