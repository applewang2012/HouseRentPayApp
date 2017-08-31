package tenant.guardts.house;

import android.os.Bundle;
import android.view.Window;

public class VerifyPhoneNumActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_verify_phone_num);
	}
}
