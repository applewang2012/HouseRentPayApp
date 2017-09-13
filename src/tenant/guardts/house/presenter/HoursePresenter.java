package tenant.guardts.house.presenter;

import java.util.HashMap;

import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.content.Context;
import tenant.guardts.house.MapActivity;
import tenant.guardts.house.R;
import tenant.guardts.house.impl.DataStatusInterface;
import tenant.guardts.house.model.DataModel;

public class HoursePresenter {
	private Context mContext;
	private DataStatusInterface mDataInterface;
	private DataModel mDataModel;
	private Activity mActivity;

	public HoursePresenter(Context context, DataStatusInterface statusInterface) {
		mContext = context;
		mDataInterface = statusInterface;
		mDataModel = new DataModel(this);
	}
	
	public void readyPresentServiceParams(Activity ctx, String url, String action, SoapObject object){
		mActivity = ctx;
		mDataModel.setAsyncTaskReady(ctx, url, action, object);
	}
	public void startPresentServiceTask(boolean loading){
		if (loading){
			mDataInterface.onStatusStart(mActivity);
		}
		mDataModel.startDataRequestTask();
	}
	public void readyPresentHttpServiceParams(Context ctx, String url, HashMap<String, String> data){
		mDataModel.setHttpTaskReady(ctx, url, data);
	}
	
	public void startPresentHttpServiceTask(){
		mDataModel.startHttpRequestTask();
		mDataInterface.onStatusStart(mActivity);
	}
	
	public void notifyDataRequestSuccess(String action, String value){
		mDataInterface.onStatusSuccess(action, value);
	}
	
	public void notifyDataRequestError(String action, String error){
		mDataInterface.onStatusError(action, error);
	}
}
