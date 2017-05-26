package landlord.guardts.house;


import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import landlord.guardts.house.download.DownloadManager;
import landlord.guardts.house.download.DownloadManager.Request;
import landlord.guardts.house.downloadui.DownloadAdapter;
import landlord.guardts.house.downloadui.DownloadSelectListener;
import landlord.guardts.house.util.Constants;

public class MainActivity extends Activity{

	private DownloadManager mDownloadManager;
	private Cursor mSizeSortedCursor;
	private DownloadAdapter mSizeSortedAdapter;
	private ListView mSizeOrderedListView;
	private int mIdColumnId;
	private int mStatusColumnId;
	private long mQueuedDownloadId;
	private AlertDialog mQueuedDialog;
	private int mLocalUriColumnId;
	private int mMediaTypeColumnId;
	private int mReasonColumndId;
	private String TAG = "DownloadList";
	private String mDownloadVideoUrl = "http://p.vod05.icntvcdn.com/media/new/2013/icntv2/media/2016/03/21/gongyiguoshijia.ts";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button map = (Button)findViewById(R.id.id_start_baidu_map);
		map.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(MainActivity.this, OverlayDemo.class));
			}
		});
		Button wechatButton = (Button)findViewById(R.id.id_start_wechat);
		wechatButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
		            //利用Intent打开微信
		            Uri uri = Uri.parse("weixin://wxpay/bizpayurl?pr=MjPm03x");
		            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		            startActivity(intent);
		        } catch (Exception e) {
		            //若无法正常跳转，在此进行错误处理
		            Toast.makeText(MainActivity.this, "无法跳转到微信，请检查您是否安装了微信！", Toast.LENGTH_SHORT).show();
		        }
			}
		});
		if (mDownloadManager == null) {
			mDownloadManager = new DownloadManager(
					getApplicationContext().getContentResolver(), getApplicationContext().getPackageName());
		}
		mSizeOrderedListView = (ListView) findViewById(R.id.size_ordered_list);
		mSizeOrderedListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				handleItemClick(mSizeSortedCursor);
			}
		});
		Button donwload = (Button)findViewById(R.id.id_start_download);
		donwload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Request request = new Request(Uri.parse("http://app.znds.com/down/20170427/dangbeimarket_3.9.9.0_115_znds_0425_74934f3.apk"));
				
				startActivity(new Intent(MainActivity.this, DownloadAppActivity.class));
				
