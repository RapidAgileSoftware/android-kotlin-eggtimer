/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.eggtimernotifications.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.example.android.eggtimernotifications.MainActivity
import com.example.android.eggtimernotifications.R
import com.example.android.eggtimernotifications.receiver.SnoozeReceiver

// Notification ID.
private val NOTIFICATION_ID = 0
// needed to access the pending Intent if we want to update/cancel it
private val REQUEST_CODE = 0
// "one shot" Intent can be only used once ... it disappears when snoozing
private val FLAGS = 0

// extension function to send messages
/**
 * Builds and delivers the notification.
 *
 * @param context, activity context.
 */
fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
    // Create the content intent for the notification, which launches
    // this activity
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    // create PendingIntent
    // PendingIntent.FLAG_UPDATE_CURRENT uses/updates the current intent instead of creating a new one
    val pendingContentfIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    // specify image
    val eggImage = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.cooked_egg
    )
    // create big picture style for notification
    val bigPictureStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(eggImage)
        // large Icon goes away when the notification is expanded
        .bigLargeIcon(null)

    // add snooze Intent
    val snoozeIntent = Intent(applicationContext,SnoozeReceiver::class.java)
    val snoozePendingIntent = PendingIntent.getBroadcast(
        applicationContext,
        REQUEST_CODE,
        snoozeIntent,
        FLAGS
    )

    // Build the notification
    // need to use NotificationCompat.Builder instead NotificationBuilder to support older Android versions
    // pass in context and channel id
    // all notifications need to be assigned to a channel
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.egg_notification_channel_id)
    )
    //set title, text and icon to builder
        .setSmallIcon(R.drawable.cooked_egg)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
    // set content intent
        .setContentIntent(pendingContentfIntent)
    // cancel notification when user taps on it
        .setAutoCancel(true)
        // add big picture style
        .setStyle(bigPictureStyle)
        //set image as largeIcon, so it will be displayes as smaller item when the notification is collapsed
       .setLargeIcon(eggImage)
        // add snooze action
        .addAction(
            R.drawable.egg_icon,
            applicationContext.getString(R.string.snooze),
            snoozePendingIntent
        )
        // set importance manually to support lower api levels, note: use setPriority and NotificationCompat
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    // call notify
    // NOTIFICATION_ID represents the current notification instance
    // since we always have only one notification at a time, we can use the same for all notifications
    notify(NOTIFICATION_ID,builder.build())

}

// Cancel all notifications
fun NotificationManager.cancelNotifications(){
    cancelAll()
}
