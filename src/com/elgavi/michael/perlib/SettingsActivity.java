package com.elgavi.michael.perlib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.elgavi.michael.perlib.book.Library;
import com.elgavi.michael.perlib.book.Settings;

public class SettingsActivity extends Activity {

	EditText customMessage;
	Button btnSave;
	Settings settings;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.activity_settings);
	    customMessage = (EditText) findViewById(R.id.customMessage);
	    btnSave = (Button) findViewById(R.id.btnSave);
	    
	    settings = Library.loadSettings(getApplicationContext());
	    customMessage.setText(settings.getEmailMessage());
	    
	    btnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				settings.setEmailMessage(customMessage.getText().toString());
				Library.saveSettings(settings);
				Intent main = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(main);
			}
		});
	}
	
	
	
}
