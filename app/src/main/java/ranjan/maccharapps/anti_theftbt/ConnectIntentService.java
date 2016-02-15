package ranjan.maccharapps.anti_theftbt;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Ashish Ranjan on 11-06-2015.
 */
public class ConnectIntentService extends IntentService {

	private static final String TAG = "ranjan.anti_theftbt.TAG";
	private static final String TAG2 = "ranjan.TAG2";

	public static volatile boolean shouldContinue = true;
	public int FinalisRun;
	public boolean isTimeOver = false;
	public String tmpMissingDevice;
	private int expiry_time = 6000;
	private int delay_time = 100;
	private Timer ctimer = new Timer();
	private TimerTask cTT = new ConnectTimerTask();
	private Integer numOfRestartcTTcalls;
	Intent broadcastIntent = new Intent("ranjan.maccharapps.anti_theftbt.MISSING_DEVICE_TAG");
	public static final String MISSING_DEVICE_NAME = "ranjan.maccharapps.anti_theftbt.MISSING_DEVICE";
	public Integer numOfMissingCounts = 0;
	private boolean isThisFirstAlert = true;

	SharedPreferences prefLog;

	private Logging log = new Logging();

	private ArrayList<String> FinalchosenDevices = new ArrayList<String>();
	private BluetoothSocket mSocket;
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();
	ArrayList<BluetoothDevice> FinalPairedDevices = new ArrayList<BluetoothDevice>();

	private String sampleNotificationSound;

