package com.ahmadreduan.amitumi;

import static androidx.core.app.ActivityCompat.requestPermissions;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.ahmadreduan.amitumi.Models.MessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FirebaseService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("FCM", "Message Received: " + remoteMessage.getData());


        if (remoteMessage.getData().size() > 0) {
            String uId = remoteMessage.getData().get("uId");
            String message = remoteMessage.getData().get("message");
            Long timestamp = Long.parseLong(remoteMessage.getData().get("timestamp"));

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(() -> {
                saveMessageToDatabase(new MessageModel(uId, timestamp, message));
            });
        }






        String title = "New Message";
        String body = "You have received a new message";

        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }

        showNotification(title, body);
    }


    private void saveMessageToDatabase(MessageModel messageModel) {
        // Save the message to Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("messages");
        ref.push().setValue(messageModel);
    }


    private void showNotification(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "default_channel";
            CharSequence channelName = "General Notifications";
            String channelDescription = "All app notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);
            channel.enableLights(true);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        Log.d("NotificationDebug", "Notification Channel Created");



        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default_channel")
                .setSmallIcon(R.drawable.pencil)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);


        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());

    }


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM Token", token);

        // 🔹 Firebase-এ ইউজারের FCM টোকেন আপডেট করুন
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        ref.child("fcmToken").setValue(token);
    }
}
