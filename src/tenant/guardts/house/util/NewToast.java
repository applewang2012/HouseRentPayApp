package tenant.guardts.house.util;



import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;


public class NewToast {
	private Context mContext;
	private Drawable mBackground;
	private int mWidth;
	private int mHeight;
	private int mDuration;
	private int mAnimationStyle;
	private int mSoftInputMode;
	private boolean mIsShowing;
	private View mPopupView;
	private View mContentView;
	private int mInputMethodMode = PopupWindow.INPUT_METHOD_FROM_FOCUSABLE;
	private boolean mFocusable;
	private boolean mTouchable = true;
	private boolean mOutsideTouchable = true;
	private boolean mClippingEnabled = true;
	private boolean mIgnoreCheekPress = false;
	private WindowManager mWindowManager;
	private OnDismissListener mOnDismissListener;
	private OnTouchListener mTouchInterceptor;
	
	private final int DISMISS = 1001;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
				case DISMISS:
				 if(NewToast.this.getContentView().isShown()){
					 dismiss();
				 }
				break;
			}
		}
		
	};
	
	public NewToast(Context context) {
		mContext = context;
		mWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
	}

	/** to set duration for this toast.
	 * @param duration  the display time .
	 */
	public void setDuration(int duration){
		mDuration = duration;
	}
	public Drawable getBackground() {
		return mBackground;
	}

	public void setBackgroundDrawable(Drawable background) {
		mBackground = background;
	}

	public void setAnimationStyle(int animationStyle) {
		mAnimationStyle = animationStyle;
	}


	public View getContentView() {
		return mContentView;
	}


	public void setContentView(View contentView) {
		if (isShowing()) {
			return;
		}
		mContentView = contentView;

		if (mContext == null) {
			mContext = mContentView.getContext();
		}
		if (mWindowManager == null) {
			mWindowManager = (WindowManager) mContext
					.getSystemService(Context.WINDOW_SERVICE);
		}
	}


	public int getHeight() {
		return mHeight;
	}


	public void setHeight(int height) {
		mHeight = height;
	}

	public int getWidth() {
		return mWidth;
	}

	public void setWidth(int width) {
		mWidth = width;
	}

	public void setSoftInputMode(int mode) {
		mSoftInputMode = mode;
	}

	public int getSoftInputMode() {
		return mSoftInputMode;
	}

	public boolean isShowing() {
		return mIsShowing;
	}

	public interface OnDismissListener {
		public void onDismiss();
	}

	public void setOnDismissListener(OnDismissListener onDismissListener) {
		mOnDismissListener = onDismissListener;
	}

	public void setTouchInterceptor(OnTouchListener l) {
		mTouchInterceptor = l;
	}

	public void dismiss() {
		if (isShowing() && mPopupView != null) {
			if(mHandler != null){
				mHandler = null;
			}
			try {
				mWindowManager.removeView(mPopupView);
			} finally {
				if (mPopupView != mContentView
						&& mPopupView instanceof ViewGroup) {
					((ViewGroup) mPopupView).removeView(mContentView);
				}
				mPopupView = null;
				mIsShowing = false;

				if (mOnDismissListener != null) {
					mOnDismissListener.onDismiss();
				}
			}
		}
	}

	public void show() {
		if (isShowing() || mContentView == null) {
			return;
		}
		mIsShowing = true;
		WindowManager.LayoutParams p = createPopupLayout();
		if (mAnimationStyle > 0) {
			p.windowAnimations = mAnimationStyle;
		}
		preparePopup(p);
		invokePopup(p);
		
		if(mDuration <= 0){
			mDuration = 1000;//默认显示1秒。
		}
		mHandler.sendEmptyMessageDelayed(DISMISS, mDuration);

	}

	private void invokePopup(WindowManager.LayoutParams p) {
		p.packageName = mContext.getPackageName();
		mWindowManager.addView(mPopupView, p);
	}

	private void preparePopup(WindowManager.LayoutParams p) {
		if (mContentView == null || mContext == null || mWindowManager == null) {
			throw new IllegalStateException(
					"You must specify a valid content view by "
							+ "calling setContentView() before attempting to show the popup.");
		}
		final ViewGroup.LayoutParams layoutParams = mContentView
				.getLayoutParams();
		int height = ViewGroup.LayoutParams.MATCH_PARENT;
		if (layoutParams != null
				&& layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
			height = ViewGroup.LayoutParams.WRAP_CONTENT;
		}
		// when a background is available, we embed the content view
		// within another view that owns the background drawable
		PopupViewContainer popupViewContainer = new PopupViewContainer(mContext);
		PopupViewContainer.LayoutParams listParams = new PopupViewContainer.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, height);
		if (mBackground != null) {
			popupViewContainer.setBackgroundDrawable(mBackground);
		}
		popupViewContainer.addView(mContentView, listParams);
		mPopupView = popupViewContainer;
	}

	private WindowManager.LayoutParams createPopupLayout() {
		// generates the layout parameters for the drop down
		// we want a fixed size view located at the bottom left of the anchor
		WindowManager.LayoutParams p = new WindowManager.LayoutParams();
		p.gravity = Gravity.LEFT | Gravity.TOP;
		if(mWidth <= 0){
			mWidth = WindowManager.LayoutParams.WRAP_CONTENT;
		}
		if(mHeight <= 0){
			mHeight = WindowManager.LayoutParams.WRAP_CONTENT;
		}
		p.width = mWidth;
		p.height = mHeight;
		if (mBackground != null) {
			p.format = mBackground.getOpacity();
		} else {
			p.format = PixelFormat.TRANSLUCENT;
		}
		p.flags = computeFlags(p.flags);
		p.type = WindowManager.LayoutParams.TYPE_TOAST;
		p.softInputMode = mSoftInputMode;
		p.gravity = Gravity.CENTER;
		p.setTitle("PopupWindow:" + Integer.toHexString(hashCode()));

		return p;
	}

	public void setIgnoreCheekPress() {
		mIgnoreCheekPress = true;
	}

	private int computeFlags(int curFlags) {
		curFlags &= ~(WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		if (mIgnoreCheekPress) {
			curFlags |= WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES;
		}
		if (!mFocusable) {
			curFlags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
			if (mInputMethodMode == PopupWindow.INPUT_METHOD_NEEDED) {
				curFlags |= WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
			}
		} else if (mInputMethodMode == PopupWindow.INPUT_METHOD_NOT_NEEDED) {
			curFlags |= WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
		}
		if (!mTouchable) {
			curFlags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
		}
		if (mOutsideTouchable) {
			curFlags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		}
		if (!mClippingEnabled) {
			curFlags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		}
		return curFlags;
	}

	private class PopupViewContainer extends FrameLayout {

		public PopupViewContainer(Context context) {
			super(context);
		}

		@Override
		public boolean dispatchKeyEvent(KeyEvent event) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				if (event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getRepeatCount() == 0) {
					getKeyDispatcherState().startTracking(event, this);
					return true;
				} else if (event.getAction() == KeyEvent.ACTION_UP
						&& getKeyDispatcherState().isTracking(event)
						&& !event.isCanceled()) {
					dismiss();
					return true;
				}
				return super.dispatchKeyEvent(event);
			} else {
				return super.dispatchKeyEvent(event);
			}
		}

		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
			if (mTouchInterceptor != null
					&& mTouchInterceptor.onTouch(this, ev)) {
				return true;
			}
			return super.dispatchTouchEvent(ev);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			final int x = (int) event.getX();
			final int y = (int) event.getY();
			if ((event.getAction() == MotionEvent.ACTION_DOWN)
					&& ((x < 0) || (x >= getWidth()) || (y < 0) || (y >= getHeight()))) {
				dismiss();
				return true;
			} else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
				dismiss();
				return true;
			} else {
				return super.onTouchEvent(event);
			}
		}

		@Override
		public void sendAccessibilityEvent(int eventType) {
			if (mContentView != null) {
				mContentView.sendAccessibilityEvent(eventType);
			} else {
				super.sendAccessibilityEvent(eventType);
			}
		}
	}
}
