package tenant.guardts.house;

import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class HomeSearchActivity extends BaseActivity {

	private TextView mTitleBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_home_search);
		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView)findViewById(R.id.id_titlebar);
		mTitleBar.setText("搜索房屋");
		
	}
}
