package tenant.guardts.house;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class WalletActivity extends Activity {

	private TextView mBalance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wallet);
		Intent intent = getIntent();
		String balance = intent.getStringExtra("balance");
		mBalance = (TextView) findViewById(R.id.text_balance);
		mBalance.setText(balance);
	}
}
