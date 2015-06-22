package ranjan.maccharapps.anti_theftbt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by Ashish Ranjan on 11-06-2015.
 */
public class TempConnectActivity extends Thread {

	private final BluetoothSocket mSocket;
	//	private final BluetoothDevice mDevice;
	private BluetoothAdapter mBluetoothAdapter;

	private static final String TAG = "ranjan.anti_theftbt.TAG";
	private Logging log = new Logging();

	private UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	ChosenDeviceList mCDList = new ChosenDeviceList();

	public TempConnectActivity(BluetoothDevice tdevice) {

		BluetoothSocket tmp = null;
		Log.v(TAG, "TempConnectActivity initialized");

		try {
			if(true) {
				Method method;

				method = tdevice.getClass().getMethod("createRfcommSocket", new Class[] { int.class } );
				tmp = (BluetoothSocket) method.invoke(tdevice, 1);
			}
			else {
				tmp = tdevice.createRfcommSocketToServiceRecord(MY_UUID);
			}
			Log.v(TAG, "tmp Socket created");
		}
		catch (Exception e) {
			Log.v(TAG, "Exception raised in creating tmp Socket");
			Log.v(TAG, "Exception message: {" + e.getMessage() + "}");
			e.printStackTrace();
		}

		mSocket = tmp;
	}

	public int tempRun() {

		int isRun = 0;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mBluetoothAdapter.isEnabled() == false) {
			mBluetoothAdapter.enable();
			Log.v(TAG, "Bluetooth was switched OFF! It has been switched 'ON' programmatically :)");
		}
		mBluetoothAdapter.cancelDiscovery();
		Log.v(TAG, "Discovery of devices cancelled");

		try {
			mSocket.connect();
			isRun = 1;
			Log.v(TAG, "Connection to socket is successfull");
		}
		catch (Exception connectException) {
			Log.v(TAG, "Exception raised during Connection to socket. Trying to close the socket...");
			Log.v(TAG, "Exception message: {" + connectException.getMessage() + "}");
			connectException.printStackTrace();

			try {
				mSocket.close();
				Log.v(TAG, "Socket has been closed");
			}
			catch (Exception closeException) {
				Log.v(TAG, "Exception raised during closing socket");
				Log.v(TAG, "Exception message: {" + closeException.getMessage() + "}");
				closeException.printStackTrace();
			}
			return isRun;
		}
		return isRun;

	}

	public void tempCancel() {
		try {
			mSocket.close();
			Log.v(TAG, "Cancel function has successfully closed the socket");
		}
		catch (Exception cancelException) {
			Log.v(TAG, "Cancel function cannot close the socket. Shit man! :(");
			Log.v(TAG, "Exception message: {" + cancelException.getMessage() + "}");
			cancelException.printStackTrace();
		}
	}

}











