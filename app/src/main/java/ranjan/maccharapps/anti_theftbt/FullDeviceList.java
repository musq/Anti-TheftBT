package ranjan.maccharapps.anti_theftbt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

/**
 * Created by Ashish Ranjan on 11-06-2015.
 */
public class FullDeviceList extends Activity {

	public final static String CHOSEN_DEVICES_LIST = "ranjan.maccharapps.anti_theftbt.CHOSEN_DEVICES_LIST";
	public final static String TAG = "ranjan.anti_theftbt.TAG";
	private Logging log = new Logging();

	private BluetoothAdapter mBluetoothAdapter;
	FullDeviceListAdapter FDLadapter = null;
	SharedPreferences prefLog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_device_list);

		prefLog = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

		displayListView();

		checkSaveButtonClick();
	}

	private class FDLitem {

		String fdname = null;
		String fdaddress = null;
		boolean fdselect = false;

		public FDLitem(String fdname, String fdaddress, boolean fdselect) {
			super();
			this.fdname = fdname;
			this.fdaddress = fdaddress;
			this.fdselect = fdselect;
		}

		public String getFdname() {
			return fdname;
		}

		public void setFdname(String fdname) {
			this.fdname = fdname;
		}

		public String getFdaddress() {
			return fdaddress;
		}

		public void setFdaddress(String fdaddress) {
			this.fdaddress = fdaddress;
		}

		public boolean isFdselect() {
			return fdselect;
		}

		public void setFdselect(boolean fdselect) {
			this.fdselect = fdselect;
		}
	}

	private void displayListView() {

		ArrayList<FDLitem> fdlitemList = new ArrayList<FDLitem>();
		FDLitem item;

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mBluetoothAdapter.isEnabled() == false) {
			mBluetoothAdapter.enable();
			Toast.makeText(getApplicationContext(), "Waiting for bluetooth to start", Toast.LENGTH_SHORT).show();
			Log.v(TAG, "Bluetooth was switched OFF! It has been switched 'ON' programmatically :)");
			log.Logging(getApplicationContext(), "Bluetooth was switched OFF! It has been switched 'ON' programmatically :)");
		}
		Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();

		if (mPairedDevices.size() > 0) {
			int i = 0;
			for (BluetoothDevice mDevice : mPairedDevices) {
				item = new FDLitem(mDevice.getName(), mDevice.getAddress(), false);
				fdlitemList.add(item);
			}
		}

		FDLadapter = new FullDeviceListAdapter(this, R.layout.full_device_list_style, fdlitemList);
		ListView sampleListView = (ListView) findViewById(R.id.fullDeviceListID);

		sampleListView.setAdapter(FDLadapter);


		sampleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				FDLitem item = (FDLitem) parent.getItemAtPosition(position);
//				Toast.makeText(getApplicationContext(), "Clicked on Row: " + item.getFdname(), Toast.LENGTH_SHORT).show();
				item.setFdselect(item.isFdselect());
			}
		});
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


	private class FullDeviceListAdapter extends ArrayAdapter<FDLitem> {

		private ArrayList<FDLitem> fdlitemList;

		public FullDeviceListAdapter(Context context, int textViewResourceId, ArrayList<FDLitem> fdlitemList) {
			super(context, textViewResourceId, fdlitemList);
			this.fdlitemList = new ArrayList<FDLitem>();
			this.fdlitemList.addAll(fdlitemList);
		}

		private class ViewHolder {
			TextView tvname;
			TextView tvaddress;
			CheckBox cbselect;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder mholder = null;

			if(convertView == null) {
				LayoutInflater minflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = minflater.inflate(R.layout.full_device_list_style, null);

				mholder = new ViewHolder();
				mholder.tvname = (TextView) convertView.findViewById(R.id.fullDeviceNameID);
				mholder.tvaddress = (TextView) convertView.findViewById(R.id.fullDeviceAddressID);
				mholder.cbselect = (CheckBox) convertView.findViewById(R.id.fullCheckBoxID);

				convertView.setTag(mholder);

				mholder.cbselect.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v;
						TextView tv = (TextView) v;
						FDLitem item = (FDLitem) cb.getTag();

//						Toast.makeText(getApplicationContext(), "Clicked on Checkbox: " + tv.getText() + " is " + cb.isChecked(), Toast.LENGTH_SHORT).show();
						item.setFdselect(cb.isChecked());

					}
				});

			}
			else {
				mholder = (ViewHolder) convertView.getTag();
			}

			FDLitem item = fdlitemList.get(position);
			mholder.tvname.setText(item.getFdname());
			mholder.tvaddress.setText(item.getFdaddress());
			mholder.cbselect.setChecked(item.isFdselect());
			mholder.cbselect.setTag(item);

			return convertView;

		}
	}

	private void checkSaveButtonClick() {

		Button bSave = (Button) findViewById(R.id.fullSaveButtonID);

		bSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit();

				SharedPreferences prefMissingDevices = getApplicationContext().getSharedPreferences("MissingDevicesPref", MODE_PRIVATE);
				SharedPreferences.Editor editorMissingDevices = prefMissingDevices.edit();
				editorMissingDevices.clear();
				editorMissingDevices.commit();

				StringBuffer responseText = new StringBuffer();
				responseText.append("The following are selected...\n");

				ArrayList<FDLitem> fdlitemList = FDLadapter.fdlitemList;
				ArrayList<String> chosenDevices = new ArrayList<String>();
				ArrayList<String> isDeviceInRange = new ArrayList<String>();

				Log.v(TAG, "chosenDevices ArrayList created");
				log.Logging(getApplicationContext(), "chosenDevices ArrayList created");

				for(int i=0; i<fdlitemList.size(); i++) {
					FDLitem item = fdlitemList.get(i);
					if(item.isFdselect()) {
						if (prefLog.getString("ALLOW_DEVICE_NAME_IN_LOG", "Yes").matches("Yes")) {
							Log.v(TAG, "Added to FullDeviceList's chosenDevices: " + item.getFdaddress());
							log.Logging(getApplicationContext(), "Added to FullDeviceList's chosenDevices: " + item.getFdaddress());
						}
						else if (prefLog.getString("ALLOW_DEVICE_NAME_IN_LOG", "Yes").matches("No")) {
							Log.v(TAG, "Added to FullDeviceList's chosenDevices: **:**:**:**:**:** " + item.getFdaddress());
							log.Logging(getApplicationContext(), "Added to FullDeviceList's chosenDevices: **:**:**:**:**:**");
						}

						chosenDevices.add(item.getFdaddress());

						isDeviceInRange.add("no");
					}
				}

				String chosenDevicesString = convertToString(chosenDevices);
				editor.putString("CHOSEN_DEVICES_STRING", chosenDevicesString);
				editor.commit();

				Log.v(TAG, "FullDeviceList's 'chosenDevices' ArrayList updated");
				if (prefLog.getString("ALLOW_DEVICE_NAME_IN_LOG", "Yes").matches("Yes")) {
					Log.v(TAG, "chosenDevicesString---> " + chosenDevicesString);
					log.Logging(getApplicationContext(), "chosenDevicesString---> " + chosenDevicesString);
				}
				else if (prefLog.getString("ALLOW_DEVICE_NAME_IN_LOG", "Yes").matches("No")) {
					Log.v(TAG, "chosenDevicesString---> **:**:**:**:**:**,...,**:**:**:**:**:** " + chosenDevicesString);
					log.Logging(getApplicationContext(), "chosenDevicesString---> **:**:**:**:**:**,...,**:**:**:**:**:**");
				}

				finish();
			}
		});


	}

}














