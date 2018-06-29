package com.sanxynet.journalapp;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    long todoId;
    Journal caseJournal;
    JournalDao journalDao;

    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(final Context context, Intent intent) {
        todoId = intent.getLongExtra(IntentConstants.JOURNAL,-1);

        JournalDatabase database = JournalDatabase.getInstance(context);
        journalDao = database.journalDao();

        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.large_icon);

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                caseJournal = journalDao.getTodoById(todoId);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setContentTitle(caseJournal.getJournalName())
                        .setContentText(caseJournal.getJournalDesc())
                        .setDefaults(NotificationCompat.DEFAULT_LIGHTS|NotificationCompat.DEFAULT_SOUND)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setSmallIcon(R.drawable.small_icon)
                        .setLargeIcon(bitmap)
                        .setAutoCancel(true);

                Intent resultIntent = new Intent(context,MainActivity.class);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) todoId,resultIntent,
                        PendingIntent.FLAG_ONE_SHOT);

                builder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager) context.
                        getSystemService(context.NOTIFICATION_SERVICE);
                notificationManager.notify((int) todoId,builder.build());

            }
        }.execute();
    }

}
