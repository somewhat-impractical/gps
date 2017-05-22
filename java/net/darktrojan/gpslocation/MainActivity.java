package net.darktrojan.gpslocation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.location.LocationManager;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    static final String LATITUDE_KEY = "latitude";
    static final String LONGITUDE_KEY = "longitude";
	static final String ALTITUDE_KEY = "altitude";
	static final String PROVIDER_KEY = "provider";

	static final String LATITUDE_COARSE_KEY = "latitude_coarse";
	static final String LONGITUDE_COARSE_KEY = "longitude_coarse";
	static final String ALTITUDE_COARSE_KEY = "altitude_coarse";
	static final String PROVIDER_COARSE_KEY = "provider_coarse";

	LocationManager lm;
    SharedPreferences prefs;
	MyLocationListener fineListener = new MyLocationListener(
			LATITUDE_KEY, LONGITUDE_KEY, ALTITUDE_KEY, PROVIDER_KEY
	);
	MyLocationListener coarseListener = new MyLocationListener(
			LATITUDE_COARSE_KEY, LONGITUDE_COARSE_KEY, ALTITUDE_COARSE_KEY, PROVIDER_COARSE_KEY
	);

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getPreferences(MODE_PRIVATE);
        if (updateDisplay()) {
            return;
        }
	}

    public void refreshLocation(View view) {
		lm = (LocationManager) (getSystemService(Context.LOCATION_SERVICE));
		if (view.getId() == R.id.button) {
			if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				Toast.makeText(getApplicationContext(), "GPS not enabled", Toast.LENGTH_SHORT).show();

				Intent intent = new Intent();
				intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);
				return;
			}

			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, fineListener);
		} else {
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, coarseListener);
		}
	}

    boolean updateDisplay() {
		((TextView)findViewById(R.id.textView15)).setText(prefs.getString(PROVIDER_KEY, ""));
		((TextView)findViewById(R.id.textView2)).setText(prefs.getString(LATITUDE_KEY, "0.000°"));
		((TextView)findViewById(R.id.textView4)).setText(prefs.getString(LONGITUDE_KEY, "0.000°"));
		((TextView)findViewById(R.id.textView6)).setText(prefs.getString(ALTITUDE_KEY, "0m"));
		((TextView)findViewById(R.id.textView15b)).setText(prefs.getString(PROVIDER_COARSE_KEY, ""));
		((TextView)findViewById(R.id.textView2b)).setText(prefs.getString(LATITUDE_COARSE_KEY, "0.000°"));
		((TextView)findViewById(R.id.textView4b)).setText(prefs.getString(LONGITUDE_COARSE_KEY, "0.000°"));
		((TextView)findViewById(R.id.textView6b)).setText(prefs.getString(ALTITUDE_COARSE_KEY, "0m"));

		return prefs.getString(LATITUDE_KEY, "").length() > 0;
    }

	class MyLocationListener implements LocationListener {
		String latKey, longKey, altKey, providerKey;

		MyLocationListener(String latKey, String longKey, String altKey, String providerKey) {
			this.latKey = latKey;
			this.longKey = longKey;
			this.altKey = altKey;
			this.providerKey = providerKey;
		}

		@Override
		public void onLocationChanged(Location l) {
			lm.removeUpdates(this);

			String latitude = String.format("%.3f°%c", Math.abs(l.getLatitude()), l.getLatitude() < 0 ? 'S' : 'N');
			String longitude = String.format("%.3f°%c", Math.abs(l.getLongitude()), l.getLongitude() < 0 ? 'W' : 'E');
			String altitude = String.format("%.0fm", l.getAltitude());

			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(this.latKey, latitude);
			editor.putString(this.longKey, longitude);
			editor.putString(this.altKey, altitude);
			editor.putString(this.providerKey, l.getProvider() + " (" + l.getAccuracy() + "m)");
			editor.apply();

			updateDisplay();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}
	}
}