	public ConnectIntentService() {
		super("ConnectIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_FOREGROUND);
		Log.v(TAG, "Inside ConnectIntentService's onHandleIntent()");
		log.Logging(getApplicationContext(), "Inside ConnectIntentService's onHandleIntent()");

		Log.v(TAG2, "Inside ConnectIntentService's onHandleIntent()");

		LocalBroadcastManager.getInstance(ConnectIntentService.this).registerReceiver(MissingDeviceBroadcastReceiver, new IntentFilter("ranjan.maccharapps.anti_theftbt.MISSING_DEVICE_TAG"));
		FinalchosenDevices = intent.getStringArrayListExtra(ChosenDeviceList.SERVICE_START_MSG);

		SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();

		prefLog = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

		expiry_time = Integer.parseInt(pref.getString("MAX_CONNECTION_TIME", "6000"));
		delay_time = Integer.parseInt(pref.getString("DELAY", "100"));

		runAsForeground();
		startIntentServiceRun();
	}

	private class ConnectTimerTask extends TimerTask {
		@Override
		public void run() {
			isTimeOver = true;

			if (FinalisRun==2) {
				numOfMissingCounts++;
				Log.v(TAG, "MISSING device detected in CONNECT_TIMER_TASK----------> $" + tmpMissingDevice + "$");
				log.Logging(getApplicationContext(), "MISSING device detected in CONNECT_TIMER_TASK----------> $" + tmpMissingDevice + "$");
				broadcastIntent.putExtra(MISSING_DEVICE_NAME, tmpMissingDevice);
				LocalBroadcastManager.getInstance(ConnectIntentService.this).sendBroadcast(broadcastIntent);

				restartcTT();
			}
		}
	}

	private void restartcTT() {
		cTT.cancel();

		if (shouldContinue==true && FinalisRun==2) {
			numOfMissingCounts++;
			Log.v(TAG, "ConnectTimerTask has been RESTARTED through restartcTT()");
			log.Logging(getApplicationContext(), "ConnectTimerTask has been RESTARTED through restartcTT()");
			ctimer = new Timer();
			cTT = new ConnectTimerTask();
			ctimer.schedule(cTT, expiry_time);
			numOfRestartcTTcalls++;
		}
	}

	private int tempRun() {
		int isRun = 0;

		if (mBluetoothAdapter.isEnabled()==false) {
			mBluetoothAdapter.enable();
			Log.v(TAG, "Bluetooth was OFF! It has been switched 'ON' in 'ConnectIntentService=>tempRun()'");
			log.Logging(getApplicationContext(), "Bluetooth was OFF! It has been switched 'ON' in 'ConnectIntentService=>tempRun()'");
		}
		mBluetoothAdapter.cancelDiscovery();
		Log.v(TAG, "Discovery of devices cancelled");
//		log.Logging(getApplicationContext(), "Discovery of devices cancelled");

		try {
			mSocket.connect();
			isRun = 1;
			Log.v(TAG, "Connection to socket is successful");
			log.Logging(getApplicationContext(), "Connection to socket is successful");
		}
		catch (Exception e1) {
			Log.v(TAG, "Exception raised during connecting with socket: {" + e1.getMessage() + "}");
			log.Logging(getApplicationContext(), "Exception raised during connecting with socket: {" + e1.getMessage() + "}");
//			Log.v(TAG, "Exception message: {" + e1.getMessage() + "}");
//			log.Logging(getApplicationContext(), "Exception message: {" + e1.getMessage() + "}");
			e1.printStackTrace();
			try {
				mSocket.close();
				Log.v(TAG, "Socket has been closed");
				log.Logging(getApplicationContext(), "Socket has been closed");
			}
			catch (Exception e2) {
				Log.v(TAG, "Exception raised during closing the socket");
				log.Logging(getApplicationContext(), "Exception raised during closing the socket");
				Log.v(TAG, "Exception message: {" + e2.getMessage() + "}");
				log.Logging(getApplicationContext(), "Exception message: {" + e2.getMessage() + "}");
				e2.printStackTrace();
			}
			return isRun;
		}
		return isRun;
	}

	private void tempCancel() {
		try {
			mSocket.close();
			Log.v(TAG, "tempCancel function has closed the socket successfully");
			log.Logging(getApplicationContext(), "tempCancel function has closed the socket successfully");
		}
		catch (Exception e) {
			Log.v(TAG, "Exception raised during closing socket through tempCancel function");
			log.Logging(getApplicationContext(), "Exception raised during closing socket through tempCancel function");
			e.printStackTrace();
			Log.v(TAG, "Exception message: {" + e.getMessage() + "}");
			log.Logging(getApplicationContext(), "Exception message: {" + e.getMessage() + "}");
		}
	}

	private void sleep_wait(int ms) {
		try {
			Thread.sleep(ms);
		}
		catch (Exception e) {
			Log.v(TAG, "Exception raised during executing Thread.sleep() through sleep_wait()");
			log.Logging(getApplicationContext(), "Exception raised during executing Thread.sleep() through sleep_wait()");
			e.printStackTrace();
			Log.v(TAG, "Exception message: {" + e.getMessage() + "}");
			log.Logging(getApplicationContext(), "Exception message: {" + e.getMessage() + "}");
		}
	}

	private void startIntentServiceRun() {
		try {
			Log.v(TAG, "startIntentServiceRun() started");
			log.Logging(getApplicationContext(), "startIntentServiceRun() started");

			if (mPairedDevices.size() > 0) {
				int i = FinalchosenDevices.size();
				Log.v(TAG, "mPairedDevices size -------> " + Integer.toString(mPairedDevices.size()));
				log.Logging(getApplicationContext(), "mPairedDevices size -------> " + Integer.toString(mPairedDevices.size()));
				Log.v(TAG, "FinalchosenDevices size ---> " + Integer.toString(i));
				log.Logging(getApplicationContext(), "FinalchosenDevices size ---> " + Integer.toString(i));

				for (BluetoothDevice mDevice : mPairedDevices) {
					if (i > 0) {
						if (mDevice.getAddress().equalsIgnoreCase(FinalchosenDevices.get(0))) {
							FinalPairedDevices.add(mDevice);
							FinalchosenDevices.remove(0);
							i--;
						}
					}
				}
			}
			Log.v(TAG, "FinalPairedDevices updated with FinalchosenDevices");
			log.Logging(getApplicationContext(), "FinalPairedDevices updated with FinalchosenDevices");

			int j = 0;

			try {
				while (shouldContinue) {

					for (BluetoothDevice tmpDevice : FinalPairedDevices) {

						if (shouldContinue) {
							tmpMissingDevice = tmpDevice.getName();

							isTimeOver = false;
							FinalisRun = 2;
							numOfRestartcTTcalls = 0;
							numOfMissingCounts = 0;
							isThisFirstAlert = true;

							ctimer = new Timer();
							cTT = new ConnectTimerTask();
							ctimer.schedule(cTT, expiry_time);
							Log.v(TAG, "Timer started with expiry-time -> " + expiry_time + " ms");
							log.Logging(getApplicationContext(), "Timer started with expiry-time -> " + expiry_time + " ms");

							BluetoothSocket tmpSocket = null;
							try {
								Method method;
								method = tmpDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
								tmpSocket = (BluetoothSocket) method.invoke(tmpDevice, 1);
								Log.v(TAG, "Socket created successfully");
//								log.Logging(getApplicationContext(), "Socket created successfully");
							}
							catch (Exception e) {
								Log.v(TAG, "Exception raised during creating tmpSocket: {" + e.getMessage() + "}");
								log.Logging(getApplicationContext(), "Exception raised during creating tmpSocket: {" + e.getMessage() + "}");
								e.printStackTrace();
							}
							mSocket = tmpSocket;

							FinalisRun = tempRun();

							SharedPreferences prefMissingDevices = getApplicationContext().getSharedPreferences("MissingDevicesPref", MODE_PRIVATE);
							SharedPreferences.Editor editorMissingDevices = prefMissingDevices.edit();
							String isDeviceInRangeString = prefMissingDevices.getString("MISSING_DEVICES", "");
							ArrayList<String> MissingDevices = new ArrayList<String>();
							MissingDevices = convertToArrayList(isDeviceInRangeString);

							Log.v(TAG, "We find the value of 'FinalisRun' = [" + FinalisRun + "] & 'isTimeOver' = [" + isTimeOver + "] && 'delay_time' = [" + delay_time + "]");
							log.Logging(getApplicationContext(), "We find the value of 'FinalisRun' = [" + FinalisRun + "] & 'isTimeOver' = [" + isTimeOver + "]");

							if (FinalisRun == 0) {
								cTT.cancel();
								tempCancel();
								sleep_wait(300);

								tmpMissingDevice = tmpDevice.getName();

								isTimeOver = false;
								FinalisRun = 2;
								numOfRestartcTTcalls = 0;
								numOfMissingCounts = 0;
								isThisFirstAlert = true;

								ctimer = new Timer();
								cTT = new ConnectTimerTask();
								ctimer.schedule(cTT, expiry_time);
								Log.v(TAG, "Timer started with expiry-time -> " + expiry_time + " ms");
								log.Logging(getApplicationContext(), "Timer started with expiry-time -> " + expiry_time + " ms");

								tmpSocket = null;
								try {
									Method method;
									method = tmpDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
									tmpSocket = (BluetoothSocket) method.invoke(tmpDevice, 1);
									Log.v(TAG, "Socket created successfully");
//								log.Logging(getApplicationContext(), "Socket created successfully");
								}
								catch (Exception e) {
									Log.v(TAG, "Exception raised during creating tmpSocket: {" + e.getMessage() + "}");
									log.Logging(getApplicationContext(), "Exception raised during creating tmpSocket: {" + e.getMessage() + "}");
									e.printStackTrace();
								}
								mSocket = tmpSocket;

								FinalisRun = tempRun();

								if (FinalisRun == 0) {
									numOfMissingCounts++;
									Log.v(TAG, "MISSING device detected -------------------------------> $" + tmpDevice.getName() + "$");
									log.Logging(getApplicationContext(), "MISSING device detected -------------------------------> $" + tmpDevice.getName() + "$");
									cTT.cancel();
									Log.v(TAG, "ConnectTimerTask() cancelled ");
									log.Logging(getApplicationContext(), "ConnectTimerTask() cancelled ");
									broadcastIntent.putExtra(MISSING_DEVICE_NAME, tmpMissingDevice);
									LocalBroadcastManager.getInstance(ConnectIntentService.this).sendBroadcast(broadcastIntent);

									editorMissingDevices.putString("MISSING_DEVICES", addToArrayList(MissingDevices, tmpMissingDevice));
									editorMissingDevices.commit();

									if (prefLog.getString("ALLOW_DEVICE_NAME_IN_LOG", "Yes").matches("Yes")) {
										Log.v(TAG, "MISSING_DEVICES listString: [" + prefMissingDevices.getString("MISSING_DEVICES", "") + "]");
										log.Logging(getApplicationContext(), "MISSING_DEVICES listString: [" + prefMissingDevices.getString("MISSING_DEVICES", "") + "]");
									}
									else if (prefLog.getString("ALLOW_DEVICE_NAME_IN_LOG", "Yes").matches("No")) {
										Log.v(TAG, "MISSING_DEVICES listString: *****,...,***** " + prefMissingDevices.getString("MISSING_DEVICES", ""));
										log.Logging(getApplicationContext(), "MISSING_DEVICES listString: *****,...,***** ");
									}
								}
							}


/*							if (FinalisRun == 0) {
								numOfMissingCounts++;
								Log.v(TAG, "MISSING device detected -------------------------------> $" + tmpDevice.getName() + "$");
								log.Logging(getApplicationContext(), "MISSING device detected -------------------------------> $" + tmpDevice.getName() + "$");
								cTT.cancel();
								Log.v(TAG, "ConnectTimerTask() cancelled ");
								log.Logging(getApplicationContext(), "ConnectTimerTask() cancelled ");
								broadcastIntent.putExtra(MISSING_DEVICE_NAME, tmpMissingDevice);
								LocalBroadcastManager.getInstance(ConnectIntentService.this).sendBroadcast(broadcastIntent);

								editorMissingDevices.putString("MISSING_DEVICES", addToArrayList(MissingDevices, tmpMissingDevice));
								editorMissingDevices.commit();

								if (prefLog.getString("ALLOW_DEVICE_NAME_IN_LOG", "Yes").matches("Yes")) {
									Log.v(TAG, "MISSING_DEVICES listString: [" + prefMissingDevices.getString("MISSING_DEVICES", "") + "]");
									log.Logging(getApplicationContext(), "MISSING_DEVICES listString: [" + prefMissingDevices.getString("MISSING_DEVICES", "") + "]");
								}
								else if (prefLog.getString("ALLOW_DEVICE_NAME_IN_LOG", "Yes").matches("No")) {
									Log.v(TAG, "MISSING_DEVICES listString: *****,...,***** " + prefMissingDevices.getString("MISSING_DEVICES", ""));
									log.Logging(getApplicationContext(), "MISSING_DEVICES listString: *****,...,***** ");
								}
							}
*/							if (FinalisRun == 1) {
								Log.v(TAG, "Device in range confirmed ------------------> [" + tmpMissingDevice + "]");
								log.Logging(getApplicationContext(), "Device in range confirmed ------------------> [" + tmpMissingDevice + "]");
								cTT.cancel();
								Log.v(TAG, "ConnectTimerTask() cancelled ");
								log.Logging(getApplicationContext(), "ConnectTimerTask() cancelled ");

								editorMissingDevices.putString("MISSING_DEVICES", removeFromArrayList(MissingDevices, tmpMissingDevice));
								editorMissingDevices.commit();
							}

							if (j == 0) {
								Toast.makeText(getApplicationContext(), "Scanning service has started", Toast.LENGTH_SHORT);
								j = 1;
							}

							tempCancel();
							cTT.cancel();
							sleep_wait(delay_time);
						}
						Log.v(TAG, "Checking the value of 'shouldContinue' -> [" + shouldContinue + "]");
						log.Logging(getApplicationContext(), "Checking the value of 'shouldContinue' -> [" + shouldContinue + "]");
					}

					sleep_wait(100);
				}
				if (shouldContinue == false) {
					LocalBroadcastManager.getInstance(ConnectIntentService.this).unregisterReceiver(MissingDeviceBroadcastReceiver);
					Log.v(TAG, "stopSelf() has been called to stop this ConnectIntentService");
					log.Logging(getApplicationContext(), "stopSelf() has been called to stop this ConnectIntentService");
					stopSelf();
				}
			}
			catch (Exception e1) {
				Log.v(TAG, "Exception raised during 'Scanning of devices'");
				log.Logging(getApplicationContext(), "Exception raised during 'Scanning of devices'");
				e1.printStackTrace();
				Log.v(TAG, "Exception: {" + e1.getMessage() + "}");
				log.Logging(getApplicationContext(), "Exception: {" + e1.getMessage() + "}");
			}

		}
		catch (Exception e2) {
			Log.v(TAG, "Exception raised during adding FinalchosenDevices to FinalPairedDevices");
			log.Logging(getApplicationContext(), "Exception raised during adding FinalchosenDevices to FinalPairedDevices");
			e2.printStackTrace();
			Log.v(TAG, "Exception: {" + e2.getMessage() + "}");
			log.Logging(getApplicationContext(), "Exception: {" + e2.getMessage() + "}");
		}

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

	private String removeFromArrayList(ArrayList<String> mlist, String mstring) {
		ArrayList<String> klist = mlist;
		int k = 0;
		for (String s : klist) {
			if (s.equals(mstring)) {
				break;
			}
			k++;
		}
		if (k < klist.size()) {
			klist.remove(k);
			if (prefLog.getString("ALLOW_DEVICE_NAME_IN_LOG", "Yes").matches("Yes")) {
				Log.v(TAG, mstring + " removed from MissingDeviceList");
				log.Logging(getApplicationContext(), mstring + " removed from MissingDeviceList");
			}
			else if (prefLog.getString("ALLOW_DEVICE_NAME_IN_LOG", "Yes").matches("No")) {
				Log.v(TAG, "***** removed from MissingDeviceList");
				log.Logging(getApplicationContext(), "***** removed from MissingDeviceList");
			}
		}
		return convertToString(klist);
	}

	private String addToArrayList(ArrayList<String> mlist, String mstring) {
		int k = 0;
		ArrayList<String> klist = mlist;
		for (String s : klist) {
			if (s.equals(mstring)) {
				k = 1;
				break;
			}
		}
		if (k==0) {
			klist.add(mstring);
			if (prefLog.getString("ALLOW_DEVICE_NAME_IN_LOG", "Yes").matches("Yes")) {
				Log.v(TAG, mstring + " added to MissingDeviceList");
				log.Logging(getApplicationContext(), mstring + " added to MissingDeviceList");
			}
			else if (prefLog.getString("ALLOW_DEVICE_NAME_IN_LOG", "Yes").matches("No")) {
				Log.v(TAG, "***** added to MissingDeviceList");
				log.Logging(getApplicationContext(), "***** added to MissingDeviceList");
			}
		}
		return convertToString(klist);
	}

	private void runAsForeground() {
		PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, ChosenDeviceList.class), 0);

		Notification serviceStartedNotification=new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.service_notification)
				.setContentTitle("Anti-Theft BT")
				.setContentText("ATBT service is running")
				.setContentIntent(pi).build();

