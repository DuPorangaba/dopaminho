package com.example.dopaminho

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val inIntent = Intent(context, UsageStatService::class.java)
        context.startService(inIntent)
        setAlarm(context)
    }

    companion object {
        @SuppressLint("ScheduleExactAlarm")
        fun setAlarm(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, FLAG_IMMUTABLE)

            val intervalMillis = 15 * 1000L // 15 segundos
            val triggerTime = System.currentTimeMillis() + intervalMillis

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

}