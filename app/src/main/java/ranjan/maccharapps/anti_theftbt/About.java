package ranjan.maccharapps.anti_theftbt;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Ashish Ranjan on 15-06-2015.
 */
public class About extends Activity {

	private static final String TAG = "ranjan.anti_theftbt.TAG";

	private Logging log = new Logging();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		Log.v(TAG, "Inside About's onCreate");
		log.Logging(getApplicationContext(), "Inside About's onCreate");

		ImageView ivWordpress = (ImageView) findViewById(R.id.ivAboutMeWordpressID);
		ImageView ivGithub = (ImageView) findViewById(R.id.ivAboutMeGithubID);
		ImageView ivGmail = (ImageView) findViewById(R.id.ivAboutMeGmailID);

		ivWordpress.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.addCategory(Intent.CATEGORY_BROWSABLE);
				intent.setData(Uri.parse("https://ashish28ranjan.wordpress.com/"));
				try {
					startActivity(intent);
					Log.v(TAG, "Wordpress link opened from 'About Me'");
					log.Logging(getApplicationContext(), "Wordpress link opened from 'About Me'");
				}
				catch(Exception e) {
					e.printStackTrace();
					Log.v(TAG, "Exception: {" + e.getMessage() + "}");
					log.Logging(getApplicationContext(), "Exception: {" + e.getMessage() + "}");
				}
			}
		});

		ivGithub.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.addCategory(Intent.CATEGORY_BROWSABLE);
				intent.setData(Uri.parse("https://github.com/ashish28ranjan"));
				try {
					startActivity(intent);
					Log.v(TAG, "Github link opened from 'About Me'");
					log.Logging(getApplicationContext(), "Github link opened from 'About Me'");
				}
				catch(Exception e) {
					e.printStackTrace();
					Log.v(TAG, "Exception: {" + e.getMessage() + "}");
					log.Logging(getApplicationContext(), "Exception: {" + e.getMessage() + "}");
				}
			}
		});

		ivGmail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String yourMail = "ashish28ranjan@gmail.com";
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("message/rfc822");
				intent.putExtra(Intent.EXTRA_EMAIL, new String[]{yourMail});
				intent.putExtra(Intent.EXTRA_SUBJECT, "Anti-Theft BT issue");
				intent.putExtra(Intent.EXTRA_TEXT, "(Please attach corresponding Log file which is stored in sdcard/Anti-Theft BT (ATBT) Logs/...)");
				try {
					startActivity(intent);
					Log.v(TAG, "Gmail opened from 'About Me'");
					log.Logging(getApplicationContext(), "Gmail opened from 'About Me'");
				}
				catch(Exception e) {
					Toast.makeText(About.this, "Have no email application!", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
					Log.v(TAG, "Exception: {" + e.getMessage() + "}");
					log.Logging(getApplicationContext(), "Exception: {" + e.getMessage() + "}");
				}
			}
		});

		TextView tvEpicBrowser = (TextView) findViewById(R.id.aboutMyChoiceDataEpicBrowserID);
		tvEpicBrowser.setClickable(true);
		tvEpicBrowser.setMovementMethod(LinkMovementMethod.getInstance());
		String text = "<a href='https://www.epicbrowser.com/'> Please, at least pay their website a visit and decide for yourself.</a>";
		tvEpicBrowser.setText(Html.fromHtml(text));
	}
}
