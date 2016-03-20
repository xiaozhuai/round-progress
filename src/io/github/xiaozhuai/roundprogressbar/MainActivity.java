package io.github.xiaozhuai.roundprogressbar;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity{
	private RoundProgressBar mProgress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mProgress = (RoundProgressBar) findViewById(R.id.progress);
		mProgress.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mProgress.animationToNow();
			}
		});
		
	}


}
