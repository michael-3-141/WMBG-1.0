package com.perlib.wmbg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.perlib.wmbg.R;
import com.perlib.wmbg.book.Library;
import com.perlib.wmbg.book.Settings;

public class SettingsActivity extends ActionBarActivity {

	EditText customMessage;
	Button btnSave;
	Settings settings;
	ToggleButton tbDeleteConfirm;
	Spinner spSwipeMode;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_settings);
	    
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
	    customMessage = (EditText) findViewById(R.id.customMessage);
	    btnSave = (Button) findViewById(R.id.btnSave);
	    tbDeleteConfirm = (ToggleButton)findViewById(R.id.tbDeleteConfirm);
	    spSwipeMode = (Spinner)findViewById(R.id.spSwipeMode);
	    
	    settings = Library.loadSettings(getApplicationContext());
	    customMessage.setText(settings.getEmailMessage());
	    tbDeleteConfirm.setChecked(settings.isConfirmDelete());
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.swipemodes, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spSwipeMode.setAdapter(adapter);
	    spSwipeMode.setSelection(settings.getSwipeMode());
	    
	    btnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				settings.setEmailMessage(customMessage.getText().toString());
				settings.setConfirmDelete(tbDeleteConfirm.isChecked());
				settings.setSwipeMode(spSwipeMode.getSelectedItemPosition());
				Library.saveSettings(settings);
				Intent main = new Intent(getApplicationContext(), MainActivity.class);
				main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(main);
			}
		});
	}
	
	
	
}
