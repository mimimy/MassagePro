package com.massage.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        RelativeLayout img = (RelativeLayout) findViewById(R.id.rl_root);
        
        RotateAnimation rAnimation = new RotateAnimation(0, 360,
        		Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rAnimation.setDuration(1000);
        rAnimation.setFillAfter(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1,0,1,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setDuration(1000);
        scaleAnimation.setFillAfter(true);
        AlphaAnimation alanimation = new AlphaAnimation(0, 1);
        alanimation.setDuration(1000);
        alanimation.setFillAfter(true);
        
        
        AnimationSet am = new AnimationSet(true);
        am.addAnimation(rAnimation);
        am.addAnimation(scaleAnimation);
        am.addAnimation(alanimation);
        
        img.startAnimation(am);
        am.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
				startActivity(intent);
				finish();
				
			}
		});
    }

    
}
