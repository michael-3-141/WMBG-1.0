package com.perlib.wmbg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.perlib.wmbg.book.Book;
import com.perlib.wmbg.book.Library;

public class ScanBook extends ActionBarActivity {

	TextView tvBookName;
	TextView tvBookAuthor;
	Button btnAddBook;
	Button btnReturnBook;
	Button btnEditBook;
	
	List<Book> items = new ArrayList<Book>();
	List<Book> matchedItems = new ArrayList<Book>();
	List<Integer> matchedItemsPos = new ArrayList<Integer>();
	List<Book> matchedLendedItems = new ArrayList<Book>();
	List<Integer> matchedLendedItemsPos = new ArrayList<Integer>();
	Book result = new Book();
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_scanbook);
	    
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	    
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
		
		int j = 0;
		for(Iterator<Book> i = items.iterator(); i.hasNext();)
		{
			Book item = i.next();
			if(item.getName().equals(result.getName()))
			{
				matchedItems.add(item);
				matchedItemsPos.add(j);
				if(item.getLendedTo().length() > 0)
				{
					matchedLendedItems.add(item);
					matchedLendedItemsPos.add(j);
				}
				j++;
			}
		}
		if(matchedItems.size() == 0)
		{
			btnReturnBook.setVisibility(View.GONE);
			btnEditBook.setVisibility(View.GONE);
		}
		else
		{
			boolean lended = false;
			boolean isFirst = true;
			String display = getString(R.string.returnScannedBook);
			for(Iterator<Book> i = matchedItems.iterator(); i.hasNext();)
			{
				Book item = i.next();
				if(item.getLendedTo().length() > 0)
				{
					if(!lended)lended = true;
					if(isFirst)
					{
						display += " " + item.getLendedTo();
					}
					else
					{
						display += " or " + item.getLendedTo();
					}
					isFirst = false;
				}
			}
			if(!lended)
			{
				btnReturnBook.setVisibility(View.GONE);
			}
			else
			{
				btnReturnBook.setText(display);
			}
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
				
				if(matchedLendedItems.size() == 1)
				{
					Book item = matchedLendedItems.get(0);
					item.setLendedTo("");
					item.setDateLended(-1);
					item.setEmail("");
					items.set(matchedLendedItemsPos.get(0), item);
					Library.saveInfo(items);
					Intent main = new Intent(getApplicationContext(), MainActivity.class);
					main.putParcelableArrayListExtra("items", (ArrayList<? extends Parcelable>) items);
					startActivity(main);
				}
				else
				{
					List<String> display = new ArrayList<String>();
					for(Iterator<Book> i = matchedLendedItems.iterator(); i.hasNext();)
					{
						Book item = i.next();
						display.add(getString(R.string.lendedToDisplay)+item.getLendedTo());
					}
					String[] displayArray = new String[]{};
					displayArray = display.toArray(displayArray);
					AlertDialog.Builder duplicateDialog = new AlertDialog.Builder(ScanBook.this);
					duplicateDialog.setTitle(getString(R.string.duplicateBooks)).setItems(displayArray, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							Book item = matchedLendedItems.get(which);
							item.setLendedTo("");
							item.setDateLended(-1);
							item.setEmail("");
							items.set(matchedLendedItemsPos.get(which), item);
							Library.saveInfo(items);
							Intent main = new Intent(getApplicationContext(), MainActivity.class);
							main.putParcelableArrayListExtra("items", (ArrayList<? extends Parcelable>) items);
							startActivity(main);
						}
					});
					duplicateDialog.show();
				}
			}
		});
	    
	    btnEditBook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(matchedItems.size() == 1)
				{
					Intent editBook = new Intent(getApplicationContext(), EditBook.class);
					editBook.putParcelableArrayListExtra("items", (ArrayList<? extends Parcelable>) items);
					editBook.putExtra("position", matchedItemsPos.get(0));
					startActivity(editBook);
				}
				else
				{
					List<String> display = new ArrayList<String>();
					for(Iterator<Book> i = matchedItems.iterator(); i.hasNext();)
					{
						Book item = i.next();
						if(item.getLendedTo().length() < 1)
						{
							display.add(getString(R.string.lendedToDisplay) + getString(R.string.none));
						}
						else
						{
							display.add(getString(R.string.lendedToDisplay)+item.getLendedTo());
						}
					}
					String[] displayArray = new String[]{};
					displayArray = display.toArray(displayArray);
					AlertDialog.Builder duplicateDialog = new AlertDialog.Builder(ScanBook.this);
					duplicateDialog.setTitle(getString(R.string.duplicateBooks)).setItems(displayArray, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							Intent editBook = new Intent(getApplicationContext(), EditBook.class);
							editBook.putParcelableArrayListExtra("items", (ArrayList<? extends Parcelable>) items);
							editBook.putExtra("position", matchedItemsPos.get(which));
							startActivity(editBook);
						}
					});
					duplicateDialog.show();
				}
			}
			
		});
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		items = Library.loadData();
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
		Library.saveInfo(items);
	}
}
