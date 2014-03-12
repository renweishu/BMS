package com.nttdata.chongqing.bms.downloadpage.service;

import com.nttdata.chongqing.bms.R;
import com.nttdata.chongqing.bms.newbookonline.activity.NewBookOnlineActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * @author chao11.ma
 *
 */
public class DownloadReceiver extends BroadcastReceiver {
	private NotificationManager mNotificationManager;
	private RemoteViews mRemoteViews;
	private Intent mIntent;
	private PendingIntent mPendingIntent;
	public Handler mHandler;
	@Override
	public void onReceive(Context context, Intent intent) {
		int progress=intent.getIntExtra("pro", 0);
		int id=intent.getIntExtra("id", 0);
		final Notification notification = new Notification();
		mNotificationManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
		mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.download);
		// 设置图标
		notification.icon = R.drawable.face;
		notification.when=id;
		notification.flags=Notification.FLAG_AUTO_CANCEL;
		mRemoteViews.setImageViewResource(R.id.image_download,
				R.drawable.download);
		/**
		 * 单击Notification时发出的Intent消息
		 */
		mIntent = new Intent(context, NewBookOnlineActivity.class);
		mPendingIntent = PendingIntent.getActivity(context, 0,
				mIntent, 0);
		mRemoteViews.setProgressBar(R.id.progress_down, 100,
				progress, false);
		mRemoteViews.setTextViewText(R.id.text_download,
				progress + "%");
		notification.contentView = mRemoteViews;
		notification.contentIntent = mPendingIntent;
		mNotificationManager.notify(id, notification);
		if (progress>=98) {
			Toast.makeText(context, "hahaha", Toast.LENGTH_SHORT);
		}

	}
}