//				String downloadfile = Constants.getDefaultDownloadPath(Constants.DOWLOAD_URL);
//				if (downloadfile == null){
//					Toast.makeText(getApplicationContext(), "文件未找到,开始下载！", Toast.LENGTH_SHORT).show();
//					Request request = new Request(Uri.parse("http://weiyuzhu.cn/HRPApp.apk"));
//					request.setPackageName("com.pay.renthouse");
//					request.setShowRunningNotification(false);
//					request.setMimeType("application/vnd.android.package-archive");
////					request.setIconUrl(mIconCache.get(info.packageName));
////					request.setSourceType(Constants.DOWNLOAD_FROM_MARKET);
////					request.setMD5("82acb1ab0c7949ef2c272ce1db98bc57");
//					long id = mDownloadManager.enqueue(request);
//				}else{
//					Log.i("mingguo", "download path  download file  "+downloadfile);
//					if (queryIsDownloadCompleteDatabase(downloadfile)){
//						Constants.installApk(MainActivity.this, downloadfile);
//						
//					}else{
//						Toast.makeText(getApplicationContext(), "下载未完成，请稍后！", Toast.LENGTH_SHORT).show();
//					}
//					
//				}
			}
		});
		
		Button downloadAdv = (Button)findViewById(R.id.id_start_download_adv);
		downloadAdv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Request request = new Request(Uri.parse(mDownloadVideoUrl));
				//request.setPackageName("com.dangbeimarket");
				request.setShowRunningNotification(false);
				request.setMimeType("video/*");
				long id = mDownloadManager.enqueue(request);
				
			}
		});
		
		Button advButton = (Button)findViewById(R.id.id_start_adv);
		advButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String downloadfile = Constants.getDefaultDownloadPath(mDownloadVideoUrl);
				Log.e("mingguo", "onclick download file path  "+downloadfile+"  curosr  size   "+mSizeSortedCursor.getCount());
				if (downloadfile == null){
					Toast.makeText(getApplicationContext(), "文件未找到！", Toast.LENGTH_SHORT).show();
				}else{
					if (!queryIsDownloadCompleteDatabase(downloadfile)){
						Toast.makeText(getApplicationContext(), "下载未完成，请稍后！", Toast.LENGTH_SHORT).show();
						return;
					}
					Intent intent = new Intent(MainActivity.this, SurfaceViewTestActivity.class);
					intent.putExtra("download_adv", downloadfile);
					startActivity(intent);
				}
				
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
		    mMediaTypeColumnId = mSizeSortedCursor
				    .getColumnIndexOrThrow(DownloadManager.COLUMN_MEDIA_TYPE);
			    mReasonColumndId = mSizeSortedCursor
				    .getColumnIndexOrThrow(DownloadManager.COLUMN_REASON);
			mSizeSortedAdapter = new DownloadAdapter(MainActivity.this, mSizeSortedCursor,new DownloadSelectListener() {
				
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
					// TODO Auto-generated method stub
					
				}
			});
			    mSizeOrderedListView.setAdapter(mSizeSortedAdapter);
	}
	
	private boolean queryIsDownloadCompleteDatabase(String downloadpath){
		Log.i("mingguo", "query database  cursor count  "+mSizeSortedCursor.getCount()+" cursor position  "+mSizeSortedCursor.getPosition());
		for (mSizeSortedCursor.moveToFirst(); !mSizeSortedCursor.isAfterLast(); mSizeSortedCursor
        		.moveToNext()) {
			Log.i("mingguo", "query database  move to next ");
			String databasepath = mSizeSortedCursor.getString(mLocalUriColumnId);
			Log.i("mingguo", "databasepath path  "+databasepath+"  file path  "+downloadpath);
			if (databasepath.equalsIgnoreCase("file://"+downloadpath)){
				if (mSizeSortedCursor.getInt(mStatusColumnId) == DownloadManager.STATUS_SUCCESSFUL){
					return true;
				}else{
					return false;
				}
			}
		}
		return false;
	}

    private void handleItemClick(Cursor cursor) {
	long id = cursor.getInt(mIdColumnId);
	switch (cursor.getInt(mStatusColumnId)) {
	case DownloadManager.STATUS_PENDING:
	case DownloadManager.STATUS_RUNNING:
	    showRunningDialog(id);
	    break;

	case DownloadManager.STATUS_PAUSED:
	    if (isPausedForWifi(cursor)) {
		mQueuedDownloadId = id;
		mQueuedDialog = new AlertDialog.Builder(this)
			.setTitle(R.string.dialog_title_queued_body)
			.setMessage(R.string.dialog_queued_body)
			.setPositiveButton(R.string.keep_queued_download, null)
			.setNegativeButton(R.string.remove_download,
				getDeleteClickHandler(id))
			.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					mQueuedDownloadId = 0;
					mQueuedDialog = null;
				}
			}).show();
	    } else {
		showPausedDialog(id);
	    }
	    break;

	case DownloadManager.STATUS_SUCCESSFUL:
	    openCurrentDownload(cursor);
	    break;

	case DownloadManager.STATUS_FAILED:
	    showFailedDialog(id, getErrorMessage(cursor));
	    break;
	}
    }

    private void showRunningDialog(long downloadId) {
    	new AlertDialog.Builder(this)
    		.setTitle(R.string.download_running)
    		.setMessage(R.string.dialog_running_body)
    		.setNegativeButton(R.string.cancel_running_download,
    			getDeleteClickHandler(downloadId))
    		.setPositiveButton(R.string.pause_download,
    			getPauseClickHandler(downloadId)).show();
        }

        private void showPausedDialog(long downloadId) {
    	new AlertDialog.Builder(this)
    		.setTitle(R.string.download_queued)
    		.setMessage(R.string.dialog_paused_body)
    		.setNegativeButton(R.string.delete_download,
    			getDeleteClickHandler(downloadId))
    		.setPositiveButton(R.string.resume_download,
    			getResumeClickHandler(downloadId)).show();
        }

        private void showFailedDialog(long downloadId, String dialogBody) {
    	new AlertDialog.Builder(this)
    		.setTitle(R.string.dialog_title_not_available)
    		.setMessage(dialogBody)
    		.setNegativeButton(R.string.delete_download,
    			getDeleteClickHandler(downloadId))
    		.setPositiveButton(R.string.retry_download,
    			getRestartClickHandler(downloadId)).show();
        }
        
        private DialogInterface.OnClickListener getPauseClickHandler(
        	    final long downloadId) {
        	return new DialogInterface.OnClickListener() {
        	    @Override
        	    public void onClick(DialogInterface dialog, int which) {
        		mDownloadManager.pauseDownload(downloadId);
        	    }
        	};
            }
        
        private DialogInterface.OnClickListener getResumeClickHandler(
        	    final long downloadId) {
        	return new DialogInterface.OnClickListener() {
        	    @Override
        	    public void onClick(DialogInterface dialog, int which) {
        		mDownloadManager.resumeDownload(downloadId);
        	    }
        	};
            }
        
        private DialogInterface.OnClickListener getRestartClickHandler(
        	    final long downloadId) {
        	return new DialogInterface.OnClickListener() {
        	    @Override
        	    public void onClick(DialogInterface dialog, int which) {
        		mDownloadManager.restartDownload(downloadId);
        	    }
        	};
            }

        
        private boolean isPausedForWifi(Cursor cursor) {
        	return cursor.getInt(mReasonColumndId) == DownloadManager.PAUSED_QUEUED_FOR_WIFI;
            }
        
        private void openCurrentDownload(Cursor cursor) {
        	Uri localUri = Uri.parse(cursor.getString(mLocalUriColumnId));
        	try {
        	    getContentResolver().openFileDescriptor(localUri, "r").close();
        	} catch (FileNotFoundException exc) {
        	    Log.d("downloadlist",
        		    "Failed to open download " + cursor.getLong(mIdColumnId),
        		    exc);
        	    showFailedDialog(cursor.getLong(mIdColumnId),
        		    getString(R.string.dialog_file_missing_body));
        	    return;
        	} catch (IOException exc) {
        	    // close() failed, not a problem
        	}
        	Log.e(TAG , "open  localUri  "+localUri+"   type   "+cursor.getString(mMediaTypeColumnId));
        	Intent intent = new Intent(Intent.ACTION_VIEW);
        	intent.setDataAndType(localUri, cursor.getString(mMediaTypeColumnId));
        	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
        		| Intent.FLAG_GRANT_READ_URI_PERMISSION);
        	try {
        	    startActivity(intent);
        	} catch (ActivityNotFoundException ex) {
        	    Toast.makeText(this, R.string.download_no_application_title,
        		    Toast.LENGTH_LONG).show();
        	}
            }
        
        private DialogInterface.OnClickListener getDeleteClickHandler(
        	    final long downloadId) {
        	return new DialogInterface.OnClickListener() {
        	    @Override
        	    public void onClick(DialogInterface dialog, int which) {
        		deleteDownload(downloadId);
        	    }
        	};
            }
        
        /**
         * Delete a download from the Download Manager.
         */
        private void deleteDownload(long downloadId) {
    	if (moveToDownload(downloadId)) {
    	    int status = mSizeSortedCursor.getInt(mStatusColumnId);
    	    boolean isComplete = status == DownloadManager.STATUS_SUCCESSFUL
    		    || status == DownloadManager.STATUS_FAILED;
    	    String localUri = mSizeSortedCursor.getString(mLocalUriColumnId);
    	    if (isComplete && localUri != null) {
    		String path = Uri.parse(localUri).getPath();
    		if (path.startsWith(Environment.getExternalStorageDirectory()
    			.getPath())) {
    		    mDownloadManager.markRowDeleted(downloadId);
    		    return;
    		}
    	    }
    	}
    	mDownloadManager.remove(downloadId);
        }
        
        private boolean moveToDownload(long downloadId) {
        	for (mSizeSortedCursor.moveToFirst(); !mSizeSortedCursor.isAfterLast(); mSizeSortedCursor
        		.moveToNext()) {
        	    if (mSizeSortedCursor.getLong(mIdColumnId) == downloadId) {
        		return true;
        	    }
        	}
        	return false;
            }

        
        private String getErrorMessage(Cursor cursor) {
        	switch (cursor.getInt(mReasonColumndId)) {
        	case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
        	    if (isOnExternalStorage(cursor)) {
        		return getString(R.string.dialog_file_already_exists);
        	    } else {
        		// the download manager should always find a free filename for
        		// cache downloads,
        		// so this indicates a strange internal error
        		return getUnknownErrorMessage();
        	    }

        	case DownloadManager.ERROR_INSUFFICIENT_SPACE:
        	    if (isOnExternalStorage(cursor)) {
        		return getString(R.string.dialog_insufficient_space_on_external);
        	    } else {
        		return getString(R.string.dialog_insufficient_space_on_cache);
        	    }

        	case DownloadManager.ERROR_DEVICE_NOT_FOUND:
        	    return getString(R.string.dialog_media_not_found);

        	case DownloadManager.ERROR_CANNOT_RESUME:
        	    return getString(R.string.dialog_cannot_resume);

        	default:
        	    return getUnknownErrorMessage();
        	}
            }
        
        private boolean isOnExternalStorage(Cursor cursor) {
        	String localUriString = cursor.getString(mLocalUriColumnId);
        	if (localUriString == null) {
        	    return false;
        	}
        	Uri localUri = Uri.parse(localUriString);
        	if (!localUri.getScheme().equals("file")) {
        	    return false;
        	}
        	String path = localUri.getPath();
        	String externalRoot = Environment.getExternalStorageDirectory()
        		.getPath();
        	return path.startsWith(externalRoot);
            }
        
        private String getUnknownErrorMessage() {
        	return getString(R.string.dialog_failed_body);
            }

}
