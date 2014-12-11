package rdc.jim.m_flowlayout;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewGroup;
import android.widget.Button;

public class MainActivity extends Activity {
	
	FlowLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		layout = (FlowLayout)findViewById(R.id.my_layout);
		
		for(int i = 0; i<15; i++){
			Button button = new Button(this);
			button.setText("test jim"+i);
			
			layout.addView(button, new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}
		
	}
}
