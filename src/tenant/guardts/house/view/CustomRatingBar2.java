package tenant.guardts.house.view;

import tenant.guardts.house.R;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class CustomRatingBar2 extends LinearLayout {

	private View view;
	private CheckBox box1;
	private CheckBox box2;
	private CheckBox box3;
	private CheckBox box4;
	private CheckBox box5;

	private CheckBox[] box;
	private int score;

	private int getScore() {

		return score;
	}

	public void setScore(int score) {

		this.score = score;
		invalidate();
	}

	public CustomRatingBar2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		view = LayoutInflater.from(context).inflate(R.layout.rating_bar2, this, true);
		box1 = (CheckBox) view.findViewById(R.id.c1);
		box2 = (CheckBox) view.findViewById(R.id.c2);
		box3 = (CheckBox) view.findViewById(R.id.c3);
		box4 = (CheckBox) view.findViewById(R.id.c4);
		box5 = (CheckBox) view.findViewById(R.id.c5);
		box = new CheckBox[] { box1, box2, box3, box4, box5 };
		if (getScore() != 0) {
			for (int i = 0; i < 5; i++) {
				int score = getScore();
				if (i < score) {
					box[i].setChecked(true);
				}
			}
		}

	}

	public CustomRatingBar2(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomRatingBar2(Context context) {
		this(context, null);
	}

}
