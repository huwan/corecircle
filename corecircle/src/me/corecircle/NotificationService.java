package me.corecircle;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

public class NotificationService extends Service {


	private NotificationManager myNotiManager;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		super.onStartCommand(intent, flags, startId);

		CallLogHelper dbHelper = new CallLogHelper(getBaseContext());
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		Cursor cursor = db.query(CallLogHelper.MAIN_TABLE, null, null, null,
				null, null, null);
		// 需要提醒的人数
		int notification = 0;
		final int maxneednotification = cursor.getCount()/2;
		// 提醒分数下限
		float LowerBound = 27;
		float cutscore = 2;
		float plusscore = 2;
		boolean hasRecord = cursor.moveToFirst();
		while (hasRecord) {
			// 上次通话日期
			long lastcalldate = cursor.getLong(cursor
					.getColumnIndex(CallLogHelper.LASTCALLDATE));
			// 上次的分数
			float lastscore = cursor.getFloat(cursor
					.getColumnIndex(CallLogHelper.SCORE));

			// 每天减去两分
			lastscore -= cutscore;
			ContentValues values = new ContentValues();

			if (lastscore < LowerBound) {
				// 提醒
				notification++;

				// 提醒算成一次打电话，加2分，并且时间设置为前一天的这个时间（为了不至于分数加的特别快）
				lastscore += plusscore;
				lastcalldate = java.lang.System.currentTimeMillis() - 24 * 60
						* 60 * 1000;
				values.put(CallLogHelper.LASTCALLDATE, lastcalldate);
			}
			values.put(CallLogHelper.SCORE, lastscore);
			db.update(CallLogHelper.MAIN_TABLE, values,
					CallLogHelper.CACHED_NAME + " = ?",
					new String[] { cursor.getString(cursor
							.getColumnIndex(CallLogHelper.CACHED_NAME)) });
			hasRecord = cursor.moveToNext();
		}
		if (notification > maxneednotification) {
			myNotiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			setNotiType(R.drawable.icon30, "Hi,您有一段时间没和圈子里的人联系了，现在就去圈子看看？");
		}
		cursor.close();
		db.close();

		stopSelf();
		return START_STICKY;
	}

	/* 发出Notification的method */
	private void setNotiType(int iconId, String text) {
		/*
		 * 建立新的Intent，作为点选Notification留言条时， 会执行的Activity
		 */
		Intent notifyIntent = new Intent(this, CoreCircle.class);
		notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		/* 建立PendingIntent作为设定递延执行的Activity */
		PendingIntent appIntent = PendingIntent.getActivity(this, 0,
				notifyIntent, 0);

		/* 建立Notication，并设定相关参数 */
		Notification myNoti = new Notification();
		/* 设定statusbar显示的icon */
		myNoti.icon = iconId;
		/* 设定statusbar显示的文字讯息 */
		myNoti.tickerText = text;
		/* 设定notification发生时同时发出预设声音 */
		myNoti.defaults = Notification.DEFAULT_SOUND;
		/* 设定Notification留言条的参数 */
		myNoti.setLatestEventInfo(this, "CoreCircle温馨提示", text, appIntent);
		/* 送出Notification */
		myNotiManager.notify(0, myNoti);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
