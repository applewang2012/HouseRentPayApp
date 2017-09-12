package tenant.guardts.house.impl;

import android.app.Activity;
import android.content.Context;
import android.view.View;

public interface DataStatusInterface {
	void onStatusSuccess(String action, String templateInfo);
	void onStatusStart(Activity activity);
	void onStatusError(String action, String error);
}
