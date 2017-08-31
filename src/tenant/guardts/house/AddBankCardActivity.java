package tenant.guardts.house;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class AddBankCardActivity extends BaseActivity {

	private Button mNext;//下一步

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_bank_card);
		initView();
		initEvent();
	}

	private void initEvent() {
		mNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//跳转验证手机号页面，并回传银行卡信息，和手机号
				/*Intent intent = new Intent();
				intent.putExtra("CardType", "");
				intent.putExtra("LastFourNum", "");
				intent.putExtra("Phone", "");
				setResult(RESULT_OK, intent);*/
				Intent intent = new Intent(AddBankCardActivity.this, VerifyPhoneNumActivity.class);
				startActivity(intent);
				
				
			}
		});
		
	}

	private void initView() {
		mNext = (Button) findViewById(R.id.add_bank_card_btn_next);
		
	}
}
