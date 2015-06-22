package ranjan.maccharapps.anti_theftbt;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class ChosenDeviceList extends ActionBarActivity {

	public boolean CDLallowInsecureConnections = true;
	public int isRun = 0;
	private BluetoothAdapter mBluetoothAdapter;
	ChosenDeviceListAdapter CDLadapter = null;
	public ArrayList<CDLitem> cdlitemListFinal = new ArrayList<CDLitem>();
	public ArrayList<String> chosenDevices = new ArrayList<String>();
	public ArrayList<String> FinalchosenDevices = new ArrayList<String>();
	private MenuItem logItem;

	SharedPreferences prefLog;

	private Logging log = new Logging();

	private static final String TAG = "ranjan.anti_theftbt.TAG";
	public final static String SERVICE_START_MSG = "ranjan.maccharapps.anti_theftbt.SERVICE_START_MSG";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chosen_device_list);

		prefLog = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

		Log.v(TAG, "Inside ChosenDeviceList's onCreate");
		log.Logging(getApplicationContext(), "Inside ChosenDeviceList's onCreate");
/*		Log.v(TAG, "Intent Starting");
		log.Logging(getApplicationContext(), "Intent Starting");
		Intent CDLintent = getIntent();
		Log.v(TAG, "Intent Started");
		log.Logging(getApplicationContext(), "Intent Started");
*/
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mBluetoothAdapter.isEnabled() == false) {
			mBluetoothAdapter.enable();
			Toast.makeText(getApplicationContext(), "Waiting for bluetooth to start", Toast.LENGTH_SHORT).show();
			Log.v(TAG, "Bluetooth was switched OFF! It has been switched 'ON' programmatically :)");
			log.Logging(getApplicationContext(), "Bluetooth was switched OFF! It has been switched 'ON' programmatically :)");
		}

		SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

		SharedPreferences prefPassword = getApplicationContext().getSharedPreferences("MyPasswordPref", MODE_PRIVATE);
		SharedPreferences.Editor prefPasswordEditor = prefPassword.edit();

		String chosenDevicesString = pref.getString("CHOSEN_DEVICES_STRING", null);
		if (chosenDevicesString == null) {
			Intent firstRunIntent = new Intent(getApplicationContext(), FullDeviceList.class);
			startActivity(firstRunIntent);
			Log.v(TAG, "First Run! Redirected to FullDeviceList");
			log.Logging(getApplicationContext(), "First Run! Redirected to FullDeviceList");
			this.finish();
		}
		else {
			if ((prefPassword.getBoolean("ASK_FOR_PASSWORD", false) == true) && (prefPassword.getBoolean("IS_LOGGED_IN", false) == false)) {
				Intent i = new Intent(getApplicationContext(), PasswordSplash.class);
				startActivity(i);
				Log.v(TAG, "'ASK_FOR_PASSWORD' = true. Redirected to PasswordSplash");
				log.Logging(getApplicationContext(), "'ASK_FOR_PASSWORD' = true. Redirected to PasswordSplash");
			}
			else {
				prefPasswordEditor.putBoolean("IS_LOGGED_IN", true);
				prefPasswordEditor.commit();
				chosenDevices = convertToArrayList(chosenDevicesString);
			}
		}

		Log.v(TAG, "Chosen Devices Updated");
		log.Logging(getApplicationContext(), "Chosen Devices Updated");

		Log.v(TAG, "Before display start: chosenDevices -> " + Integer.toString(chosenDevices.size()));
		log.Logging(getApplicationContext(), "Before display start: chosenDevices -> " + Integer.toString(chosenDevices.size()));
		Log.v(TAG, "Before display start: FinalchosenDevices -> " + Integer.toString(FinalchosenDevices.size()));
		log.Logging(getApplicationContext(), "Before display start: FinalchosenDevices -> " + Integer.toString(FinalchosenDevices.size()));

		for (int k=0; k < chosenDevices.size(); k++) {
			FinalchosenDevices.add(chosenDevices.get(k));
		}

		displayChosenListView();

		Log.v(TAG, "Before service start: FinalchosenDevices -> " + Integer.toString(FinalchosenDevices.size()));
		log.Logging(getApplicationContext(), "Before service start: FinalchosenDevices -> " + Integer.toString(FinalchosenDevices.size()));

		checkStartServiceButton();

		checkRefreshButton();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);

		Log.v(TAG, "Inside ChosenDeviceList's onBackPressed");
		log.Logging(getApplicationContext(), "Inside ChosenDeviceList's onBackPressed");

		SharedPreferences prefPassword = getApplicationContext().getSharedPreferences("MyPasswordPref", MODE_PRIVATE);
		SharedPreferences.Editor prefPasswordEditor = prefPassword.edit();
		prefPasswordEditor.putBoolean("IS_LOGGED_IN", false);
		prefPasswordEditor.commit();

		finish();
	}

	private String convertToString(ArrayList<String> mlist) {

		StringBuilder sb = new StringBuilder();
		String delim = "";
		for (String s : mlist) {
			sb.append(delim);
			sb.append(s);
			delim = ",";
		}
		return sb.toString();
	}

	private ArrayList<String> convertToArrayList(String mstring) {
		ArrayList<String> mlist = new ArrayList<String>(Arrays.asList(mstring.split(",")));
		return mlist;
	}

