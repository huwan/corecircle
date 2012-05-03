package me.corecircle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationServiceReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent temp = new Intent(context, NotificationService.class);
		context.startService(temp);
	}
}