package tenant.guardts.house;

import java.lang.reflect.Type;
import java.util.ArrayList;

import org.json.JSONArray;
import org.ksoap2.serialization.SoapObject;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.oliveapp.face.livenessdetectorsdk.utilities.utils.LogUtil;

import tenant.guardts.house.adapter.CommonRetinueAdapter;
import tenant.guardts.house.adapter.CommonRetinueAdapter.CallBack;
import tenant.guardts.house.model.Retinue;
import tenant.guardts.house.model.RetinuesResult;
import tenant.guardts.house.model.UniversalAdapter;
import tenant.guardts.house.model.UniversalViewHolder;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.view.CityInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class AddRentParternerActivity extends BaseActivity implements CallBack {

	private TextView mTitleBar;

	private HoursePresenter mPresenter;
	private EditText mIdCard;

	private Button mAdd;
	private EditText mName;

	private TextView commonRetinue;
	private String mGetRetinuesByIDCard = "http://tempuri.org/GetRetinuesByIDCard";// 根据手机号获取常用随行人员

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_add_rent_parterner);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		mTitleBar = (TextView) findViewById(R.id.id_titlebar);
		mTitleBar.setText("添加随行人员");
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		initView();
		getRetinuesByIDCard(CommonUtil.mUserLoginName);
		initEvent();

	}

	private void initEvent() {
		mAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(mName.getText().toString())) {
					Toast.makeText(AddRentParternerActivity.this, "随行人员姓名不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				if (TextUtils.isEmpty(mIdCard.getText().toString())) {
					Toast.makeText(AddRentParternerActivity.this, "随行人员身份证号不能为空", Toast.LENGTH_SHORT).show();
					return;
				} else if (mIdCard.getText().toString().length() < 18) {
					Toast.makeText(AddRentParternerActivity.this, "身份证号位数有误", Toast.LENGTH_SHORT).show();
					return;
				}

				Intent intent = new Intent();
				intent.putExtra("retinue", new RetinuesResult(mName.getText().toString(), mIdCard.getText().toString()));
				setResult(RESULT_OK, intent);
				finish();

			}
		});

		commonRetinue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				View parent = View.inflate(AddRentParternerActivity.this, R.layout.activity_add_rent_parterner, null);
				if (popupWindow != null) {
					popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
					setBackgroundAlpha(0.2f);
				}
			}
		});

		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				Intent intent = new Intent();
				intent.putExtra("common_list", commonChoose);
				setResult(RESULT_OK, intent);
				finish();
				// Intent intent = new Intent(AddRentParternerActivity.this,
				// ParternerRecordActivity.class);
				// Bundle bundle = new Bundle();
				// if (commonChoose != null) {
				// bundle.putSerializable("common_list", commonChoose);
				// }
				// intent.putExtras(bundle);
				// startActivity(intent);
				// Toast.makeText(AddRentParternerActivity.this,
				// commonChoose+"", Toast.LENGTH_LONG).show();

			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (popupWindow != null) {
					popupWindow.dismiss();
				}

			}
		});
		// listview.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		// long arg3) {
		// CheckBox box=(CheckBox) arg1.findViewById(R.id.checkbox);
		//
		//
		// }
		// });

	}

	private ArrayList<RetinuesResult> list;
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 100) {
				String value = (String) msg.obj;
				if (!TextUtils.isEmpty(value)) {
					LogUtil.e("mingguo", value + "--" + mGetRetinuesByIDCard);
					if (value.equals("[]")) {
						commonRetinue.setVisibility(View.GONE);
					} else {
						commonRetinue.setVisibility(View.VISIBLE);
						Gson gson = new Gson();
						Type type = new TypeToken<ArrayList<RetinuesResult>>() {
						}.getType();
						list = gson.fromJson(value, type);
						initPopupWindow(list);

					}
				}

			}
		};
	};

	private PopupWindow popupWindow;

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		super.onStatusSuccess(action, templateInfo);
		if (action != null && templateInfo != null) {
			if (action.equals(mGetRetinuesByIDCard)) {
				Message msg = mHandler.obtainMessage();
				msg.what = 100;
				msg.obj = templateInfo;
				msg.sendToTarget();
			}
		}

	}

	/**
	 * 设置背景透明度
	 * 
	 * @param alpha
	 */
	public void setBackgroundAlpha(float alpha) {
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.alpha = alpha;
		getWindow().setAttributes(params);
	}

	private ArrayList<RetinuesResult> commonChoose = new ArrayList<RetinuesResult>();// 选中的常用随行人员集合

	private Button btn;

	private ListView listview;

	private Button btnCancel;

	/**
	 * 常用随行人员列表
	 */
	private void initPopupWindow(final ArrayList<RetinuesResult> list) {

		popupWindow.setFocusable(true);

		listview.setAdapter(new CommonRetinueAdapter(this, list, this));

		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				setBackgroundAlpha(1f);

			}
		});
	}

	/**
	 * 根据手机号获取常用随行人员
	 * 
	 * @param idCard
	 */
	private void getRetinuesByIDCard(String idCard) {
		//
		String url = CommonUtil.mUserHost + "Services.asmx?op=GetRetinuesByIDCard";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mGetRetinuesByIDCard));
		rpc.addProperty("idCard", idCard);
		mPresenter.readyPresentServiceParams(AddRentParternerActivity.this, url, mGetRetinuesByIDCard, rpc);
		mPresenter.startPresentServiceTask(true);

	}

	private void initView() {
		mName = (EditText) findViewById(R.id.parterner_name);
		mIdCard = (EditText) findViewById(R.id.parterner_id_card_num);
		mAdd = (Button) findViewById(R.id.add_retinue);
		commonRetinue = (TextView) findViewById(R.id.common_retinue);
		View view = View.inflate(this, R.layout.common_retinue_list, null);
		popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		listview = (ListView) view.findViewById(R.id.listview);
		btn = (Button) view.findViewById(R.id.btn_add);
		btnCancel = (Button) view.findViewById(R.id.btn_cancel);
	}

	@Override
	public void click(View v) {
		switch (v.getId()) {
		case R.id.checkbox:

			CheckBox box = (CheckBox) v;
			int position=(int) v.getTag();
			if (list != null) {
				if (box.isChecked()) {
					commonChoose.add(new RetinuesResult(list.get(position).name, list.get(position).idcard));
				}
			}
			break;

		default:
			break;
		}

	}

}
