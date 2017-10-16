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
		initScore();
	}

	
		
		

	private void initScore() {
		switch (getScore()) {
		case 0:
			box1.setChecked(false);
			box2.setChecked(false);
			box3.setChecked(false);
			box4.setChecked(false);
			box5.setChecked(false);
			break;
		case 1:
			box1.setChecked(true);
			box2.setChecked(false);
			box3.setChecked(false);
			box4.setChecked(false);
			box5.setChecked(false);
			break;
		case 2:
			box1.setChecked(true);
			box2.setChecked(true);
			box3.setChecked(false);
			box4.setChecked(false);
			box5.setChecked(false);
			break;
		case 3:
			box1.setChecked(true);
			box2.setChecked(true);
			box3.setChecked(true);
			box4.setChecked(false);
			box5.setChecked(false);
			break;
		case 4:
			box1.setChecked(true);
			box2.setChecked(true);
			box3.setChecked(true);
			box4.setChecked(true);
			box5.setChecked(false);
			break;
		case 5:
			box1.setChecked(true);
			box2.setChecked(true);
			box3.setChecked(true);
			box4.setChecked(true);
			box5.setChecked(true);
			break;

		default:
			break;
		}
		
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
		
		

	}


	public CustomRatingBar2(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomRatingBar2(Context context) {
		this(context, null);
	}

}
