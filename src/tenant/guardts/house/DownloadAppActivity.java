package tenant.guardts.house;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import tenant.guardts.house.download.DownloadManager;
import tenant.guardts.house.downloadui.DownloadAdapter;
import tenant.guardts.house.downloadui.DownloadSelectListener;
import tenant.guardts.house.util.CommonUtil;
import tenant.guardts.house.util.LogUtil;

public class DownloadAppActivity extends BaseActivity{

	private DownloadManager mDownloadManager;
	private DownloadAdapter mSizeSortedAdapter;
	private ListView mSizeOrderedListView;
	private Cursor mSizeSortedCursor;
	private int mIdColumnId;
	private int mStatusColumnId;
	private long mQueuedDownloadId;
	private AlertDialog mQueuedDialog;
	private int mLocalUriColumnId;
	private int mMediaTypeColumnId;
	private int mReasonColumndId;
	private Button donwloadButton;
	private Button mDownloadButton;
	//private int mDownloadId;
	private int mDownloadUrlColumnId;
	private int mCurrentDownloadId;
	private PackageInstallerReceiver mInstallerReceiver = new PackageInstallerReceiver();
	private static int mUpdateDownloadProgress = 0;
	private ProgressBar mProgressBar;
	private TextView mShowProgress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.download_app_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		TextView titlebar  = (TextView)findViewById(R.id.id_titlebar);
		titlebar.setText("下载更新");
		FrameLayout backButton = (FrameLayout) findViewById(R.id.id_titlebar_back);
		backButton.setVisibility(View.INVISIBLE);
		mProgressBar = (ProgressBar) findViewById(R.id.download_progress);
		mShowProgress = (TextView)findViewById(R.id.status_text);
		ImageView iconView = (ImageView)findViewById(R.id.download_icon);
		iconView.setImageResource(R.drawable.icon_house);
		mDownloadButton = (Button)findViewById(R.id.id_download_button);
		mDownloadButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				changeDownloadStatus();
			}
		});
