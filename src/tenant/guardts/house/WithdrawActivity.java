package tenant.guardts.house;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

public class WithdrawActivity extends BaseActivity {

	private TextView mAddBankCard;
	private TextView mSum;//提现金额
	private String withdrawSum;
	private PopupWindow popupWindow;
	private Button mWithdraw;
	private String idCard;
	private String num;
	private String type;
	private String cardNo;
	private String bankName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_withdraw);
		initData();
		initView();
		initEvent();
	}

	private void initData() {
		Intent intent = getIntent();
		withdrawSum = intent.getStringExtra("balance");
		idCard = intent.getStringExtra("IDCard");
		cardNo = intent.getStringExtra("CardNO");
		bankName = intent.getStringExtra("BankName");
	}

	private void initEvent() {
		mAddBankCard.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//添加银行卡
				Intent intent = new Intent(WithdrawActivity.this, AddBankCardActivity.class);
				intent.putExtra("IDCard", idCard);
				
				startActivityForResult(intent, 100);
			}
		});
		
		mWithdraw.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//提现验证码
				getVerifyNum();
				
			}
		});
		
		popupWindow.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				//popupwindow消失时还原
				setBackgroundAlpha(1f);
			}
		});
		
	}
	/**设置背景透明度
	 * @param alpha
	 */
	public void setBackgroundAlpha(float alpha){
		WindowManager.LayoutParams params=getWindow().getAttributes();
		params.alpha=alpha;
		getWindow().setAttributes(params);
	}

	/**验证码popupwindow
	 * 
	 */
	protected void getVerifyNum() {
		setBackgroundAlpha(0.2f);
		View parent=View.inflate(this, R.layout.activity_withdraw, null);
		popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
	}

	private void initView() {
		mAddBankCard = (TextView) findViewById(R.id.tv_add_bank_card);
		mSum = (TextView) findViewById(R.id.withdraw_tv_sum);
		mSum.setText("¥ "+withdrawSum);
		mWithdraw = (Button) findViewById(R.id.btn_withdraw);
		View view = View.inflate(this, R.layout.withdraw_popupwindow, null);
		popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(false);
		if(cardNo==null||cardNo.equals("")){
			mAddBankCard.setText("添加银行卡");
		}else{
			mAddBankCard.setText(bankName+"("+cardNo+")");
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//回传银行卡类型及卡号后四位
		if(resultCode==RESULT_OK){
			if(requestCode==100){
				num = data.getStringExtra("bankNum");
				type = data.getStringExtra("bankType");
				mAddBankCard.setText(type+"("+num+")");
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
