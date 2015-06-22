package ranjan.maccharapps.anti_theftbt;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ashish Ranjan on 13-06-2015.
 */
public class Logging {

	public void Logging(Context context, String logMessage) {

		SharedPreferences logPref = context.getSharedPreferences("MyLogPref", context.MODE_PRIVATE);
		String logFileName = "ATBT_" + logPref.getString("LOG_FILE_NAME", null);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String currentDateandTime = sdf.format(new Date());

		if (logPref.getString("LOG_FILE_NAME", null) != null) {

			File folder = new File(Environment.getExternalStorageDirectory().toString() + "/Anti-Theft BT (ATBT) Logs");
			if (!folder.exists()) {
				folder.mkdirs();
			}
			//Save the path as a string value
			String extStorageDirectory = folder.toString();
			//Create New file and name it Image2.PNG
			File myFile = new File(extStorageDirectory, logFileName);

			if(!myFile.exists()) {
				try {
					myFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				FileWriter fw = new FileWriter(myFile,true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(currentDateandTime + "  ::  " + logMessage + "\n");
				bw.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