		startForeground(922365, serviceStartedNotification);
	}

	private BroadcastReceiver MissingDeviceBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String missingDevice = intent.getStringExtra(MISSING_DEVICE_NAME);

			Log.v(TAG, "'expiry_time' = [" + expiry_time + "] && 'numOfMissingCounts' = [" + numOfMissingCounts + "] && 'delay_time' = [" + delay_time + "]");
			log.Logging(getApplicationContext(), "expiry_time = " + expiry_time + " && numOfMissingCounts = " + numOfMissingCounts);

//			if ((isThisFirstAlert == false) && ((expiry_time <= 2750 && numOfMissingCounts > 2) || (expiry_time > 2750 && expiry_time <= 5300 && numOfMissingCounts > 1) || (expiry_time > 5300 && numOfMissingCounts > 0))) {
//			if ((expiry_time<2700 && numOfMissingCounts>1) || (expiry_time>=2700 && numOfMissingCounts==1)) {
				Log.v(TAG, "Missing device received in MissingDeviceBroadcastReceiver");
				log.Logging(getApplicationContext(), "Missing device received in MissingDeviceBroadcastReceiver");

				Toast.makeText(context, "Missing Device is: [" + missingDevice + "]", Toast.LENGTH_LONG).show();
				Log.v(TAG, "Toast for Missing device made in MissingDeviceBroadcastReceiver ---> " + missingDevice);
				log.Logging(getApplicationContext(), "Toast for Missing device made in MissingDeviceBroadcastReceiver ---> " + missingDevice);

				Log.v(TAG2, "Missing Device: {" + missingDevice + "}");

				sendNotification(context, missingDevice);
