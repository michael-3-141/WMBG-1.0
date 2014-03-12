package com.perlib.wmbg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainMenu extends Activity {

	Button btnSearchBook;
	Button btnScanBook;
	Button btnManualAddBook;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.activity_menu);
	    
	    btnSearchBook = (Button) findViewById(R.id.btnSeachBooks);
	    btnScanBook = (Button) findViewById(R.id.btnScanBook);
	    btnManualAddBook = (Button) findViewById(R.id.btnManualAddBook);
	    
	    
	    btnSearchBook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent searchBook = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(searchBook);
			}
		});
	    
	    btnScanBook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent scanBook = new Intent(getApplicationContext(), ScanBook.class);
				startActivity(scanBook);
				
			}
		});
	    
	    btnManualAddBook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent addBook = new Intent(getApplicationContext(), AddBook.class);
				startActivity(addBook);
			}
		});
	}

}
