package com.perlib.wmbg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.perlib.wmbg.book.Book;
import com.perlib.wmbg.book.BookJsonAdapter;
import com.perlib.wmbg.book.Library;

public class ScanBook extends Activity implements OnDownloadComplete {

	TextView tvBookName;
	TextView tvBookAuthor;
	Button btnAddBook;
	Button btnReturnBook;
	Button btnEditBook;
	
	IntentIntegrator scanIntegrator = new IntentIntegrator(this);
	
	List<Book> items = new ArrayList<Book>();
	List<Book> matchedItems = new ArrayList<Book>();
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.activity_scanbook);
	    
	    tvBookName = (TextView) findViewById(R.id.tvBookName);
	    tvBookAuthor = (TextView) findViewById(R.id.tvBookAuthor);
	    btnAddBook = (Button) findViewById(R.id.btnAddBook);
	    btnReturnBook = (Button) findViewById(R.id.btnReturnBook);
	    btnEditBook = (Button) findViewById(R.id.btnEditBook);
	    
	    
	    scanIntegrator.initiateScan();
	    
	    btnAddBook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	    
	    btnReturnBook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	    
	    btnEditBook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null) {
			String contents = scanResult.getContents();
			if(contents != null)
			{
				Library.handleISBN(contents, getApplicationContext(), this);
			}
		}
	}

	@Override
	public void OnTaskFinished(String result) {
		
		Gson gson = new Gson();
		BookJsonAdapter adapter = gson.fromJson(result , BookJsonAdapter.class);
		if(adapter == null)
		{
			Toast.makeText(getApplicationContext(), getString(R.string.InvalidISBN) , Toast.LENGTH_SHORT).show();
			return;
		}
		Book resultBook = adapter.convertToBook();
		if(resultBook == null)
		{
			Toast.makeText(getApplicationContext(), getString(R.string.InvalidISBN) , Toast.LENGTH_SHORT).show();
			return;
		}
		
		tvBookName.setText(resultBook.getName());
		tvBookAuthor.setText(resultBook.getAuthor());
		
		for(Iterator<Book> i = items.iterator(); i.hasNext();)
		{
			Book item = i.next();
			if(item.getName().equals(resultBook.getName()))
			{
				matchedItems.add(item);
			}
		}
		
		if(matchedItems.size() < 0)
		{
			btnReturnBook.setVisibility(View.GONE);
			btnEditBook.setVisibility(View.GONE);
		}
	}
}
