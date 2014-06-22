package com.asyn.vibcon;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	
	public final static int TO_SECONDS = 1000;
	
	public final static int FIRST_ITEM = 0;
	public final static int SECOND_ITEM = 1;
	
	protected Vibrator vibrator;
	
	protected Button startNormalButton;
	protected Button startPatternButton;
	
	protected EditText durationField;
	protected Spinner unitSpinner;
	
	protected EditText strikeField;
	protected EditText pausesField;
	protected CheckBox repeatCheckBox;
	
	protected boolean vibratorIsOn;
	
	protected Spinner layoutSpinner;
	protected LayoutInflater inflater;
	protected LinearLayout hostLayout;
	
	private View normalView;
	private View patternedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_main); 
        
        layoutSpinner = (Spinner) findViewById(R.id.optionsSpinner);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibratorIsOn = false;
        
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        hostLayout = (LinearLayout) findViewById(R.id.hostLayout);
        normalView = inflater.inflate(R.layout.normal_main, null);
        patternedView = inflater.inflate(R.layout.patternized_main, null);
        initializeNormalViewComponents(normalView);
        initializePatternedViewComponents(patternedView);
        hostLayout.addView(normalView);
        
        layoutSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				View view = null;
				setNormalToNull();
				hostLayout.removeAllViews();
				switch (layoutSpinner.getSelectedItemPosition()) {
				case FIRST_ITEM:
					view = normalView;
					initializeNormalViewComponents(view);
					break;

				case SECOND_ITEM:
					view = patternedView;
					initializePatternedViewComponents(view);
					break;
				}
				hostLayout.addView(view);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				initializeNormalViewComponents(normalView);
				hostLayout.addView(normalView);
			}
		});
        
        startNormalButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startNormalVibration();
			}
		});        
        
        startPatternButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				startPatternedVibration();
			}
		});
        
    }
    
    
    private void initializeNormalViewComponents(View view) {
    	durationField = (EditText) view.findViewById(R.id.durationField);
    	startNormalButton = (Button) view.findViewById(R.id.startButton);
    	unitSpinner = (Spinner) view.findViewById(R.id.unitSpinner);
    }
    
    private void setNormalToNull() {
    	durationField = null;
    	strikeField = null;startNormalButton = null;
    	pausesField = null;
    	unitSpinner = null;
    }
    
    private void initializePatternedViewComponents(View view) {
    	strikeField = (EditText) view.findViewById(R.id.strikeField);
    	pausesField = (EditText) view.findViewById(R.id.pauseField);
    	repeatCheckBox = (CheckBox) view.findViewById(R.id.repeatCheckBox);
    	startPatternButton = (Button) view.findViewById(R.id.patternButton);
    	unitSpinner = (Spinner) view.findViewById(R.id.unitSpinner);
    }
    
    private int checkInput(EditText field) {
    	if(!field.getText().toString().isEmpty()) {
    		int input = Integer.parseInt(field.getText().toString());
    		return input;
    	}
    	return 0;
    }
    
    private void changeButtonTextAfter(int duration) {
    	new Handler().postDelayed(new Runnable() {			
			@Override
			public void run() {
				switch (layoutSpinner.getSelectedItemPosition()) {
				case FIRST_ITEM:
					startNormalButton.setText(R.string.start_vibration_button);
					break;

				case SECOND_ITEM:
					startPatternButton.setText(R.string.start_vibration_button);
					break;
				}
				vibratorIsOn = false; // this is to set vibratorIsOn to false after duration completion
			}
		}, duration);
    }
    
    private void startNormalVibration() {
		int duration = checkInput(durationField);
		if(unitSpinner.getSelectedItemPosition() == FIRST_ITEM)
			duration = duration * TO_SECONDS;
		
		if(duration>0 && !vibratorIsOn) {
			vibratorIsOn = true;
			vibrator.vibrate(duration);
			startNormalButton.setText(R.string.stop_vibration_button);
			changeButtonTextAfter(duration);
		} else if(vibratorIsOn) {
			vibratorIsOn = false;
			vibrator.cancel();
			startNormalButton.setText(R.string.start_vibration_button);
		} else {
			Toast.makeText(this, "Enter Duration", Toast.LENGTH_SHORT).show();
		}
	}
    
    private void startPatternedVibration() {
    	boolean status = false;
    	int strike = checkInput(strikeField);
    	int pauses = checkInput(pausesField);
    	int repeat = -1;
    	
    	if(repeatCheckBox.isChecked())
    		repeat = 0;
    	
    	if(strike>0 && pauses>=0)
    		status = true;
    	
    	
    	if(unitSpinner.getSelectedItemPosition() == FIRST_ITEM) {
    		strike = strike * TO_SECONDS;
    		pauses = pauses * TO_SECONDS;
    	}
    	
    	
    	long stk = (long) strike;
    	long pse = (long) pauses;
    	
    	if(status && !vibratorIsOn) {
    		vibratorIsOn = true;
    		vibrator.vibrate(new long[]{0,stk, pse}, repeat);
    		startPatternButton.setText(R.string.stop_vibration_button);
    	} else if (vibratorIsOn) {
    		vibratorIsOn = false;
    		vibrator.cancel();
    		startPatternButton.setText(R.string.start_vibration_button);
    	} else {
    		Toast.makeText(this, "Make sure you entered correct information.", Toast.LENGTH_SHORT).show();
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
