package ranjan.maccharapps.anti_theftbt;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Ashish Ranjan on 15-06-2015.
 */
public class PasswordSplash extends Activity {

	private static final String TAG = "ranjan.anti_theftbt.TAG";

	private Logging log = new Logging();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password_splash);
		Log.v(TAG, "PasswordSplash on-create");
		log.Logging(getApplicationContext(), "PasswordSplash on-create");

		final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPasswordPref", MODE_PRIVATE);
		final SharedPreferences.Editor editor = pref.edit();

		final EditText etPasswordSplashValue = (EditText) findViewById(R.id.etPasswordSplashValueID);
		Button bPasswordSplashOK = (Button) findViewById(R.id.bPasswordSplashOKID);

		etPasswordSplashValue.setText("");

		etPasswordSplashValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
					Log.i(TAG, "Enter pressed");
					String passKey = etPasswordSplashValue.getText().toString();
					if (pref.getString("CURRENT_PASSWORD", "").equals(passKey)) {
						editor.putBoolean("IS_LOGGED_IN", true);
						editor.commit();

						Intent i = new Intent(getApplicationContext(), ChosenDeviceList.class);
						startActivity(i);

						finish();
					}
					else {
						etPasswordSplashValue.setText("");
					}
				}

				return false;
			}
		});

		bPasswordSplashOK.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "OK button pressed");
				String passKey = etPasswordSplashValue.getText().toString();
				if (pref.getString("CURRENT_PASSWORD", "").equals(passKey)) {
					editor.putBoolean("IS_LOGGED_IN", true);
					editor.commit();

					finish();
					Intent i = new Intent(getApplicationContext(), ChosenDeviceList.class);
					startActivity(i);
				}
				else {
					etPasswordSplashValue.setText("");
				}
			}
		});

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		Log.v(TAG, "PasswordSplash back-pressed");
		log.Logging(getApplicationContext(), "PasswordSplash on-create");

		SharedPreferences prefPassword = getApplicationContext().getSharedPreferences("MyPasswordPref", MODE_PRIVATE);
		SharedPreferences.Editor prefPasswordEditor = prefPassword.edit();
		prefPasswordEditor.putBoolean("IS_LOGGED_IN", false);
		prefPasswordEditor.commit();

		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);

		finish();
	}

}
