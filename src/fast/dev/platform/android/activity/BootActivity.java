package fast.dev.platform.android.activity;

import com.tencent.bugly.crashreport.CrashReport;

import android.os.Bundle;
import fast.dev.platform.android.R;
import fast.dev.platform.android.activity.base.BaseActivity;

public class BootActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		CrashReport.setUserSceneTag(getContext(), 11891);
	}

}