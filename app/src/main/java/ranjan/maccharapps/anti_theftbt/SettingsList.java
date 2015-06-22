package ranjan.maccharapps.anti_theftbt;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Ashish Ranjan on 11-06-2015.
 */
public class SettingsList extends Activity {

	private static final String TAG = "ranjan.anti_theftbt.TAG";
	String[] settingsListItemName = {"CountDown Time", "Password"};
	String[] settingsListItemFile = {"CountDownTime", "Password"};
	ArrayList<SLitem> slitemListFinal;
	SettingsListAdapter SLadapter;

	private Logging log = new Logging();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_list);

		displaySettingsView();

	}

	private class SLitem {

		String sname = null;
		String svalue = null;

		public SLitem(String sname, String svalue) {
			this.sname = sname;
			this.svalue = svalue;
		}

		public String getSname() {
			return sname;
		}

		public void setSname(String sname) {
			this.sname = sname;
		}

		public String getSvalue() {
			return svalue;
		}

		public void setSvalue(String svalue) {
			this.svalue = svalue;
		}
	}

	private void displaySettingsView() {
		SLitem item;
		SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();

		String FullPairedDevices = "Add devices to list";
		String ResetService = "Reset Service";
		String maxConnectionTime = "Maximum Connection Time";
		String delay = "Delay";
		String ADNIL = "Allow Device Name In Log?";
		String password = "Password";
		String sound = "Missing Alert Sound";

		String FullPairedDevicesValue = "";
		String ResetServiceValue = "";
		String maxConnectionTimeValue = pref.getString("MAX_CONNECTION_TIME", "6000") + " ms";
		String delayValue = pref.getString("DELAY", "100") + " ms";
		String ADNILValue = pref.getString("ALLOW_DEVICE_NAME_IN_LOG", "Yes");
		String passwordValue = "*********";
		String soundValue = pref.getString("NOTIFICATION_SOUND_NAME", "start_medium1");

		slitemListFinal = new ArrayList<SLitem>(Arrays.asList(new SLitem(FullPairedDevices, FullPairedDevicesValue), new SLitem(ResetService, ResetServiceValue), new SLitem(maxConnectionTime, maxConnectionTimeValue), new SLitem(delay, delayValue), new SLitem(ADNIL, ADNILValue), new SLitem(password, passwordValue), new SLitem(sound, soundValue)));

		SLadapter = new SettingsListAdapter(this, R.layout.settings_list_style, slitemListFinal);
		ListView sampleListView = (ListView) findViewById(R.id.settingsListID);

		sampleListView.setAdapter(SLadapter);

		sampleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SLitem item = (SLitem) parent.getItemAtPosition(position);

				switch (item.getSname()) {
					case "Add devices to list":
						showFullPairedDevices();
						break;
					case "Reset Service":
						showResetServiceDialog();
						break;
					case "Maximum Connection Time":
						showMaximumConnectionTimeDialog();
						break;
					case "Delay":
						showDelayDialog();
						break;
					case "Allow Device Name In Log?":
						showAllowDeviceNameInLog();
						break;
					case "Password":
						showPasswordDialog();
						break;
					case "Missing Alert Sound":
						showMissingSoundActivity();
						break;
				}

			}
		});

	}

	private class SettingsListAdapter extends ArrayAdapter<SLitem> {

		private ArrayList<SLitem> slitemList;

		public SettingsListAdapter (Context context, int textViewResourceId, ArrayList<SLitem> slitemList) {
			super(context, textViewResourceId, slitemList);
			this.slitemList = new ArrayList<SLitem>();
			this.slitemList.addAll(slitemList);
		}

		private class ViewHolder {
			TextView tvname;
			TextView tvvalue;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder mholder = null;

			if (convertView == null) {
				LayoutInflater minflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = minflater.inflate(R.layout.settings_list_style, null);

				mholder = new ViewHolder();
				mholder.tvname = (TextView) convertView.findViewById(R.id.settingsItemNameID);
				mholder.tvvalue = (TextView) convertView.findViewById(R.id.settingsItemValueID);
				convertView.setTag(mholder);
			}
			else {
				mholder = (ViewHolder) convertView.getTag();
			}

			SLitem item = slitemList.get(position);
			mholder.tvname.setText(item.getSname());
			mholder.tvvalue.setText(item.getSvalue());

			return convertView;
		}
	}

	private void showFullPairedDevices() {
		Intent i = new Intent(getApplicationContext(), FullDeviceList.class);
		startActivity(i);
		this.finish();
	}

	private void showResetServiceDialog() {
		final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
		final SharedPreferences.Editor editor = pref.edit();
//		Log.v(TAG, "SharedPreferences imported.");

		final Dialog ResetServiceDialog = new Dialog(this);
//		Log.v(TAG, "ResetServiceDialog has been started.");

		ResetServiceDialog.setContentView(R.layout.reset_service);
		ResetServiceDialog.setTitle("Reset Service?");
//		Log.v(TAG, "ResetServiceDialog Title has been set.");

		Button bResetServiceYes = (Button) ResetServiceDialog.findViewById(R.id.ResetServiceYesButtonID);
		Button bResetServiceNo = (Button) ResetServiceDialog.findViewById(R.id.ResetServiceNoButtonID);
//		Log.v(TAG, "ResetService Buttons have been set.");

		bResetServiceYes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				stopService(new Intent(SettingsList.this, ConnectService.class));
				ConnectIntentService.shouldContinue = false;
				Log.v(TAG, "Service stopped from Reset Service button");
				log.Logging(getApplicationContext(), "Service stopped from Reset Service button");

				editor.putBoolean("IS_SERVICE_STARTED", false);
				editor.commit();

				editor.putString("SAMPLE_NOTIFICATION_SOUND", "android.resource://ranjan.maccharapps.anti_theftbt/raw/stop1");
				editor.putBoolean("IS_THIS_START", false);
				editor.commit();

				ResetServiceDialog.dismiss();
			}
		});

		bResetServiceNo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ResetServiceDialog.dismiss();
			}
		});

		ResetServiceDialog.show();
