package me.corecircle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class SplashScreen extends Activity {
	ImageView _imageView1;
	Button _btn1;
	AnimationDrawable _animaition;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }//onCreate

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		setContentView(R.layout.layout_splash);   
		    
	    _imageView1 = (ImageView)findViewById(R.id.lsh_imageView1);
	    //设置动画背景
	    _imageView1.setBackgroundResource(R.anim.anime);
	    //获得动画对象
	    _animaition = (AnimationDrawable) _imageView1.getBackground();
	    _animaition.setOneShot(false);
		_animaition.start();
	    Handler mHandler2 = new Handler();
	    mHandler2.postDelayed(new Runnable(){
	    	public void run() {
	    		_animaition.stop();
	    		SplashScreen.this.setResult(1, null);
	        	finish();
	    	}
	    }, 3000);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}
}
