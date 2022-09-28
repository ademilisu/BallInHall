package com.ademilisu.ballinhall;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class AndroidLauncher extends AndroidApplication  implements AdsService{

	private InterstitialAd interstitialAd;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new BallinHall(this), config);

		loadAd();

	}


	@Override
	public void loadAd() {
		InterstitialAd.load(this, "ca-app-pub-8482280134803556~3677619737", new AdRequest.Builder().build(),
				new InterstitialAdLoadCallback() {
					@Override
					public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
						interstitialAd = null;
					}

					@Override
					public void onAdLoaded(@NonNull InterstitialAd mInterstitialAd) {
						interstitialAd = mInterstitialAd;

						interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
							@Override
							public void onAdClicked() {
								// Called when a click is recorded for an ad.
							}

							@Override
							public void onAdDismissedFullScreenContent() {
								// Called when ad is dismissed.
								// Set the ad reference to null so you don't show the ad a second time.
								interstitialAd = null;
							}

							@Override
							public void onAdFailedToShowFullScreenContent(AdError adError) {
								// Called when ad fails to show.
								interstitialAd = null;
							}

							@Override
							public void onAdImpression() {
								// Called when an impression is recorded for an ad.
							}

							@Override
							public void onAdShowedFullScreenContent() {
								// Called when ad is shown.
							}
						});

					}
				});
	}

	@Override
	public void showAd() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (interstitialAd != null) {
					interstitialAd.show(AndroidLauncher.this);
				} else {
					loadAd();
				}
			}
		});
	}
}
