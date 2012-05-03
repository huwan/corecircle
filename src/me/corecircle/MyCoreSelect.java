package me.corecircle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MyCoreSelect extends Activity {
	/** Called when the activity is first created. */
	private ListView mlv1;
	private Button mb1, mb2, mb3;
	private TextView mtv1;
	private int next_time = 0;// 下一步点击次数
	private int back_time = 0;// 上一步点击次数
	private static int step = 0;// 记录步骤
	private long[] cb_array;// 记录ListView中勾选中的项
	private ArrayAdapter<String> adpt;//
	private static String[][] s_array, c_array, dc_array;// ListView绑定的字符串数组
	private static String[] all_array;
	private final static String[] Q_array = { "闲暇的时候，你愿意联系哪些人？",
			"试着去掉就在你身边，无需打电话的人", "不开心的时候，你愿意向哪些人倾诉？", "如果，还需要你在删去一些人，你会选择？",
			"真是艰难的决定！记得多给TA们打电话~" };
	private int Q_number;
	private int RQ_number;
	private int CotactsNumber = 0;
	private String CoreMember[];
	private int TotalMember = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_coreselect);

	}// end onCreate

	@Override
	public void onStart() {
		super.onStart();

		mlv1 = (ListView) findViewById(R.id.lcs_listView1);
		mb1 = (Button) findViewById(R.id.lcs_button1);
		mb2 = (Button) findViewById(R.id.lcs_button2);
		mb3 = (Button) findViewById(R.id.lcs_button3);
		mtv1 = (TextView) findViewById(R.id.lcs_textView1);

		s_array = new String[5][];
		c_array = new String[4][];
		dc_array = new String[4][];
		step = 0;

		// 加载ListView
		Cursor t_cursor = managedQuery(ContactsContract.Contacts.CONTENT_URI,
				null, null, null, null);
		t_cursor.moveToFirst(); // 访问数据库的适配器
		CotactsNumber = t_cursor.getCount();
		if (CotactsNumber > 0) {
			// CautionForCoreSelectError();
			all_array = new String[CotactsNumber];

			for (int i = 0; i < CotactsNumber; i++) {
				all_array[i] = t_cursor
						.getString(t_cursor
								.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
				t_cursor.moveToNext();
			}
			adpt = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_multiple_choice,
					all_array);
			mlv1.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			mlv1.setAdapter(adpt);

			s_array[step] = all_array;
			RefreshQuestion();
			mb3.setEnabled(false);
			mb2.setEnabled(false);
			mb1.setEnabled(false);
		} else {
			CautionForCoreSelectError();
		}

		mlv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				RefreshItem();
			}
		});// end setOnItemClickListener

		mb1.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (step > 0)
					step--;

				RefreshQuestion();
				RefreshButton();
				AdpterUpdata(s_array[step]);

			}

		});// end mb1 Listener

		mb2.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (step == 1 || step == 3) {
					ChoiceCoreMember(1);
					step++;
				} else if (step == 0 || step == 2) {
					ChoiceCoreMember(0);
					step++;
				}
				RefreshQuestion();
				RefreshButton();

			}

		});// end mb2 Listener

		mb3.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {

				TransMessage();
				finish();

			}

		});// end mb1 Listener
	}

	private void TransMessage() {

		Intent intent = this.getIntent();
		Bundle bundle = new Bundle();
		CoreMember = s_array[step];
		bundle.putStringArray("CoreMember", CoreMember);
		intent.putExtras(bundle);
		MyCoreSelect.this.setResult(3, intent);

	}

	private void ArraySet(String all[], long check[]) {
		if (all.length - check.length > 0 && check.length > 0) {
			String dischoice[] = new String[all.length - check.length];
			String choice[] = new String[check.length];
			for (int i = 0, j = 0, k = 0; j < all.length; i++, j++) {
				for (int f = 0; f < check.length; f++)
					if (j == check[f]) {
						choice[k] = all[j];
						j++;
						k++;
					}
				if (j < all.length)
					dischoice[i] = all[j];

			}
			dc_array[step] = dischoice;
			c_array[step] = choice;
		}// ebd if
		else {
			if (check.length == 0) {
				c_array[step] = null;
				dc_array[step] = all;
			} else {
				dc_array[step] = null;
				c_array[step] = all;
			}
		}
	}

	private void AdpterUpdata(String ArrayIn[]) {
		// adpt.notifyDataSetChanged();
		adpt = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice, ArrayIn);
		mlv1.setAdapter(adpt);
	}

	private void ChoiceCoreMember(int control) {
		cb_array = mlv1.getCheckItemIds();
		ArraySet(s_array[step], cb_array);
		if (control == 0) {

			AdpterUpdata(c_array[step]);
			s_array[step + 1] = c_array[step];

		} else {
			AdpterUpdata(dc_array[step]);
			s_array[step + 1] = dc_array[step];
		}
	}

	private void RefreshQuestion() {

		if (RQ_number != 0) {
			mtv1.setText(Q_array[step] + "\n[还需选择: " + Integer.toString(RQ_number)
					+ " 人]");
		} else {
			mtv1.setText(Q_array[step]);
		}

	}

	private void RefreshButton() {
		if (step == 0) {
			mb1.setEnabled(false);
			mb3.setEnabled(false);
			mb2.setEnabled(false);
		} else if (step == 1) {
			mb1.setEnabled(true);
			mb3.setEnabled(false);
			mb2.setEnabled(true);
		} else if (step == 2) {
			mb1.setEnabled(true);
			mb3.setEnabled(false);
			mb2.setEnabled(false);
		} else if (step == 3) {
			mb1.setEnabled(true);
			mb3.setEnabled(false);
			mb2.setEnabled(false);
		} else if (step == 4) {
			mb1.setEnabled(true);
			mb3.setEnabled(true);
			mb2.setEnabled(false);
		}
	}

	private void RefreshItem() {
		cb_array = mlv1.getCheckItemIds();
		Q_number = cb_array.length;

		if (step == 0) {
			if (CotactsNumber > 30) {
				if (Q_number == 20)
					mb2.setEnabled(true);
				else {
					mb2.setEnabled(false);
				}
				RQ_number = 20 - Q_number;
			} else {
				if (Q_number > 0)
					mb2.setEnabled(true);
				else
					mb2.setEnabled(false);
				RQ_number = 0;
			}
		} else if (step == 1) {
			if (s_array[step].length - Q_number > 0)
				mb2.setEnabled(true);
			else
				mb2.setEnabled(false);
			RQ_number = 0;
		} else if (step == 2) {
			if (Q_number > 0)
				mb2.setEnabled(true);
			else
				mb2.setEnabled(false);
			RQ_number = 0;
		} else if (step == 3) {

			if (s_array[step].length - 6 > 0) {
				if (Q_number >= s_array[step].length - 6
						&& s_array[step].length - Q_number > 0)
					mb2.setEnabled(true);
				else
					mb2.setEnabled(false);
				RQ_number = s_array[step].length - 6 - Q_number;
			} else {
				if (s_array[step].length - Q_number > 0)
					mb2.setEnabled(true);
				else
					mb2.setEnabled(false);
				RQ_number = 0;
			}
		}

		if (RQ_number != 0) {
			mtv1.setText(Q_array[step] + "\n[还需选择: " + Integer.toString(RQ_number)
					+ " 人]");
		} else {
			mtv1.setText(Q_array[step]);
		}
	}

	private void CautionForCoreSelectError() {// The Fuction to Show the list
												// catain phonenumber and
												// phonetype

		new AlertDialog.Builder(MyCoreSelect.this)
				.setTitle("提示")
				.setMessage("通讯录中联系人过少，请在通讯录中添加更多联系人")
				.setNeutralButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface,
									int wichbutton) {
								Intent intent = new Intent(Intent.ACTION_INSERT);
								intent.setType("vnd.android.cursor.dir/person");
								intent.setType("vnd.android.cursor.dir/contact");
								intent.setType("vnd.android.cursor.dir/raw_contact");

								startActivity(intent);
								//finish();
							}
						})
				.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface,
									int wichbutton) {
								finish();
							}
						}).show();

	}// end CautionForNotification()

}// end Activity