//			}
			isThisFirstAlert = false;
		}
	};

	private void sendNotification(Context context, String missingDevice) {

		NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(context);
		mbuilder.setSmallIcon(R.drawable.missing_notification);

		PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(context, MissingDeviceList.class), 0);
		mbuilder.setContentIntent(pi);
		mbuilder.setContentTitle("Missing: [" + missingDevice + "]");
		mbuilder.setContentText("See list of Missing Devices");
		mbuilder.setSubText("Tap to see list of Missing Devices.");

		SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

		Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		if (isSDPresent == true) {
			Uri msound = Uri.parse(pref.getString("SAMPLE_NOTIFICATION_SOUND", "android.resource://ranjan.maccharapps.anti_theftbt/raw/start_medium1"));
			mbuilder.setSound(msound);
		}
		else {
			if (pref.getBoolean("IS_THIS_START", true)) {
				Uri msound = Uri.parse("android.resource://ranjan.maccharapps.anti_theftbt/raw/start_medium1");
				mbuilder.setSound(msound);
			}
			else {
				Uri msound = Uri.parse("android.resource://ranjan.maccharapps.anti_theftbt/raw/stop1");
				mbuilder.setSound(msound);
			}

		}

		NotificationManager nm = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

		nm.notify(0, mbuilder.build());
	}

}
