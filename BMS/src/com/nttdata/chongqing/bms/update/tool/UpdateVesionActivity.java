package com.nttdata.chongqing.bms.update.tool;

import android.app.Activity;
import android.os.Bundle;

public class UpdateVesionActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		UpdateManager manager = new UpdateManager(
				UpdateVesionActivity.this);
		// 检查软件更新
		manager.checkUpdate();

	}
}