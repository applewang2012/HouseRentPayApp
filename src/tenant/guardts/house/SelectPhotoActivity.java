package tenant.guardts.house;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import tenant.guardts.house.presenter.HoursePresenter;
import tenant.guardts.house.util.BMapUtil;
import tenant.guardts.house.util.Bimp;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.FileUpLoadUtils;
import tenant.guardts.house.util.ImageItem;
import tenant.guardts.house.util.LogUtil;
import tenant.guardts.house.util.PublicWay;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;


/**
 * 首页面activity
 *
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日  下午11:48:34
 */
public class SelectPhotoActivity extends BaseActivity {

	private GridView noScrollgridview;
	private GridAdapter adapter;
	private View parentView;
	private PopupWindow pop = null;
	private LinearLayout ll_popup;
	public static Bitmap mAddImageBitmap ;
	private  String mAddImageAction = "http://tempuri.org/AddRentImage";
	private HoursePresenter mPresenter;
	private int mUploadNum = 0;
	private String mRentNo = "";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAddImageBitmap = BitmapFactory.decodeResource(
				getResources(),
				R.drawable.icon_addpic_unfocused);
		PublicWay.activityList.add(this);	
		parentView = getLayoutInflater().inflate(R.layout.activity_selectimg, null);
		setContentView(parentView);
		Init();
	}

	public void Init() {
		mRentNo = getIntent().getStringExtra("rentNo");
		mPresenter = new HoursePresenter(getApplicationContext(), this);
		pop = new PopupWindow(SelectPhotoActivity.this);
		
		
		View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);

		ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
		
		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setContentView(view);
		
		RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
		Button bt1 = (Button) view
				.findViewById(R.id.item_popupwindows_camera);
		Button bt2 = (Button) view
				.findViewById(R.id.item_popupwindows_Photo);
		Button bt3 = (Button) view
				.findViewById(R.id.item_popupwindows_cancel);
		parent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				photo();
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(SelectPhotoActivity.this,
						AlbumActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
				pop.dismiss();
				ll_popup.clearAnimation();
				finish();
			}
		});
		bt3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		
		noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);	
		noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridAdapter(this);
		adapter.update();
		noScrollgridview.setAdapter(adapter);
		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == Bimp.tempSelectBitmap.size()) {
					LogUtil.w("ddddddd", "----------");
					ll_popup.startAnimation(AnimationUtils.loadAnimation(SelectPhotoActivity.this,R.anim.activity_translate_in));
					pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
				} else {
					Intent intent = new Intent(SelectPhotoActivity.this, GalleryActivity.class);
					startActivity(intent);
					finish();
				}
			}
		});
		
		Button upLoadImage = (Button)findViewById(R.id.id_upload_selected_image);
		upLoadImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mUploadNum = 1;
				startAddRentHouseImage(mUploadNum);
			}
		});
	}
	
	private void startAddRentHouseImage(int num){
		if (Bimp.tempSelectBitmap.size() < 1){
			LogUtil.w("mingguo", "onclick  bitmap size  "+Bimp.tempSelectBitmap.size());
			Toast.makeText(getApplicationContext(), "请先选择图片", Toast.LENGTH_SHORT).show();
		}else{
			
			addRentHouseImageRequest(num);
			//}
			//for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++){
				LogUtil.w("mingguo", "path  "+Bimp.tempSelectBitmap.get(num-1).getImagePath());
				LogUtil.w("mingguo", "id  "+Bimp.tempSelectBitmap.get(num-1).getImageId());
				LogUtil.w("mingguo", "size  "+Bimp.tempSelectBitmap.get(num-1).getBitmap().getByteCount());
				LogUtil.w("mingguo", "width  "+Bimp.tempSelectBitmap.get(num-1).getBitmap().getWidth()+"  height  "+Bimp.tempSelectBitmap.get(num-1).getBitmap().getHeight());
			//}
		}
	}
	
	private void addRentHouseImageRequest(int num){
		String url = CommonUtil.mUserHost+"services.asmx?op=AddRentImage";
		SoapObject rpc = new SoapObject(CommonUtil.NAMESPACE, CommonUtil.getSoapName(mAddImageAction));
		rpc.addProperty("RentNO", mRentNo);
		rpc.addProperty("fileName", Bimp.tempSelectBitmap.get(num-1).getImageName());
		rpc.addProperty("memo", "");
		rpc.addProperty("userId", CommonUtil.mUserLoginName);
		rpc.addProperty("imageStr", BMapUtil.bitmapToBase64(Bimp.tempSelectBitmap.get(num-1).getBitmap()));
		mPresenter.readyPresentServiceParams(this, url, mAddImageAction, rpc);
		mPresenter.startPresentServiceTask(true);
	}

	@SuppressLint("HandlerLeak")
	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private int selectedPosition = -1;
		private boolean shape;

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void update() {
			loading();
		}

		public int getCount() {
			if(Bimp.tempSelectBitmap.size() == 6){
				return 6;
			}
			return (Bimp.tempSelectBitmap.size() + 1);
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_published_grida,
						parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == Bimp.tempSelectBitmap.size()) {
				holder.image.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.icon_addpic_unfocused));
				if (position == 6) {
					holder.image.setVisibility(View.GONE);
				}
			} else {
				holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position).getBitmap());
			}

			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
		}

		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					adapter.notifyDataSetChanged();
					break;
				}
				super.handleMessage(msg);
			}
		};

		public void loading() {
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						if (Bimp.max == Bimp.tempSelectBitmap.size()) {
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
							break;
						} else {
							Bimp.max += 1;
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
						}
					}
				}
			}).start();
		}
	}

	public String getString(String s) {
		String path = null;
		if (s == null)
			return "";
		for (int i = s.length() - 1; i > 0; i++) {
			s.charAt(i);
		}
		return path;
	}

	protected void onRestart() {
		adapter.update();
		super.onRestart();
	}

	private static final int TAKE_PICTURE = 0x000001;

	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE:
			if (Bimp.tempSelectBitmap.size() < 6 && resultCode == RESULT_OK) {
				
				String fileName = String.valueOf(System.currentTimeMillis());
				Bitmap bm = (Bitmap) data.getExtras().get("data");
				FileUpLoadUtils.saveBitmap(bm, fileName);
				
				ImageItem takePhoto = new ImageItem();
				takePhoto.setBitmap(bm);
				Bimp.tempSelectBitmap.add(takePhoto);
			}
			break;
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			for(int i=0;i<PublicWay.activityList.size();i++){
				if (null != PublicWay.activityList.get(i)) {
					PublicWay.activityList.get(i).finish();
				}
			}
			System.exit(0);
		}
		return true;
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			if (msg.what == 100){
				if (msg.obj != null){
					JSONObject object;
					try {
						object = new JSONObject((String)msg.obj);
						String ret = object.optString("ret");
						if (ret != null && ret.equals("0")){
							mUploadNum++;
							LogUtil.e("mingguo", "  upload num  "+mUploadNum);
							if (mUploadNum <= Bimp.tempSelectBitmap.size()){
								startAddRentHouseImage(mUploadNum);
							}else if (mUploadNum > Bimp.tempSelectBitmap.size()){
								LogUtil.e("mingguo", "  upload Bimp.tempSelectBitmap.size()  "+Bimp.tempSelectBitmap.size());
								Toast.makeText(getApplicationContext(), "上传图片完成！", Toast.LENGTH_SHORT).show();
								SelectPhotoActivity.this.finish();
							}
						}else{
							Toast.makeText(getApplicationContext(), "上传图片失败！", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		}
	};

	@Override
	public void onStatusSuccess(String action, String templateInfo) {
		super.onStatusSuccess(action, templateInfo);
		LogUtil.w("mingguo", "on success  action " + action + "  msg  " + templateInfo);
		if (action != null && templateInfo != null){
			if (action.equals(mAddImageAction)){
				Message message = mHandler.obtainMessage();
				message.what = 100;
				message.obj = templateInfo;
				mHandler.sendMessage(message);
			}
		}
	}


	@Override
	public void onStatusError(String action, String error) {
		super.onStatusError(action, error);
		
		
	}

}

