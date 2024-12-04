package com.example.dopaminho

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val serviceClass = UsageStatService::class.java

        if(!isServiceRunning(context, serviceClass)) {
            val inIntent = Intent(context, serviceClass)
            context.startService(inIntent)
        }

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

        @SuppressLint("ServiceCast")
        private fun isServiceRunning (context: Context, serviceClass: Class<*>): Boolean {
            val manager = context.getSystemService(Context.ACCOUNT_SERVICE) as ActivityManager
            return manager.getRunningServices(Int.MAX_VALUE).any { it.service.className == serviceClass.name }
        }
    }

}