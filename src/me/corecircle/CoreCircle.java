package me.corecircle;

import java.util.Random;
import me.corecircle.R;
import me.corecircle.ShakeDetector.OnShakeListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import android.app.PendingIntent;
import android.app.AlarmManager;

public class CoreCircle extends Activity {
	/** Called when the activity is first created. */
	private int RunTime = 0;
	private static long StartTime = 0, RepeatTime = 24 * 60 * 60 * 1000;// 提醒检测周期
	private static int MODE = MODE_PRIVATE;// 读写配置文件模式
	private static final String PREFERENCE_NAME = "SaveSetting";// 读写配置文件文件名

	private int TotalMember = 0;
	private int RecomMember = -5;// 随机到的成员名字
	private String CoreMember[];
	private int CoreMemberScore[];
	private int NeedToReplaceMember = -1;
	private boolean NeedToCreateMember = false;// 控制是否进行创建操作
	private static String PhoneType[];
	private static String PhoneNumber[];
	private static String PhoneMessage[];
	private static final int PICK_CONTACT_SUBACTIVITY = 2;// *Value to describe
															// ContactsPiker
	private static final int PICK_CORE_MEMBER = 3;
	private static final int INIT_LOG = 1;
	private static final int INIT_HELP = 4;
	private Button mb1;
	private ShakeDetector shake;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐去标题（应用的名字)
		// 此设定必须要写在setContentView之前，否则会有异常）
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		InitSP(0);

		TelephonyManager telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new CustomPhoneStateListener(this),
				PhoneStateListener.LISTEN_CALL_STATE);

		Intent wait = new Intent();
		wait.setClass(CoreCircle.this, SplashScreen.class);
		startActivityForResult(wait, INIT_LOG);

	}// end OnCreate

	@Override
	public void onStart() {
		super.onStart();

		if (RunTime != 0) {

			setContentView(R.layout.layout_main);
			InitMainActivity();

			ViewMovement();

			mb1 = (Button) findViewById(R.id.lma_button1);
			mb1.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					// 刷新一次主Activity数据
					InitMainActivity();
					ViewMovementRadom();

					mb1.setEnabled(false);
					for (; MovementView.ReturnWait() == 0;) {
						// wait
					}

					Handler mHandler = new Handler();
					Runnable mRunnable = new Runnable() {
						public void run() {
							mb1.setEnabled(true);
							InitPhoneAlertDialog(CoreMember[RecomMember]);
							ViewMovement();
						}
					};
					mHandler.postDelayed(mRunnable, 1000 * 2);
				}
			});

			shake = new ShakeDetector(this);
			shake.registerOnShakeListener(new OnShakeListener() {
				public void onShake() {
					ViewMovement();
				}
			});
			shake.start();
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (RunTime != 0) {
			shake.stop();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float xtouch = event.getX();
		float ytouch = event.getY();

		try {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:

				String temp = MovementView.CheckInOut((int) xtouch,
						(int) ytouch);

				if (temp == "IN") {

					ViewMovementStop();
				} else {
					InitPhoneAlertDialog(temp);
				}
				// ViewMovement();
				break;
			case MotionEvent.ACTION_MOVE:

				break;
			case MotionEvent.ACTION_UP:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PICK_CONTACT_SUBACTIVITY: // contactspicker模块(用于CoreMemberReplace)
		{
			if (data != null) {
				final Uri uriRet = data.getData();

				try {

					Cursor t_cursor;
					int Continue = 1;
					String ReplaceMember = null;

					t_cursor = managedQuery(uriRet, null, null, null, null);
					t_cursor.moveToFirst();
					ReplaceMember = t_cursor
							.getString(t_cursor
									.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
					if (NeedToReplaceMember != -1 && ReplaceMember != null) {
						for (int i = 0; i < CoreMember.length; i++)
							if (ReplaceMember.contentEquals(CoreMember[i]))
								Continue = 0;
						if (Continue == 1) {
							Replace(CoreMember[NeedToReplaceMember],
									ReplaceMember);// 更新数据库
							CoreMember[NeedToReplaceMember] = ReplaceMember;// 更新主Activity

						} else {

						}
						NeedToReplaceMember = -1;
						ReplacePhoneAlertDialog();

					}
					if (NeedToCreateMember == true && ReplaceMember != null) {
						for (int i = 0; i < CoreMember.length; i++)
							if (ReplaceMember.contentEquals(CoreMember[i]))
								Continue = 0;
						if (Continue == 1) {
							String tempcoremember[] = new String[TotalMember + 1];
							for (int i = 0; i < TotalMember; i++)
								tempcoremember[i] = CoreMember[i];
							tempcoremember[TotalMember] = ReplaceMember;
							InitContact(ReplaceMember);
							TotalMember++;
							CoreMember = tempcoremember;

						} else {

						}
						NeedToCreateMember = false;
					}// end if
					InitMainActivity();

				} catch (Exception e) {

				}
			}// end if
		}
			break; // end PICK_CONTACT_SUBACTIVITY:

		case PICK_CORE_MEMBER: {// CoreSecect消息处理
			if (data != null)// 若Activity正确返回
			{
				if (RunTime == 0) {
					RunTime++;
					InitSP(1);
				} else // 若不是第一次运行，重置时需清空数据库
				{
					for (int i = 0; i < TotalMember; i++) {
						DeleteContact(CoreMember[i]);
					}
					RunTime++;
				}
				Bundle bundle = data.getExtras();
				CoreMember = bundle.getStringArray("CoreMember");
				TotalMember = CoreMember.length;

				for (int i = 0; i < TotalMember; i++) {
					InitContact(CoreMember[i]);
				}
				InitMainActivity();// 初始化CoreMember 重新计算亲密度
				// setContentView(R.layout.layout_main);
				if (RunTime == 1) {// 注册BroadcastReceiver&AlarmManger
					StartTime = SystemClock.elapsedRealtime();
					Intent intent = new Intent(CoreCircle.this,
							NotificationServiceReceiver.class);
					PendingIntent sender = PendingIntent.getBroadcast(
							CoreCircle.this, 0, intent, 0);
					AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
					am.setRepeating(AlarmManager.RTC, StartTime, RepeatTime,
							sender);
				}
			} else {
				if (RunTime == 0)
					CautionForCoreSelectError();
			}

		}// end PICK_CORE_MEMBER
			break;

		case INIT_LOG:
			if (RunTime == 0) {
				Intent help = new Intent();
				help.setClass(CoreCircle.this, AppHelp.class);
				startActivityForResult(help, INIT_HELP);

			}// end if
			break;

		case INIT_HELP:
			if (RunTime == 0) {
				Intent select = new Intent();
				select.setClass(CoreCircle.this, MyCoreSelect.class);
				startActivityForResult(select, PICK_CORE_MEMBER);

			}
			break;
		}// end switch
		super.onActivityResult(requestCode, resultCode, data);

	}// end onActivityResult

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 4, 0, R.string.app_insert);
		menu.add(0, 0, 1, R.string.app_replace);
		menu.add(0, 1, 2, R.string.app_reset);
		menu.add(0, 5, 3, R.string.app_help);
		menu.add(0, 2, 4, R.string.app_about);
		menu.add(0, 3, 5, R.string.app_exit);

		return super.onCreateOptionsMenu(menu);

	}

	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case 0: {
			// 开始运行ContactsPicker模块
			ReplacePhoneAlertDialog();
		}
			break;
		case 1: {
			// 开始运行CoreSelect并等待其返回值
			Intent temp = new Intent();
			temp.setClass(CoreCircle.this, MyCoreSelect.class);
			startActivityForResult(temp, PICK_CORE_MEMBER);
		}
			break;
		case 2:
			AboutAlertDialog();
			break;
		case 3:
			finish();
			break;
		case 4: {
			if (TotalMember >= 6)
				CreatePhoneAlertDialog();
			else {
				NeedToCreateMember = true;// 判断标志置为True 并运行ContactsPicker模块
				startActivityForResult(
						new Intent(
								Intent.ACTION_PICK,
								android.provider.ContactsContract.Contacts.CONTENT_URI),
						PICK_CONTACT_SUBACTIVITY);
			}
		}
			break;
		case 5: {
			Intent help = new Intent();
			help.setClass(CoreCircle.this, AppHelp.class);
			startActivity(help);
		}
			break;
		}
		return true;
	}

	// 用于插入联系人
	public void InitContact(String contact) {

		String name = contact;
		long date = java.lang.System.currentTimeMillis();
		float InitialValue = 33;

		CallLogHelper dbHelper = new CallLogHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(CallLogHelper.CACHED_NAME, name);
		values.put(CallLogHelper.LASTCALLDATE, date);
		values.put(CallLogHelper.SCORE, InitialValue);
		db.insert(CallLogHelper.MAIN_TABLE, null, values);

		db.close();
	}

	// 用于删除联系人
	public void DeleteContact(String contact) {

		CallLogHelper dbHelper = new CallLogHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		db.delete(CallLogHelper.MAIN_TABLE, CallLogHelper.CACHED_NAME + "=?",
				new String[] { contact });
		db.close();
	}

	// 替换联系人，需要两个信息
	public void Replace(String oldcontact, String newcontact) {
		DeleteContact(oldcontact);
		InitContact(newcontact);
	}

	private void SPWrite(String key, int value)// 写入配置文件操作
	{
		SharedPreferences sharedPreferences = getSharedPreferences(
				PREFERENCE_NAME, MODE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	private void SPRead(String key, int value)// 写入配置文件操作
	{

		SharedPreferences sharedPreferences = getSharedPreferences(
				PREFERENCE_NAME, MODE);
		value = sharedPreferences.getInt(key, value);
		RunTime = value;
	}

	private void InitSP(int control) {
		if (control == 1) {
			SPWrite("runtime", RunTime);
		} else
			SPRead("runtime", 0);
	}

	private void ViewMovement() {
		// InitMainActivity();
		MovementView.RefreshMove(TotalMember, CoreMemberScore, CoreMember);
	}

	private void ViewMovementStop() {
		MovementView.RefreshStop();

	}

	private void ViewMovementRadom() {

		MovementView.RefreshStop();
		MovementView.RefreshRadom(TotalMember, RecomMember);

	}

	private void InitMainActivity() // Including Reading Database to get
									// CoreMember and Init The Score
	{
		CallLogHelper dbHelper = new CallLogHelper(getBaseContext());
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		Cursor cursor = db.query(CallLogHelper.MAIN_TABLE, null, null, null,
				null, null, null);
		int num = cursor.getCount();
		InitSP(0);
		// 分级的数组，数的范围为1~8,联系亲密度
		int grade[] = new int[num];
		String name[] = new String[num];
		while (cursor.moveToNext()) {
			name[cursor.getPosition()] = cursor.getString(cursor
					.getColumnIndex(CallLogHelper.CACHED_NAME));
			int lastscore = cursor.getInt(cursor
					.getColumnIndex(CallLogHelper.SCORE));
			switch (lastscore) {
			case 27:
				grade[cursor.getPosition()] = 1;
				break;
			case 28:
			case 29:
				grade[cursor.getPosition()] = 2;
				break;
			case 30:
			case 31:
				grade[cursor.getPosition()] = 3;
				break;
			case 32:
			case 33:
				grade[cursor.getPosition()] = 4;
				break;
			case 34:
			case 35:
				grade[cursor.getPosition()] = 5;
				break;
			case 36:
			case 37:
				grade[cursor.getPosition()] = 6;
				break;
			case 38:
			case 39:
				grade[cursor.getPosition()] = 7;
				break;
			case 40:
			case 41:
				grade[cursor.getPosition()] = 8;
				break;
			default:
				break;
			}
		}

		// 将联系紧密情况转换成需要联系的程度
		int reversegradesum = 0;
		int scale = 9;
		int[] reversegrade = new int[num];
		for (int i = 0; i < num; i++) {
			reversegrade[i] = scale - grade[i];
			reversegradesum += reversegrade[i];
		}
		Random random = new Random();// 创建random对象
		int randNumber = random.nextInt(reversegradesum);
		int range = 0;
		RecomMember = 0;
		for (int i = 0; i < num; i++) {
			range += reversegrade[i];
			if (randNumber <= range) {
				RecomMember = i;
				break;
			}
		}

		cursor.close();
		db.close();
		// 更新主Activity
		CoreMember = name;
		CoreMemberScore = grade;
		TotalMember = num;
	}// end InitMainActivity

	private void InitPhoneMessage() {
		if (PhoneType != null) {
			PhoneMessage = new String[PhoneType.length];
			for (int i = 0; i < PhoneType.length; i++)
				PhoneMessage[i] = "[" + PhoneType[i] + "]:" + PhoneNumber[i];
		}
	}

	// 得到其中一个联系人的电话信息
	private void GetCoreMemberMessage(String name) {
		try {
			Cursor t_cursor, t_cphones;

			int t_contactId, t_typePhone, t_resType;// 标志电话号码类型变量
			String t_phonetype;// 电话号码类型临时变量

			t_cursor = managedQuery(ContactsContract.Contacts.CONTENT_URI,
					null, null, null, null);
			t_cursor.moveToFirst();
			for (int j = 0; j < t_cursor.getCount(); j++) {
				if (name.contentEquals(t_cursor.getString(t_cursor
						.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)))) {
					t_contactId = t_cursor.getInt(t_cursor
							.getColumnIndex(ContactsContract.Contacts._ID));
					t_cphones = managedQuery(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = " + t_contactId, null, null);

					if (t_cphones.getCount() > 0) {
						t_cphones.moveToFirst();
						PhoneType = new String[t_cphones.getCount()];
						PhoneNumber = new String[t_cphones.getCount()];
						for (int i = 0; i < t_cphones.getCount(); i++) {
							t_typePhone = t_cphones
									.getInt(t_cphones
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
							t_resType = ContactsContract.CommonDataKinds.Phone
									.getTypeLabelResource(t_typePhone);
							t_phonetype = getString(t_resType);

							PhoneType[i] = t_phonetype;
							PhoneNumber[i] = t_cphones
									.getString(t_cphones
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							t_cphones.moveToNext();

						}
						t_cphones.close();

					}// end if
					else {

					}// end else
					break;
				}

				t_cursor.moveToNext();
			}// end for

		}// end try
		catch (Exception e) {

		}
	}// end GetCoreMemberMessage

	private void InitPhoneAlertDialog(String name) {// The Fuction to Show the
													// list catain phonenumber
													// and phonetype
		if (name != null) {
			GetCoreMemberMessage(name);
			InitPhoneMessage();
			new AlertDialog.Builder(CoreCircle.this)
					.setTitle("给" + name + "拨打电话")
					.setItems(PhoneMessage,
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface,
										final int seclectitem) {
									new AlertDialog.Builder(CoreCircle.this)
											.setTitle("请再次确认拨打")
											.setNeutralButton(
													"确定",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialoginterface,
																int wichbutton) {
															PhoneCall(PhoneNumber[seclectitem]);

														}
													})// end .setNeutralButton
														// floor-2
											.setNegativeButton(
													"取消",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialoginterface,
																int wichbutton) {
															dialoginterface
																	.dismiss();

														}
													}).show();// end
																// .setNegativeButton
																// floor-2

								}// end onClick
							})// end .setitemsfloor -1
					.show();

		}// end if
	}// end InitPhoneAlertDialog

	private void ReplacePhoneAlertDialog() {// The Fuction to Show the list
											// catain phonenumber and phonetype
		if (CoreMember != null) {
			new AlertDialog.Builder(CoreCircle.this)
					.setTitle("您需要替换谁？")
					.setItems(CoreMember,
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface,
										final int seclectitem) {
									new AlertDialog.Builder(CoreCircle.this)
											.setTitle("请确认替换TA？")
											.setNeutralButton(
													"确定",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialoginterface,
																int wichbutton) {
															NeedToReplaceMember = seclectitem;
															// 开始运行ContactsPicker模块
															startActivityForResult(
																	new Intent(
																			Intent.ACTION_PICK,
																			android.provider.ContactsContract.Contacts.CONTENT_URI),
																	PICK_CONTACT_SUBACTIVITY);

														}
													})// end .setNeutralButton
														// floor-2
											.setNegativeButton(
													"取消",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialoginterface,
																int wichbutton) {
															ReplacePhoneAlertDialog();
														}
													}).show();// end
																// .setNegativeButton
																// floor-2

								}// end onClick
							})// end .setitems
					.setNegativeButton("返回",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface,
										int wichbutton) {
									dialoginterface.dismiss();
								}
							})// end .setNegativeButton floor-1
					.show();

		}// end if
	}// end InitPhoneAlertDialog

	private void CreatePhoneAlertDialog() {// The Fuction to Show the list
											// catain phonenumber and phonetype

		new AlertDialog.Builder(CoreCircle.this).setTitle("Hi")
				.setMessage("核心圈已满\n请选择替换圈内成员")
				.setNeutralButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface,
							int wichbutton) {
						dialoginterface.dismiss();
					}
				}).show();

	}// end CreatePhoneAlertDialog()

	private void AboutAlertDialog() {// The Fuction to Show the list catain
										// phonenumber and phonetype

		new AlertDialog.Builder(CoreCircle.this).setTitle("关于我们")
				.setMessage(R.string.app_abouttxt)
				.setNeutralButton("返回", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface,
							int wichbutton) {
						dialoginterface.dismiss();

					}
				}).show();

	}// end AboutAlertDialog()

	private void CautionForCoreSelectError() {// The Fuction to Show the list
												// catain phonenumber and
												// phonetype

		new AlertDialog.Builder(CoreCircle.this).setTitle("提示")
				.setMessage("请先按照向导构建你的核心圈~")
				.setNeutralButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface,
							int wichbutton) {
						Intent temp = new Intent();
						temp.setClass(CoreCircle.this, MyCoreSelect.class);
						startActivityForResult(temp, PICK_CORE_MEMBER);
					}
				})

				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface,
							int wichbutton) {
						finish();
					}
				}).show();

	}// end CautionForNotification()

	private void PhoneCall(String strInput) {
		try {
			Intent mycall = new Intent("android.intent.action.CALL",
					Uri.parse("tel:" + strInput));
			startActivity(mycall);
		} catch (Exception e) {

		}
	}
}// end Activity