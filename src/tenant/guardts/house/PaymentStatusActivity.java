package tenant.guardts.house;

import android.app.Activity;
import android.os.Bundle;

public class PaymentStatusActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_successful_payment);
		setContentView(R.layout.activity_payment_failure);
		
	}
}
