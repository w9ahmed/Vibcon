package com.asyn.vibcon;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
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

import com.google.android.gms.ads.AdView;

public class MainActivity extends Activity {
	
	public final static int TO_SECONDS = 1000;
	
	public final static int FIRST_ITEM = 0;
	public final static int SECOND_ITEM = 1;
	
	protected Vibrator vibrator;
	//private InputMethodManager keyboard;
	
	protected Button startNormalButton;
	protected Button startPatternButton;
	
	protected EditText durationField;
	protected Spinner unitSpinner;
	
	protected EditText strikeField;
	protected EditText pausesField;
	protected Spinner strikeUnitSpinner;
	protected Spinner pausesUnitSpinner;
	protected CheckBox repeatCheckBox;
	
	protected boolean vibratorIsOn;
	protected int patternDuration;
	
	protected Spinner layoutSpinner;
	protected LayoutInflater inflater;
	protected LinearLayout hostLayout;
	
	private View normalView;
	private View patternedView;
	private AdView adView;
	//private AdsLoader advertise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_main); 
        
        //keyboard = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        
        layoutSpinner = (Spinner) findViewById(R.id.optionsSpinner);
        vibratorIsOn = false;
        adView = (AdView) findViewById(R.id.adView);
        /*advertise = */new AdsLoader(adView);
        
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        hostLayout = (LinearLayout) findViewById(R.id.hostLayout);
        normalView = inflater.inflate(R.layout.normal_main, null); // TODO
        patternedView = inflater.inflate(R.layout.patternized_main, null); // TODO
        initializeNormalViewComponents(normalView);
        initializePatternedViewComponents(patternedView);
        hostLayout.addView(normalView);
        
        layoutSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				vibrator.cancel();
				vibratorIsOn = false;
				View view = null;
				//setNormalToNull();
				hostLayout.removeAllViews();
				switch (layoutSpinner.getSelectedItemPosition()) {
				case FIRST_ITEM:
					view = normalView;
					initializeNormalViewComponents(view);
					startNormalButton.setText(R.string.start_vibration_button);
					break;

				case SECOND_ITEM:
					view = patternedView;
					initializePatternedViewComponents(view);
					startPatternButton.setText(R.string.start_vibration_button);
					/*strikeField.setOnTouchListener(onTouchListener);
					pausesField.setOnTouchListener(onTouchListener);*/
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
				if(!repeatCheckBox.isChecked())
					changeButtonTextAfter(patternDuration);
			}
		});
        
    }
    
    
    private void initializeNormalViewComponents(View view) {
    	durationField = (EditText) view.findViewById(R.id.durationField);
    	startNormalButton = (Button) view.findViewById(R.id.startButton);
    	unitSpinner = (Spinner) view.findViewById(R.id.strikesUnitSpinner);
    }
    
   /* private void setNormalToNull() {
    	durationField = null;
    	strikeField = null;startNormalButton = null;
    	pausesField = null;
    	unitSpinner = null;
    }*/
    
    private void initializePatternedViewComponents(View view) {
    	strikeField = (EditText) view.findViewById(R.id.strikeField);
    	pausesField = (EditText) view.findViewById(R.id.pauseField);
    	repeatCheckBox = (CheckBox) view.findViewById(R.id.repeatCheckBox);
    	startPatternButton = (Button) view.findViewById(R.id.patternButton);
    	strikeUnitSpinner = (Spinner) view.findViewById(R.id.strikesUnitSpinner);
    	pausesUnitSpinner = (Spinner) view.findViewById(R.id.pausesUnitSpinner);
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
    	
    	if(strikeUnitSpinner.getSelectedItemPosition() == FIRST_ITEM)
    		strike = strike * TO_SECONDS;
    	if(pausesUnitSpinner.getSelectedItemPosition() == FIRST_ITEM)
    		pauses = pauses * TO_SECONDS;
    	
    	patternDuration = strike + pauses;    	
    	
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
    
  /*  protected OnTouchListener onTouchListener = new OnTouchListener() {		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			advertise.setVisible(false);
			return false;
		}
	};*/
	
	/*protected void detectKeyboardStatus() {
		if(keyboard.isAcceptingText())
			advertise.setVisible(false);
		else
			advertise.setVisible(true);
	}*/
	
	@Override
	protected void onResume() {
		super.onResume();
		//detectKeyboardStatus();
	}

}
