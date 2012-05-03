package me.corecircle;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AppHelp extends Activity {

	private Button mb1, mb2, mb3;
	private ImageView iv1;
	private static int[] imagemap = {R.drawable.hp,R.drawable.hp1,
			R.drawable.hp2, R.drawable.hp3, R.drawable.hp4, R.drawable.hp5,
			R.drawable.hp6 };
	private int mapcursor = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_help);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		mb1 = (Button) findViewById(R.id.lh_button1);
		mb2 = (Button) findViewById(R.id.lh_button2);
		mb3 = (Button) findViewById(R.id.lh_button3);
		iv1 = (ImageView) findViewById(R.id.lh_imageView1);
		mb2.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mapcursor < imagemap.length - 1)
					mapcursor++;
				iv1.setImageDrawable(getResources().getDrawable(
						imagemap[mapcursor]));
			}
		});
		mb1.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mapcursor > 0)
					mapcursor--;
				iv1.setImageDrawable(getResources().getDrawable(
						imagemap[mapcursor]));
			}

		});
		mb3.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				AppHelp.this.setResult(4, null);
				finish();
			}

		});
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

}