/*	public boolean isAllowInsecureConnections () {
		return CDLallowInsecureConnections;
	}
*/
	private class CDLitem {

		String cdname = null;
		String cdaddress = null;

		public CDLitem(String cdname, String cdaddress) {
			this.cdname = cdname;
			this.cdaddress = cdaddress;
		}

		public String getCdname() {
			return cdname;
		}

		public void setCdname(String cdname) {
			this.cdname = cdname;
		}

		public String getCdaddress() {
			return cdaddress;
		}

		public void setCdaddress(String cdaddress) {
			this.cdaddress = cdaddress;
		}
	}

	private void displayChosenListView() {

		CDLitem item;

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		Log.v(TAG, "Inside ChosenDeviceList's displayChosenListView");
		log.Logging(getApplicationContext(), "Inside ChosenDeviceList's displayChosenListView");
/*		if (mBluetoothAdapter.isEnabled() == false) {
			mBluetoothAdapter.enable();
			Toast.makeText(getApplicationContext(), "Waiting for bluetooth to start", Toast.LENGTH_SHORT).show();
			Log.v(TAG, "Bluetooth was switched OFF! It has been switched 'ON' programmatically :)");
			log.Logging(getApplicationContext(), "Bluetooth was switched OFF! It has been switched 'ON' programmatically :)");
		}
*/
		Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();

		if (mPairedDevices.size() > 0) {
			int i = chosenDevices.size();
			for (BluetoothDevice mDevice : mPairedDevices) {
				if (i > 0) {
					if (mDevice.getAddress().equalsIgnoreCase(chosenDevices.get(0))) {

						item = new CDLitem(mDevice.getName(), mDevice.getAddress());
						cdlitemListFinal.add(item);

						if (prefLog.getString("ALLOW_DEVICE_NAME_IN_LOG", "Yes").matches("Yes")) {
							Log.v(TAG, "Added to FinalChosenList: " + chosenDevices.get(0));
							log.Logging(getApplicationContext(), "Added to cdlitemListFinal: " + chosenDevices.get(0));
						}
						else if (prefLog.getString("ALLOW_DEVICE_NAME_IN_LOG", "Yes").matches("No")) {
							Log.v(TAG, "Added to FinalChosenList: **:**:**:**:**:** " + chosenDevices.get(0));
							log.Logging(getApplicationContext(), "Added to cdlitemListFinal: **:**:**:**:**:**");
						}

						chosenDevices.remove(0);
						i--;
					}
				}

			}
		}
		Log.v(TAG, "cdlitemListFinal updated with chosenDevices.");
		log.Logging(getApplicationContext(), "cdlitemListFinal updated with chosenDevices.");

		CDLadapter = new ChosenDeviceListAdapter(this, R.layout.chosen_device_list_style, cdlitemListFinal);
		ListView sampleListView = (ListView) findViewById(R.id.chosenDeviceListID);

		sampleListView.setAdapter(CDLadapter);
		Log.v(TAG, "sampleListView set the adapter");
		log.Logging(getApplicationContext(), "sampleListView set the adapter");

		sampleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CDLitem item = (CDLitem) parent.getItemAtPosition(position);

				if (mBluetoothAdapter.isEnabled() == false) {
					mBluetoothAdapter.enable();
					Toast.makeText(getApplicationContext(), "Waiting for bluetooth to start", Toast.LENGTH_SHORT).show();
					Log.v(TAG, "Bluetooth was switched OFF! It has been switched 'ON' programmatically :)");
					log.Logging(getApplicationContext(), "Bluetooth was switched OFF! It has been switched 'ON' programmatically :)");
				}
				Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();
				ImageView ivicon = (ImageView) view.findViewById(R.id.chosenIconID);

				if (mPairedDevices.size() > 0) {
					for (BluetoothDevice tdevice : mPairedDevices) {
						if (tdevice.getName().equalsIgnoreCase(item.getCdname())) {
							TempConnectActivity sampleConnection = new TempConnectActivity(tdevice);
							isRun = sampleConnection.tempRun();
							Log.v(TAG, "isRun: " + String.valueOf(isRun) + " -> " + tdevice.getName());
							log.Logging(getApplicationContext(), "isRun: " + String.valueOf(isRun) + " -> " + tdevice.getName());
							sampleConnection.tempCancel();
							if (isRun == 1) {
								ivicon.setImageResource(R.drawable.yes);
								Log.v(TAG, "Connection to: " + item.getCdname() + " Successfull");
								log.Logging(getApplicationContext(), "Connection to: " + item.getCdname() + " Successfull");
								Toast.makeText(getApplicationContext(), "Connection to: " + item.getCdname() + " Successfull", Toast.LENGTH_SHORT).show();
							} else {
								ivicon.setImageResource(R.drawable.no);
								Log.v(TAG, "Connection to: " + item.getCdname() + " Unsuccessfull");
								log.Logging(getApplicationContext(), "Connection to: " + item.getCdname() + " Unsuccessfull");
								Toast.makeText(getApplicationContext(), "Connection to: " + item.getCdname() + " Unsuccessfull", Toast.LENGTH_LONG).show();
							}

						}
					}
				}

			}
		});

		SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
		Button bStartService = (Button) findViewById(R.id.chosenStartServiceButtonID);

		if (pref.getBoolean("IS_SERVICE_STARTED", false) == true) {
			bStartService.setVisibility(View.GONE);
		}
		else {
			bStartService.setVisibility(View.VISIBLE);
		}

	}

	private void checkStartServiceButton() {

		final Button bStartService = (Button) findViewById(R.id.chosenStartServiceButtonID);
//		final Intent callService = new Intent(this, ConnectService.class);
		final Intent callService = new Intent(this, ConnectIntentService.class);
		final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
		final SharedPreferences.Editor editor = pref.edit();

		bStartService.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isServiceStarted = pref.getBoolean("IS_SERVICE_STARTED", false);
				Log.v(TAG, "isServiceStarted----> [" + isServiceStarted + "]");
				log.Logging(getApplicationContext(), "isServiceStarted----> [" + isServiceStarted + "]");
				if (isServiceStarted == false) {
					ConnectIntentService.shouldContinue = true;
					callService.putStringArrayListExtra(SERVICE_START_MSG, FinalchosenDevices);
					startService(callService);

					editor.putBoolean("IS_SERVICE_STARTED", true);
					editor.commit();

					editor.putString("SAMPLE_NOTIFICATION_SOUND", pref.getString("NOTIFICATION_SOUND", "android.resource://ranjan.maccharapps.anti_theftbt/raw/start_medium1"));
					editor.putBoolean("IS_THIS_START", true);
					editor.commit();

					bStartService.setVisibility(View.GONE);

					Log.v(TAG, "SERVICE STARTED");
					log.Logging(getApplicationContext(), "SERVICE STARTED");
				}
			}
		});
	}

	private void checkRefreshButton() {
		final Button bRefresh = (Button) findViewById(R.id.chosenRefreshButtonID);

		bRefresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				displayChosenListView();
			}
		});
	}

	private class ChosenDeviceListAdapter extends ArrayAdapter<CDLitem> {

		private ArrayList<CDLitem> cdlitemList;

		public ChosenDeviceListAdapter(Context context, int textViewResourceId, ArrayList<CDLitem> cdlitemList) {
			super(context, textViewResourceId, cdlitemList);
			this.cdlitemList = new ArrayList<CDLitem>();
			this.cdlitemList.addAll(cdlitemList);
		}

		private class ViewHolder {
			TextView tvname;
			TextView tvaddress;
			ImageView ivicon;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder mholder = null;
			Log.v(TAG, String.valueOf(position));
			log.Logging(getApplicationContext(), String.valueOf(position));

			if(convertView == null) {
				LayoutInflater minflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = minflater.inflate(R.layout.chosen_device_list_style, null);

				mholder = new ViewHolder();
				mholder.tvname = (TextView) convertView.findViewById(R.id.chosenDeviceNameID);
				mholder.tvaddress = (TextView) convertView.findViewById(R.id.chosenDeviceAddressID);
				mholder.ivicon = (ImageView) convertView.findViewById(R.id.chosenIconID);
				convertView.setTag(mholder);
			}
			else {
				mholder = (ViewHolder) convertView.getTag();
			}

			CDLitem item = cdlitemList.get(position);
			mholder.tvname.setText(item.getCdname());
			mholder.tvaddress.setText(item.getCdaddress());

			return convertView;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mInflater = getMenuInflater();
		mInflater.inflate(R.menu.option_menu, menu);

		logItem = menu.findItem(R.id.menuLogID);
		SharedPreferences logPref = getApplicationContext().getSharedPreferences("MyLogPref", MODE_PRIVATE);
		SharedPreferences.Editor logPrefEditor = logPref.edit();
		if (logPref.getString("LOG_FILE_NAME", null) == null) {
			logItem.setTitle("Start Logging");
		}
		else {
			logItem.setTitle("Stop Logging");
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem mitem) {
		switch (mitem.getItemId()) {
			case R.id.menuStopServiceID:
				doConfirmStopService();
				return true;

			case R.id.menuSettingsID:
				doSettings();
				return true;

			case R.id.menuMissingDevicesID:
				doMissingDevices();
				return true;

			case R.id.menuLogID:
				toggleLogging();
				return true;

			case R.id.menuAboutID:
				doAbout();
				return true;

			case R.id.menuExitID:
				doExit();
				return true;
		}
		return false;
	}

	private void doConfirmStopService() {
		SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
		final SharedPreferences.Editor editor = pref.edit();

		final Dialog ConfirmStopServiceDialog = new Dialog(this);
		ConfirmStopServiceDialog.setContentView(R.layout.confirm_stop_service);
		ConfirmStopServiceDialog.setTitle("Really stop service?");

		Button bConfirmStopServiceYes = (Button) ConfirmStopServiceDialog.findViewById(R.id.ConfirmStopServiceYesButtonID);
		Button bConfirmStopServiceNo = (Button) ConfirmStopServiceDialog.findViewById(R.id.ConfirmStopServiceNoButtonID);

		bConfirmStopServiceYes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				stopService(new Intent(ChosenDeviceList.this, ConnectService.class));
				ConnectIntentService.shouldContinue = false;
				Log.v(TAG, "Service STOPPED from MENU");
				log.Logging(getApplicationContext(), "Service STOPPED from MENU");

				editor.putBoolean("IS_SERVICE_STARTED", false);
				editor.commit();
				Button bStartService = (Button) findViewById(R.id.chosenStartServiceButtonID);
				bStartService.setVisibility(View.VISIBLE);

				editor.putString("SAMPLE_NOTIFICATION_SOUND", "android.resource://ranjan.maccharapps.anti_theftbt/raw/stop1");
				editor.putBoolean("IS_THIS_START", false);
				editor.commit();

				ConfirmStopServiceDialog.dismiss();
			}
		});

		bConfirmStopServiceNo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ConfirmStopServiceDialog.dismiss();
			}
		});

		ConfirmStopServiceDialog.show();
	}

	private void doSettings() {
		startActivity(new Intent(this, SettingsList.class));
	}

	private void doMissingDevices() {
		startActivity(new Intent(this, MissingDeviceList.class));
	}

	private void toggleLogging() {
		SharedPreferences logPref = getApplicationContext().getSharedPreferences("MyLogPref", MODE_PRIVATE);
		SharedPreferences.Editor logPrefEditor = logPref.edit();

		if (logPref.getString("LOG_FILE_NAME", null) == null) {
			SimpleDateFormat sdfLog = new SimpleDateFormat("yyyyMMdd_HHmmss");
			String currentDateandTime = sdfLog.format(new Date()) + ".txt";

			logPrefEditor.putString("LOG_FILE_NAME", currentDateandTime);
			logPrefEditor.commit();

			logItem.setTitle("Stop Logging");

			Toast.makeText(this, "Logging STARTED in sdcard/Anti-Theft BT (ATBT) Logs/" + currentDateandTime, Toast.LENGTH_LONG).show();
		}
		else {
			Toast.makeText(this, "Logging STOPPED & SAVED in sdcard/Anti-Theft BT (ATBT) Logs/" + logPref.getString("LOG_FILE_NAME", null), Toast.LENGTH_LONG).show();

			logPrefEditor.putString("LOG_FILE_NAME", null);
			logPrefEditor.commit();

			logItem.setTitle("Start Logging");
		}

		Log.v(TAG, "Name of Log File: " + logPref.getString("LOG_FILE_NAME", null));
		log.Logging(getApplicationContext(), "Name of Log File: " + logPref.getString("LOG_FILE_NAME", null));
	}

	private void doAbout() {
		Intent i = new Intent(getApplicationContext(), About.class);
		startActivity(i);
/*
		Dialog AboutDialog = new Dialog(ChosenDeviceList.this);
		AboutDialog.setContentView(R.layout.about);
		AboutDialog.setTitle("About");
		AboutDialog.show();
*/
	}

	private void doExit() {
		SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
		final SharedPreferences.Editor editor = pref.edit();

		final Dialog ExitDialog = new Dialog(this);
		ExitDialog.setContentView(R.layout.exit);

		ExitDialog.setTitle("Turn bluetooth off?");

		Button bExitYes = (Button) ExitDialog.findViewById(R.id.bExitYesButtonID);
		Button bExitNo = (Button) ExitDialog.findViewById(R.id.bExitNoButtonID);
		Button bExitCancel = (Button) ExitDialog.findViewById(R.id.bExitCancelButtonID);

		bExitYes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mBluetoothAdapter.isEnabled() == true) {
					mBluetoothAdapter.disable();
					Toast.makeText(getApplicationContext(), "Bluetooth has been switched OFF", Toast.LENGTH_SHORT).show();
					Log.v(TAG, "Bluetooth has been switched OFF by exit()");
					log.Logging(getApplicationContext(), "Bluetooth has been switched OFF by exit()");
				}
				else {
					Toast.makeText(getApplicationContext(), "Bluetooth is already switched OFF", Toast.LENGTH_SHORT).show();
					Log.v(TAG, "Bluetooth was already switched OFF during exit()");
					log.Logging(getApplicationContext(), "Bluetooth was already switched OFF during exit()");
				}
				ExitDialog.dismiss();

				ConnectIntentService.shouldContinue = false;
				Log.v(TAG, "Service STOPPED from MENU");
				log.Logging(getApplicationContext(), "Service STOPPED from MENU");

				editor.putBoolean("IS_SERVICE_STARTED", false);
				editor.commit();
				Button bStartService = (Button) findViewById(R.id.chosenStartServiceButtonID);
				bStartService.setVisibility(View.VISIBLE);

				editor.putString("SAMPLE_NOTIFICATION_SOUND", "android.resource://ranjan.maccharapps.anti_theftbt/raw/stop1");
				editor.commit();

				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);

				finish();
			}
		});

		bExitNo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ExitDialog.dismiss();

				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);

				Log.v(TAG, "Exited from app without switching Bluetooth OFF");
				log.Logging(getApplicationContext(), "Exited from app without switching Bluetooth OFF");

				finish();
			}
		});

		bExitCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ExitDialog.dismiss();
			}
		});

		ExitDialog.show();
	}



}