//		Log.v(TAG, "ResetServiceDialog has been started.");
	}

	private void showMaximumConnectionTimeDialog() {
		final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
		final SharedPreferences.Editor editor = pref.edit();

		final Dialog MCTdialog = new Dialog(this);
		MCTdialog.setContentView(R.layout.maximum_connection_time);

		MCTdialog.setTitle("Set Max Connection Time");

		final EditText etMCTvalue = (EditText) MCTdialog.findViewById(R.id.etMCTValueID);
		Button bMCTsave = (Button) MCTdialog.findViewById(R.id.bMCTSaveID);
		Button bMCTreset = (Button) MCTdialog.findViewById(R.id.bMCTResetID);

		bMCTsave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String MCT = etMCTvalue.getText().toString();
				Log.v(TAG, "Entered Maximum Connection Time--------> [" + MCT + "]");
				log.Logging(getApplicationContext(), "Entered Maximum Connection Time--------> [" + MCT + "]");

				if (MCT.matches("")) {
					MCTdialog.dismiss();
				}
				else {
					if (Integer.parseInt(MCT) < 1000) {
						MCT = "1000";
						Log.v(TAG, "Maximum Connection Time set to default--------> [" + MCT + "]");
						log.Logging(getApplicationContext(), "Maximum Connection Time set to default--------> [" + MCT + "]");
					}
					editor.putString("MAX_CONNECTION_TIME", MCT);
					editor.commit();
					displaySettingsView();      //to update the Maximum Connection time value in the Settings List View
					MCTdialog.dismiss();
				}
			}
		});

		bMCTreset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editor.putString("MAX_CONNECTION_TIME", "6000");
				editor.commit();
				Log.v(TAG, "Maximum Connection Time RESET in sharedPreferences--------> [" + pref.getString("MAX_CONNECTION_TIME", "6000") + "]");
				log.Logging(getApplicationContext(), "Maximum Connection Time RESET in sharedPreferences--------> [" + pref.getString("MAX_CONNECTION_TIME", "6000") + "]");
				displaySettingsView();      //to update the Maximum Connection time value in the Settings List View
				MCTdialog.dismiss();
			}
		});

		MCTdialog.show();
	}

	private void showDelayDialog() {
		final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
		final SharedPreferences.Editor editor = pref.edit();

		final Dialog DelayDialog = new Dialog(this);
		DelayDialog.setContentView(R.layout.delay);

		DelayDialog.setTitle("Set Delay");

		final EditText etDelayValue = (EditText) DelayDialog.findViewById(R.id.etDelayValueID);
		Button bDelaySave = (Button) DelayDialog.findViewById(R.id.bDelaySaveID);
		Button bDelayReset = (Button) DelayDialog.findViewById(R.id.bDelayResetID);

		bDelaySave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String delay = etDelayValue.getText().toString();

				if (delay.matches("")) {
					DelayDialog.dismiss();
				}
				else {
					if (Integer.parseInt(delay) < 100) {
						delay = "100";
						Log.v(TAG, "Delay set to default------> [" + delay + "]");
						log.Logging(getApplicationContext(), "Delay set to default------> [" + delay + "]");
					}
					editor.putString("DELAY", delay);
					editor.commit();
					Log.v(TAG, "Delay in sharedPreferences------> [" + pref.getString("DELAY", "100") + "]");
					log.Logging(getApplicationContext(), "Delay in sharedPreferences------> [" + pref.getString("DELAY", "100") + "]");

					displaySettingsView();
					DelayDialog.dismiss();
				}
			}
		});

		bDelayReset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editor.putString("DELAY", "100");
				editor.commit();
				Log.v(TAG, "Delay RESET in sharedPreferences------> [" + pref.getString("DELAY", "100") + "]");
				log.Logging(getApplicationContext(), "Delay RESET in sharedPreferences------> [" + pref.getString("DELAY", "100") + "]");

				displaySettingsView();
				DelayDialog.dismiss();
			}
		});

		DelayDialog.show();
	}

	private void showAllowDeviceNameInLog() {
		SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
		final SharedPreferences.Editor editor = pref.edit();

		final Dialog ADNILDialog = new Dialog(this);
		ADNILDialog.setContentView(R.layout.reset_service);

		ADNILDialog.setTitle("Allow device name in Logs?");

		Button bADNILYes = (Button) ADNILDialog.findViewById(R.id.ResetServiceYesButtonID);
		Button bADNILNo = (Button) ADNILDialog.findViewById(R.id.ResetServiceNoButtonID);

		bADNILYes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editor.putString("ALLOW_DEVICE_NAME_IN_LOG", "Yes");
				editor.commit();
				Log.v(TAG, "AllowDeviceNameInLog has been set to 'Yes'");
				log.Logging(getApplicationContext(), "AllowDeviceNameInLog has been set to 'Yes'");

				displaySettingsView();
				ADNILDialog.dismiss();
			}
		});

		bADNILNo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editor.putString("ALLOW_DEVICE_NAME_IN_LOG", "No");
				editor.commit();
				Log.v(TAG, "AllowDeviceNameInLog has been set to 'No'");
				log.Logging(getApplicationContext(), "AllowDeviceNameInLog has been set to 'No'");

				displaySettingsView();
				ADNILDialog.dismiss();
			}
		});

		ADNILDialog.show();
	}

	private void showPasswordDialog() {
		final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPasswordPref", MODE_PRIVATE);
		final SharedPreferences.Editor editor = pref.edit();

		final Dialog PasswordDialog = new Dialog(this);
		PasswordDialog.setContentView(R.layout.password);

		PasswordDialog.setTitle("Change Password");

		final ToggleButton tbAskForPassword = (ToggleButton) PasswordDialog.findViewById(R.id.tbAskForPasswordID);
		final EditText etPasswordCurrent = (EditText) PasswordDialog.findViewById(R.id.etPasswordCurrentID);
		final EditText etPasswordNew = (EditText) PasswordDialog.findViewById(R.id.etPasswordNewID);
		final EditText etCPasswordNew = (EditText) PasswordDialog.findViewById(R.id.etCPasswordNewID);
		Button bPasswordSave = (Button) PasswordDialog.findViewById(R.id.bPasswordID);

		tbAskForPassword.setChecked(pref.getBoolean("ASK_FOR_PASSWORD", false));

		tbAskForPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (tbAskForPassword.isChecked()) {
					editor.putBoolean("ASK_FOR_PASSWORD", true);
					editor.commit();
				}
				else {
					editor.putBoolean("ASK_FOR_PASSWORD", false);
					editor.commit();
				}
			}
		});

		bPasswordSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				String PasswordCurrent = etPasswordCurrent.getText().toString();
				String PasswordNew = etPasswordNew.getText().toString();
				String CPasswordNew = etCPasswordNew.getText().toString();

				if (pref.getString("CURRENT_PASSWORD", "").equals(PasswordCurrent)) {
					if (PasswordNew.equals(CPasswordNew)) {
						editor.putString("CURRENT_PASSWORD", PasswordNew);
						editor.commit();
						PasswordDialog.dismiss();
					}
					else {
						etCPasswordNew.setText("");
						etCPasswordNew.setHint("New password & confirm\n password don't match!");
					}
				}
				else {
					etPasswordCurrent.setText("");
					etPasswordNew.setText("");
					etCPasswordNew.setText("");
					etPasswordCurrent.setHint("Current password is\n wrong!");
				}

			}
		});

		PasswordDialog.show();
	}

	private void showMissingSoundActivity() {
		Intent i = new Intent(getApplicationContext(), Sound.class);
		startActivity(i);
	}
}
