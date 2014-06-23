package com.asyn.vibcon;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class AdsLoader {
		
	private AdView adView;
	private AdRequest adRequest;
	
	public AdsLoader(AdView view) {
		adView = view;
		adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
	} // end constructor
	
	public void setVisible(boolean visible) {
		if(visible)
			adView.setVisibility(AdView.VISIBLE);
		else
			adView.setVisibility(AdView.INVISIBLE);
	}

} // end class AdLoader