//		registerReceiverData();
//		initDownloadAdapter();
//		
//		checkDownloadFile();
//		changeDownloadStatus();
		startDownloadFileTask();
		mUpdateHandler.sendEmptyMessage(100);
	}
	
	private void startDownloadFileTask(){
		new AsyncTask<Void, Void, Integer>() {

			@Override
			protected Integer doInBackground(Void... params) {
				downloadFile(CommonUtil.DOWLOAD_URL, CommonUtil.getDownloadPathWithName(CommonUtil.DOWLOAD_URL,DownloadAppActivity.this));
				//publishProgress();
				return null;
			}
			
			

			@Override
			protected void onProgressUpdate(Void... values) {
				// TODO Auto-generated method stub
				super.onProgressUpdate(values);
				
			}



			@Override
			protected void onPostExecute(Integer result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
			}
			
		}.execute();
	}
	
	private Handler mUpdateHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Log.i("mingguo", "update handler  "+mUpdateDownloadProgress);
			if (mUpdateDownloadProgress < 100){
				mProgressBar.setProgress(mUpdateDownloadProgress);
				mUpdateHandler.sendEmptyMessageDelayed(100, 800);
			}else{
				mProgressBar.setProgress(100);
				mUpdateHandler.removeCallbacksAndMessages(null);
			}
			changeDownloadStatus();
		}
		
	};
	
	public static boolean downloadFile(String downloadUrl, String saveFilePath) {  
        int fileSize = -1;  
        int downFileSize = 0;  
        boolean result = false;  
        int progress = 0;  
        InputStream inputStream = null;
    	FileOutputStream fos = null;
        try {  
        	
            URL url = new URL(downloadUrl);  
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();  
            if (null == connection) {  
                return false;  
            }  
            connection.setConnectTimeout(8000);
			connection.setInstanceFollowRedirects(true);
			connection.setDoInput(true);
			connection.connect();
			
			inputStream = connection.getInputStream();
			if (inputStream == null) {
				return false;
			}
			
			fos = new FileOutputStream(saveFilePath);
			
			byte[] buffer = new byte[4096];
			int read_size = 0;
			fileSize = connection.getContentLength();  
			while((read_size = inputStream.read(buffer, 0, 4096)) >= 0) {
				//Log.d("mingguo", "start download  size  "+read_size+"  file size  "+fileSize);
				downFileSize = downFileSize + read_size;  
				progress = (int) (downFileSize * 100.0 / fileSize);  
				fos.write(buffer, 0, read_size);
				if (downFileSize == fileSize) {  
                    // 下载完成  
                    mUpdateDownloadProgress = 100;
                } else{  
                    
                    mUpdateDownloadProgress = progress;
                }  
			}
			
        } catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}

				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        return result;  
    }  
	
	private void checkDownloadFile2(){
		
		mDownloadButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				changeDownloadStatus();
			}
		});
	}
	
	
	private void checkDownloadFile(){
		mDownloadButton = (Button)findViewById(R.id.id_download_button);
		mDownloadButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				changeDownloadStatus();
			}
		});
	}
	
	private void initDownloadAdapter(){
		if (mDownloadManager == null) {
			mDownloadManager = new DownloadManager(
					getApplicationContext().getContentResolver(), getApplicationContext().getPackageName());
		}
		//mSizeOrderedListView = (ListView) findViewById(R.id.size_ordered_list);
		mSizeOrderedListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				
			}
			
		});
		mDownloadManager.setAccessAllDownloads(true);
		DownloadManager.Query baseQuery = new DownloadManager.Query()
			.setOnlyIncludeVisibleInDownloadsUi(true);
		mSizeSortedCursor = mDownloadManager.query(baseQuery.orderBy(
				DownloadManager.COLUMN_TOTAL_SIZE_BYTES,
				DownloadManager.Query.ORDER_DESCENDING));
		
		
	    mIdColumnId = mSizeSortedCursor
			    .getColumnIndexOrThrow(DownloadManager.COLUMN_ID);
	    mStatusColumnId = mSizeSortedCursor
			    .getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS);
	    mLocalUriColumnId = mSizeSortedCursor
			    .getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI);
	    mDownloadUrlColumnId = mSizeSortedCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_URI);
	    mMediaTypeColumnId = mSizeSortedCursor
			    .getColumnIndexOrThrow(DownloadManager.COLUMN_MEDIA_TYPE);
		mReasonColumndId = mSizeSortedCursor
			    .getColumnIndexOrThrow(DownloadManager.COLUMN_REASON);
		mSizeSortedAdapter = new DownloadAdapter(DownloadAppActivity.this, mSizeSortedCursor,new DownloadSelectListener() {
			
			@Override
			public void onDownloadSelectionChanged(long downloadId, boolean isSelected) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isDownloadSelected(long id) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void downloadStatus(int status) {
				updateDownloadStatus(status);
			}
		});
		mSizeOrderedListView.setAdapter(mSizeSortedAdapter);
	}
	
	
	private int queryDownloadStatus(String downloadpath){
		for (mSizeSortedCursor.moveToFirst(); !mSizeSortedCursor.isAfterLast(); mSizeSortedCursor
        		.moveToNext()) {
			String databasepath = mSizeSortedCursor.getString(mLocalUriColumnId);
			String downloadUrl = mSizeSortedCursor.getString(mDownloadUrlColumnId);
			LogUtil.w("mingguo", "database  download path  "+databasepath+"  download url  "+downloadUrl);
			if (downloadUrl != null && downloadUrl.equalsIgnoreCase(CommonUtil.DOWLOAD_URL)){
				int downloadId =  mSizeSortedCursor.getInt(mIdColumnId);
				if (downloadpath == null){
					LogUtil.e("mingguo", "download path null  remove download id "+downloadId);
					mDownloadManager.remove(downloadId);
				}
				//if (databasepath.equalsIgnoreCase("file://"+downloadpath)){
					mCurrentDownloadId =  mSizeSortedCursor.getInt(mIdColumnId);
					if (downloadpath != null && new File(downloadpath).exists()){
						return mSizeSortedCursor.getInt(mStatusColumnId);
					}
				//}
			}
			
		}
		if (downloadpath != null && new File(downloadpath).exists()){
			LogUtil.e("mingguo", "download database null delete download file ");
			new File(downloadpath).delete();
		}
		return -1;
	}
	
	private void updateDownloadStatus(int status){
		switch (status) {
		case DownloadManager.STATUS_PENDING:
		case DownloadManager.STATUS_RUNNING:
			mDownloadButton.setText("下载中，点击暂停下载");
			break;
		case DownloadManager.STATUS_PAUSED:
			mDownloadButton.setText("已暂停，点击继续下载");
			break;
		case DownloadManager.STATUS_SUCCESSFUL:
			mDownloadButton.setText("下载完成，点击安装");
			String downloadfile = CommonUtil.getDefaultDownloadPath(CommonUtil.DOWLOAD_URL,this);
			CommonUtil.installApk(DownloadAppActivity.this, downloadfile);
			break;
		default:
			mDownloadButton.setText("未下载，点击下载");
			break;
		}
	}
	
	private void changeDownloadStatus(){
		
		if (mUpdateDownloadProgress < 100){
			mDownloadButton.setText("下载中，请稍后");
			mShowProgress.setText("下载中："+mUpdateDownloadProgress+"%");
		}else if (mUpdateDownloadProgress == 100){
			mDownloadButton.setText("下载完成，点击安装");
			mShowProgress.setText("下载完成");
			CommonUtil.installApk(DownloadAppActivity.this, CommonUtil.getDownloadPathWithName(CommonUtil.DOWLOAD_URL,this));
		}
		
	}
	
	public class PackageInstallerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
        	//PackageManager manager = context.getPackageManager();
        	LogUtil.e("mingguo", "onReceiver  ");
//        	if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED) || intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)){
//        		String packageName = intent.getData().getSchemeSpecificPart();
//        		LogUtil.e("mingguo", "package name "+packageName);
//        		if (packageName != null && packageName.equals(GlobalUtil.getPackageName(getApplicationContext()))){
//        			mDownloadManager.remove(mCurrentDownloadId);
//        			new File(CommonUtil.getDefaultDownloadPath(CommonUtil.DOWLOAD_URL)).delete();
//        			finish();
//        		}
//        	}
        }
	}
	
	private void registerReceiverData(){
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
		intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		intentFilter.addDataScheme("package"); 
		 
		this.registerReceiver(mInstallerReceiver, intentFilter);
	}
	
	private void unregisterReceiver(){
		this.unregisterReceiver(mInstallerReceiver);
	}

	@Override
	protected void onDestroy() {
		mUpdateHandler.removeCallbacksAndMessages(null);
		//unregisterReceiver();
		super.onDestroy();
	}
	
	

}
