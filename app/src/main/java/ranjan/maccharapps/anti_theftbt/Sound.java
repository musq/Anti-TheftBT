package ranjan.maccharapps.anti_theftbt;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Ashish Ranjan on 18-06-2015.
 */
public class Sound extends Activity {

	private static final String TAG = "ranjan.anti_theftbt.TAG";
	private int RQS_OPEN_AUDIO_MP3 = 1;

	ArrayList<MSitem> msitemListFinal;
	MissingSoundAdapter MSadapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sound);

		displaySoundView();

		checkCustomSoundButton();
	}

	private class MSitem {
		String sname = null;
		String svalue = null;

		public MSitem(String sname, String svalue) {
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

	private void displaySoundView() {
		MSitem item;

		final String s1 = "Short 1";
		final String s2 = "Short 2";
		final String m1 = "Medium 1";
		final String m2 = "Medium 2";
		final String l1 = "Long 1";
		final String l2 = "Long 2";
		final String el1 = "Extra Long 1";
		final String el2 = "Extra Long 2";

		final String sv1 = "android.resource://ranjan.maccharapps.anti_theftbt/raw/start_short1";
		final String sv2 = "android.resource://ranjan.maccharapps.anti_theftbt/raw/start_short2";
		final String mv1 = "android.resource://ranjan.maccharapps.anti_theftbt/raw/start_medium1";
		final String mv2 = "android.resource://ranjan.maccharapps.anti_theftbt/raw/start_medium2";
		final String lv1 = "android.resource://ranjan.maccharapps.anti_theftbt/raw/start_long1";
		final String lv2 = "android.resource://ranjan.maccharapps.anti_theftbt/raw/start_long2";
		final String elv1 = "android.resource://ranjan.maccharapps.anti_theftbt/raw/start_extra_long1";
		final String elv2 = "android.resource://ranjan.maccharapps.anti_theftbt/raw/start_extra_long2";

		msitemListFinal = new ArrayList<MSitem>(Arrays.asList(new MSitem(s1, sv1), new MSitem(s2, sv2), new MSitem(m1, mv1), new MSitem(m2, mv2), new MSitem(l1,lv1), new MSitem(l2, lv2), new MSitem(el1,elv1), new MSitem(el2, elv2)));

		MSadapter = new MissingSoundAdapter(this, R.layout.sound_style, msitemListFinal);
		ListView sampleListView = (ListView) findViewById(R.id.missingSoundListID);

		sampleListView.setAdapter(MSadapter);

		SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
		final SharedPreferences.Editor editor = pref.edit();

		sampleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				MSitem item = (MSitem) parent.getItemAtPosition(position);

				switch (item.getSname()) {
					case "Short 1":
						editor.putString("NOTIFICATION_SOUND", sv1);
						editor.putString("NOTIFICATION_SOUND_NAME", SoundPathToName(sv1));
						editor.commit();
						Toast.makeText(Sound.this, "Missing Alert Sound Set: [" + s1 + "]", Toast.LENGTH_SHORT).show();
						break;
					case "Short 2":
						editor.putString("NOTIFICATION_SOUND", sv2);
						editor.putString("NOTIFICATION_SOUND_NAME", SoundPathToName(sv2));
						editor.commit();
						Toast.makeText(Sound.this, "Missing Alert Sound Set: [" + s2 + "]", Toast.LENGTH_SHORT).show();
						break;
					case "Medium 1":
						editor.putString("NOTIFICATION_SOUND", mv1);
						editor.putString("NOTIFICATION_SOUND_NAME", SoundPathToName(mv1));
						editor.commit();
						Toast.makeText(Sound.this, "Missing Alert Sound Set: [" + m1 + "]", Toast.LENGTH_SHORT).show();
						break;
					case "Medium 2":
						editor.putString("NOTIFICATION_SOUND", mv2);
						editor.putString("NOTIFICATION_SOUND_NAME", SoundPathToName(mv2));
						editor.commit();
						Toast.makeText(Sound.this, "Missing Alert Sound Set: [" + m2 + "]", Toast.LENGTH_SHORT).show();
						break;
					case "Long 1":
						editor.putString("NOTIFICATION_SOUND", lv1);
						editor.putString("NOTIFICATION_SOUND_NAME", SoundPathToName(lv1));
						editor.commit();
						Toast.makeText(Sound.this, "Missing Alert Sound Set: [" + l1 + "]", Toast.LENGTH_SHORT).show();
						break;
					case "Long 2":
						editor.putString("NOTIFICATION_SOUND", lv2);
						editor.putString("NOTIFICATION_SOUND_NAME", SoundPathToName(lv2));
						editor.commit();
						Toast.makeText(Sound.this, "Missing Alert Sound Set: [" + l2 + "]", Toast.LENGTH_SHORT).show();
						break;
					case "Extra Long 1":
						editor.putString("NOTIFICATION_SOUND", elv1);
						editor.putString("NOTIFICATION_SOUND_NAME", SoundPathToName(elv1));
						editor.commit();
						Toast.makeText(Sound.this, "Missing Alert Sound Set: [" + el1 + "]", Toast.LENGTH_SHORT).show();
						break;
					case "Extra Long 2":
						editor.putString("NOTIFICATION_SOUND", elv2);
						editor.putString("NOTIFICATION_SOUND_NAME", SoundPathToName(elv2));
						editor.commit();
						Toast.makeText(Sound.this, "Missing Alert Sound Set: [" + el2 + "]", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		});

	}

	private class MissingSoundAdapter extends ArrayAdapter<MSitem> {

		private ArrayList<MSitem> missingSoundList;

		public MissingSoundAdapter(Context context, int textViewResourceId, ArrayList<MSitem> msList) {
			super(context, textViewResourceId, msList);
			this.missingSoundList = new ArrayList<MSitem>();
			this.missingSoundList.addAll(msList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			TextView tvsound;

			if (convertView == null) {
				LayoutInflater minflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = minflater.inflate(R.layout.sound_style, null);

				tvsound = (TextView) convertView.findViewById(R.id.missingSoundItemNameID);
				convertView.setTag(tvsound);
			}
			else {
				tvsound = (TextView) convertView.getTag();
			}

			MSitem item = missingSoundList.get(position);
			tvsound.setText(item.getSname());

			return convertView;
		}
	}

	private void checkCustomSoundButton() {
		Button bCustomSound = (Button) findViewById(R.id.missingSoundButtonID);

		bCustomSound.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent audio_intent;
				audio_intent = new Intent();
				audio_intent.setAction(Intent.ACTION_GET_CONTENT);
				audio_intent.setType("audio/*");
//				ActivityOptions options = ActivityOptions.makeScaleUpAnimation(view, 0,	0, view.getWidth(), view.getHeight());
				startActivityForResult(Intent.createChooser(audio_intent, getString(R.string.select_audio_file_title)), RQS_OPEN_AUDIO_MP3);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == RQS_OPEN_AUDIO_MP3) {
				Uri audioFileUri = data.getData();

				String MP3Path = audioFileUri.getPath();
//				Toast.makeText(this, MP3Path, Toast.LENGTH_SHORT).show();
				SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit();

				editor.putString("NOTIFICATION_SOUND", MP3Path);
				editor.putString("NOTIFICATION_SOUND_NAME", SoundPathToName(MP3Path));
				editor.commit();
				Toast.makeText(Sound.this, "Missing Alert Sound Set: [" + SoundPathToName(MP3Path) + "]", Toast.LENGTH_SHORT).show();

			}
		}
	}

	private String SoundPathToName(String mpath) {
		String path = mpath;

		while (path.indexOf('/') >= 0) {
			path = path.substring(path.indexOf('/') + 1);
		}
		return path;
	}
}
