package com.clickitproduct.FCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.clickitproduct.R;
import com.clickitproduct.activities.New_Shop_View_Activity;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService
{
    private static final String TAG = "FirebaseMsgService";
    Bitmap image;
    String title,message,imgpath,flag,shop_id;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        title=remoteMessage.getData().get("title").toString();
        message=remoteMessage.getData().get("message").toString();
        imgpath=remoteMessage.getData().get("imgpath").toString();
        flag=remoteMessage.getData().get("flag").toString();
        shop_id=remoteMessage.getData().get("shop_id").toString();

        image = getBitmapfromUrl(imgpath);
        sendNotification(message,image,flag,shop_id,title);

    }

    private void sendNotification(String messageBody, Bitmap img, String flg, String Shop_Id,String title)
    {
        Intent intent = null;

        if(flg.equals("1"))
        {
            intent = new Intent(this, New_Shop_View_Activity.class);
            intent.putExtra("Shop_Id", Shop_Id);
            intent.putExtra("focusParticularTab", 4);
        }
        else
        {
            intent = new Intent(this, New_Shop_View_Activity.class);
            intent.putExtra("focusParticularTab", 3);
            intent.putExtra("Shop_Id", Shop_Id);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = null;

            notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.small_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.small_icon))
                .setSound(defaultSoundUri)
                .setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(img)
                .setSummaryText(messageBody))
                .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notificationBuilder.build());
    }

    public Bitmap getBitmapfromUrl(String imageUrl)
    {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}
