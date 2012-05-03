package me.corecircle;

import android.telephony.PhoneStateListener;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;


public class CustomPhoneStateListener extends PhoneStateListener {
	Context context;

	public CustomPhoneStateListener(Context context) {
		super();
		this.context = context;
	}

	int phoneState = TelephonyManager.CALL_STATE_IDLE;

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		super.onCallStateChanged(state, incomingNumber);

		switch (state) {
		case TelephonyManager.CALL_STATE_IDLE:
			if (phoneState != TelephonyManager.CALL_STATE_IDLE) {
				// 获取最新的通话记录，并记录到数据库中
				Intent intent = new Intent(context, GetCallLogService.class);
				/* 设定新TASK的方式 */
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				/* 以startService方法启动Intent */
				context.startService(intent);
			}
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:
			break;
		case TelephonyManager.CALL_STATE_RINGING:
			break;
		default:
			break;
		}
		phoneState = state;
	}

};
