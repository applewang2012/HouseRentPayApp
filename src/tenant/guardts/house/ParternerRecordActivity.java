package tenant.guardts.house;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;

import org.ksoap2.serialization.SoapObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import tenant.guardts.house.adapter.ParternerRecordAdapter;
import tenant.guardts.house.adapter.ParternerRecordAdapter.CallBack;
import tenant.guardts.house.model.Retinue;
import tenant.guardts.house.model.RetinuesResult;
import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.CommonUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ParternerRecordActivity extends BaseActivity implements CallBack {

	private TextView mTitleBar;
	private ListView mListView;
	private ArrayList<RetinuesResult> newList = new ArrayList<>();
	private Button mAdd;

	private HoursePresenter mPresenter;
	private TextView title;
	private TextView mSave;

	private static final int ADD_RETINUE_REQUEST_CODE = 111;
	private ArrayList<RetinuesResult> list;
	private ParternerRecordAdapter adapter;
	private String flag;
	private ArrayList<RetinuesResult> haveAddedList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_parterner_record);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_with_save);
		mTitleBar = (TextView) findViewById(R.id.id_titlebar);
		mSave = (TextView) findViewById(R.id.save);
		mTitleBar.setText("随行人员管理");
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		initView();
		initEvent();
		haveAddedList = (ArrayList<RetinuesResult>) getIntent().getSerializableExtra("have_added");
		if (haveAddedList != null) {
			if (haveAddedList.size() > 0) {

				newList.addAll(haveAddedList);
				adapter.notifyDataSetChanged();
			}
		}

	}

	private void initEvent() {
		mAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(ParternerRecordActivity.this, AddRentParternerActivity.class);

				startActivityForResult(intent, ADD_RETINUE_REQUEST_CODE);
			}
		});

		mSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (newList.size() > 0) {
					Intent intent = new Intent();
					intent.putExtra("retinues_list", newList);
					setResult(RESULT_OK, intent);
					finish();
				}
				if (list != null) {
					if (list.size() > 0) {
						Intent intent = new Intent();
						intent.putExtra("retinues_list", list);
						setResult(RESULT_OK, intent);
						finish();
					}
				}

			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == ADD_RETINUE_REQUEST_CODE) {

				RetinuesResult retinue = (RetinuesResult) data.getSerializableExtra("retinue");
				if (retinue != null) {
					checkIdentifyIdcardSame(retinue);
				}

				list = (ArrayList<RetinuesResult>) data.getSerializableExtra("common_list");
				if (list != null) {
					
					for (int i = 0; i < list.size(); i++){
						RetinuesResult child = list.get(i);
						checkIdentifyIdcardSame(child);
					}
					
					
				}
				
				
				adapter.notifyDataSetChanged();
			}
		}
	}
	
	/**遍历集合根据身份证去重
	 * @param info
	 */
	private void checkIdentifyIdcardSame(RetinuesResult info){
		if (newList.size() == 0){
			newList.add(info);
			return;
		}
		boolean isSame = false;
		for (int index = 0; index < newList.size(); index++){
			RetinuesResult child = newList.get(index);
			if (child.idcard != null && child.idcard.equals(info.idcard)){
				isSame = true;
				break;
			}
		}
		if (!isSame){
			newList.add(info);
		}
	}

	private void initData() {
		if (getIntent() != null) {
			if (getIntent().getExtras() != null) {
				RetinuesResult retinue = (RetinuesResult) getIntent().getSerializableExtra("retinue");
				if (retinue != null) {
					newList.add(retinue);
				}
				adapter.notifyDataSetChanged();

			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initView() {
		mListView = (ListView) findViewById(R.id.listview);

		mAdd = (Button) findViewById(R.id.btn_add_retinue);
		title = (TextView) findViewById(R.id.title);
		adapter = new ParternerRecordAdapter(this, newList, this);
		mListView.setAdapter(adapter);
	}

	@Override
	public void click(View v) {
		switch (v.getId()) {
		case R.id.parterner_del:
				if (newList != null) {
					initDialog(newList, v, (Integer) v.getTag());
				}
			// Toast.makeText(this, "删除" + v.getTag(),
			// Toast.LENGTH_SHORT).show();

			break;
		case R.id.parterner_edit:
			// Toast.makeText(this, "编辑" + v.getTag(),
			// Toast.LENGTH_SHORT).show();
				if (newList != null) {
					editDialog(newList, (Integer) v.getTag());
				}

			break;

		default:
			break;
		}

	}

	/**
	 * 编辑对话框
	 * 
	 * @param list
	 * @param position
	 */
	private void editDialog(final ArrayList<RetinuesResult> list, final int position) {

		LayoutInflater inflater = getLayoutInflater();
		View layout = View.inflate(this, R.layout.edit_dialog, null);
		final EditText etName = (EditText) layout.findViewById(R.id.et_name);
		final EditText etIdCard = (EditText) layout.findViewById(R.id.et_idcard);
		if(list!=null){
			etName.setText(list.get(position).name);
			etIdCard.setText(list.get(position).idcard);
		}
		
		new AlertDialog.Builder(this).setTitle("请填写要修改的内容").setView(layout)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (TextUtils.isEmpty(etName.getText().toString())) {
							Toast.makeText(ParternerRecordActivity.this, "请输入姓名", Toast.LENGTH_LONG).show();
							return;
						}
						if (TextUtils.isEmpty(etIdCard.getText().toString())) {
							Toast.makeText(ParternerRecordActivity.this, "请输入身份证号", Toast.LENGTH_LONG).show();
							return;
						} else if (etIdCard.getText().toString().length() < 18) {
							Toast.makeText(ParternerRecordActivity.this, "身份证号位数错误", Toast.LENGTH_LONG).show();
							return;
						}

						if (list != null && adapter != null) {
							list.remove(position);
							list.add(new RetinuesResult(etName.getText().toString(), etIdCard.getText().toString()));
							adapter.notifyDataSetChanged();
						}

					}
				}).setNegativeButton("取消", null).show();
	}

	/**
	 * 删除记录对话框
	 * 
	 * @param list
	 * @param v
	 * @param position
	 */
	private void initDialog(final ArrayList<RetinuesResult> list, final View v, final int position) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("是否删除该条记录？").setNegativeButton("是", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 删除房屋
				if (list != null && adapter != null) {
					list.remove(position);
					adapter.notifyDataSetChanged();
				}

			}
		}).setPositiveButton("否", null).show();

	}

	@Override
	public void backFinish(View view) {

		if (mListView.getCount() > 0) {
			Toast.makeText(this, "请点击右上角进行保存", Toast.LENGTH_LONG).show();
		} else {
			super.backFinish(view);
		}

	}

}
