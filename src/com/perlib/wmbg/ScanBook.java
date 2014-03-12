package com.perlib.wmbg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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

public class ScanBook extends Activity {

	TextView tvBookName;
	TextView tvBookAuthor;
	Button btnAddBook;
	Button btnReturnBook;
	Button btnEditBook;
	
	List<Book> items = new ArrayList<Book>();
	List<Book> matchedItems = new ArrayList<Book>();
	Book result = new Book();
	
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
	    
		Bundle b = getIntent().getExtras();
		items = b.getParcelableArrayList("items");
		result = b.getParcelable("result");
		
		tvBookName.setText(result.getName());
		tvBookAuthor.setText(result.getAuthor());
		
		for(Iterator<Book> i = items.iterator(); i.hasNext();)
		{
			Book item = i.next();
			if(item.getName().equals(result.getName()))
			{
				matchedItems.add(item);
			}
		}
		if(matchedItems.size() < 0)
		{
			btnReturnBook.setVisibility(View.GONE);
			btnEditBook.setVisibility(View.GONE);
		}
	    
		
	    btnAddBook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent addBook = new Intent(getApplicationContext(), AddBook.class);
				addBook.putParcelableArrayListExtra("items", (ArrayList<? extends Parcelable>) items);
				addBook.putExtra("mode", AddBook.MODE_AUTO);
				addBook.putExtra("name", result.getName());
				addBook.putExtra("author", result.getAuthor());
				startActivity(addBook);
			}
		});
	    
	    btnReturnBook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
			}
		});
	    
	    btnEditBook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
