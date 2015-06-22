package ranjan.maccharapps.anti_theftbt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

/**
 * Created by Ashish Ranjan on 12-06-2015.
 */
public class MissingDeviceList extends Activity {

	private static final String TAG = "ranjan.anti_theftbt.TAG";

	SharedPreferences prefMissingDevices;
	SharedPreferences prefLog;

	private Logging log = new Logging();

	String missingDevicesString;
	ArrayList<String> missingDevices = new ArrayList<String>();
	public ArrayList<MDLitem> mdlitemListFinal = new ArrayList<MDLitem>();
	MissingDeviceListAdapter MDLadapter;

	BluetoothAdapter mBluetoothAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.missing_device_list);

		prefLog = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

		displayMissingListView();

		checkRefreshButton();
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

	private class MDLitem {
		String mdname = null;
		String mdaddress = null;

		public MDLitem(String mdname, String mdaddress) {
			this.mdname = mdname;
			this.mdaddress = mdaddress;
		}

		public String getMdname() {
			return mdname;
		}

		public void setMdname(String mdname) {
			this.mdname = mdname;
		}

		public String getMdaddress() {
			return mdaddress;
		}

		public void setMdaddress(String mdaddress) {
			this.mdaddress = mdaddress;
		}
	}

	private void displayMissingListView() {
		MDLitem item;
		mdlitemListFinal = new ArrayList<MDLitem>();

		prefMissingDevices = getApplicationContext().getSharedPreferences("MissingDevicesPref", MODE_PRIVATE);
		missingDevicesString = prefMissingDevices.getString("MISSING_DEVICES", "");

		if (prefLog.getString("ALLOW_DEVICE_NAME_IN_LOG", "Yes").matches("Yes")) {
			Log.v(TAG, "Before display start: missingDevicesString -> " + missingDevicesString);
			log.Logging(getApplicationContext(), "Before display start: missingDevicesString -> " + missingDevicesString);
		}
		else if (prefLog.getString("ALLOW_DEVICE_NAME_IN_LOG", "Yes").matches("No")) {
			Log.v(TAG, "Before display start: missingDevicesString -> *****,...,***** " + missingDevicesString);
			log.Logging(getApplicationContext(), "Before display start: missingDevicesString -> *****,...,*****");
		}

		missingDevices = new ArrayList<String>();
		missingDevices = convertToArrayList(missingDevicesString);
		Log.v(TAG, "Before removing 0th item from: missingDevices(0) -> " + missingDevices.get(0) + ",");
		log.Logging(getApplicationContext(), "Before removing 0th item from: missingDevices(0) -> " + missingDevices.get(0) + ",");

		if (!missingDevicesString.equals("")) {
			missingDevices.remove(0);

			if (prefLog.getString("ALLOW_DEVICE_NAME_IN_LOG", "Yes").matches("Yes")) {
				Log.v(TAG, "After removing 0th item from: missingDevices(0) -> " + missingDevices.get(0) + ",");
				log.Logging(getApplicationContext(), "After removing 0th item from: missingDevices(0) -> " + missingDevices.get(0) + ",");
			}
			else if (prefLog.getString("ALLOW_DEVICE_NAME_IN_LOG", "Yes").matches("No")) {
				Log.v(TAG, "After removing 0th item from: missingDevices(0) -> *****, [" + missingDevices.get(0) + ",]");
				log.Logging(getApplicationContext(), "After removing 0th item from: missingDevices(0) -> *****,");
			}
		}

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mBluetoothAdapter.isEnabled() == false) {
			mBluetoothAdapter.enable();
			Toast.makeText(getApplicationContext(), "Waiting for bluetooth to start", Toast.LENGTH_SHORT).show();
			Log.v(TAG, "Bluetooth was switched OFF! It has been switched 'ON' programmatically :)");
			log.Logging(getApplicationContext(), "Bluetooth was switched OFF! It has been switched 'ON' programmatically :)");
		}
		Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();

		if (mPairedDevices.size() > 0) {
			int j = 0;
			Log.v(TAG, "mPairedDevices.size() = " + mPairedDevices.size());
			log.Logging(getApplicationContext(), "mPairedDevices.size() = " + mPairedDevices.size());
			Log.v(TAG, "missingDevices.size() = " + missingDevices.size());
			log.Logging(getApplicationContext(), "missingDevices.size() = " + missingDevices.size());

			for (BluetoothDevice mDevice : mPairedDevices) {

				for (String MD : missingDevices) {
					if (MD.equalsIgnoreCase(mDevice.getName())) {
						item = new MDLitem(mDevice.getName(), mDevice.getAddress());
						mdlitemListFinal.add(item);

						if (prefLog.getString("ALLOW_DEVICE_NAME_IN_LOG", "Yes").matches("Yes")) {
							Log.v(TAG, "mdlitemListFinal(" + j + ") = " + mdlitemListFinal.get(j).getMdname());
							log.Logging(getApplicationContext(), "mdlitemListFinal(" + j + ") = " + mdlitemListFinal.get(j).getMdname());
						}
						else if (prefLog.getString("ALLOW_DEVICE_NAME_IN_LOG", "Yes").matches("No")) {
							Log.v(TAG, "mdlitemListFinal(" + j + ") = ***** [" + mdlitemListFinal.get(j).getMdname() + "]");
							log.Logging(getApplicationContext(), "mdlitemListFinal(" + j + ") = *****");
						}

						j++;
					}
				}
			}

		}
		Log.v(TAG, "mdlitemList updated with missingDevices");
		log.Logging(getApplicationContext(), "mdlitemList updated with missingDevices");

		MDLadapter = new MissingDeviceListAdapter(MissingDeviceList.this, R.layout.missing_device_list_style, mdlitemListFinal);
		ListView sampleMissingListView = (ListView) findViewById(R.id.missingDeviceListID);
		Log.v(TAG, "sampleMissingListView created");
		log.Logging(getApplicationContext(), "sampleMissingListView created");

		sampleMissingListView.setAdapter(MDLadapter);
		Log.v(TAG, "sampleMissingListView set the adapter");
		log.Logging(getApplicationContext(), "sampleMissingListView set the adapter");
	}

	private class MissingDeviceListAdapter extends ArrayAdapter<MDLitem> {

		private ArrayList<MDLitem> mdlitemList;

		public MissingDeviceListAdapter(Context context, int textViewResourceId, ArrayList<MDLitem> mdlitemList) {
			super(context, textViewResourceId, mdlitemList);
			this.mdlitemList = new ArrayList<MDLitem>();
			this.mdlitemList.addAll(mdlitemList);
		}

		private class ViewHolder {
			TextView tvname;
			TextView tvaddress;
		}

		@Override
		public int getCount() {

			return  mdlitemList.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder mholder = null;
			Log.v(TAG, String.valueOf(position));

			if (convertView == null) {
				LayoutInflater minflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = minflater.inflate(R.layout.missing_device_list_style, null);

				mholder = new ViewHolder();
				mholder.tvname = (TextView) convertView.findViewById(R.id.missingDeviceNameID);
				mholder.tvaddress = (TextView) convertView.findViewById(R.id.missingDeviceAddressID);
				convertView.setTag(mholder);
			}
			else {
				mholder = (ViewHolder) convertView.getTag();
			}

			MDLitem item = mdlitemList.get(position);
			mholder.tvname.setText(item.getMdname());
			mholder.tvaddress.setText(item.getMdaddress());

			return convertView;
		}
	}

	private void checkRefreshButton() {
		Button bRefresh = (Button) findViewById(R.id.missingRefreshButtonID);

		bRefresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				displayMissingListView();
			}
		});
	}

}
