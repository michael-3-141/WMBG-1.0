package com.perlib.wmbg.activities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.gson.Gson;
import com.nhaarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;
import com.perlib.wmbg.R;
import com.perlib.wmbg.asyncTasks.DownloadInfo;
import com.perlib.wmbg.book.Book;
import com.perlib.wmbg.book.BookJsonAdapter;
import com.perlib.wmbg.book.Settings;
import com.perlib.wmbg.custom.BookAdapter;
import com.perlib.wmbg.custom.Library;
import com.perlib.wmbg.interfaces.OnDownloadComplete;

public class MainActivity extends ActionBarActivity implements OnDownloadComplete{

	List<Book> items = new ArrayList<Book>(); 
	Settings settings;
	DownloadInfo downloader;
	OnDownloadComplete downloadListener = this;
	ListView bookList;
	BookAdapter adapter;
	SwipeDismissAdapter swipeAdapter;
	EditText etSearch;
	List<Map<String,String>> displayList = new ArrayList<Map<String,String>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//test
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		bookList = (ListView)findViewById(R.id.bookList);
		etSearch = (EditText) findViewById(R.id.etSearch);
		
		settings = Library.loadSettings(getApplicationContext());
		
		if(getIntent().getExtras() != null)
		{
			Bundle b = getIntent().getExtras();
			items = b.getParcelableArrayList("items");
		}
		adapter = new BookAdapter(items, getApplicationContext());
		swipeAdapter = new SwipeDismissAdapter(adapter ,new OnDismissCallback() {
			
			@Override
			public void onDismiss(AbsListView listView, int[] reverseSortedPositions) {
			    for (int position : reverseSortedPositions) {
			    	if(settings.getSwipeMode() == Settings.MODE_DELETE_ITEM)
			    	{
			    		deleteItem(position);
			    	}
			    	else if(settings.getSwipeMode() == Settings.MODE_RETURN_ITEM)
			    	{
			    		returnItem(position);
			    	}
			    }
				
			}
		});
		
		if(settings.getSwipeMode() == Settings.MODE_NOTHING)
		{
			bookList.setAdapter(adapter);
		}
		else
		{
			bookList.setAdapter(swipeAdapter);
			swipeAdapter.setAbsListView(bookList);
		}
		
		etSearch.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				MainActivity.this.adapter.getFilter().filter(s);
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
		bookList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, final View view,
					final int position, long id) {
				
				AlertDialog.Builder options_builder = new AlertDialog.Builder(MainActivity.this);
				String[] display;
				if(items.get(position).isLended())
				{
					display = new String[]{getString(R.string.sendReminder), getString(R.string.markAsReturned), getString(R.string.delete)};
				}
				else
				{
					display = new String[]{getString(R.string.delete)};
				}
				options_builder.setTitle(getString(R.string.options)).setItems(display, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						if(items.get(position).isLended())
						{
							switch(which){
							case 0:
								
								String uriText = null;
								uriText =
								"mailto:"+items.get(position).getEmail() + 
								"?subject=" + Uri.encode(getString(R.string.emailSubject), "UTF-8") + 
								"&body=" + Uri.encode(settings.getEmailMessage().replaceAll("@book@", items.get(position).getName()));
								Uri uri = Uri.parse(uriText);
						        Intent myActivity2 = new Intent(Intent.ACTION_SENDTO);                              
						        myActivity2.setData(uri);
						        startActivity(Intent.createChooser(myActivity2, getString(R.string.sendEmail)));
								break;
							case 1:
								returnItem(position);
								break;
							case 2:
								deleteItem(position);
								break;
							default:
								break;
							}
						}
						else
						{
							deleteItem(position);
						}
					}
				});
				
				AlertDialog dialog = options_builder.create();
				dialog.show();
				
				

				return true;
			}
		});
		
		bookList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				goto_editbook(position);
				
			}

		});
		
		
	}
	

	private void goto_editbook(int position) {
		
		Intent addbook = new Intent(getApplicationContext(), EditBook.class);
		Bundle b = new Bundle();
		b.putParcelableArrayList("items", (ArrayList<? extends Parcelable>) items);
		b.putInt("position", position);
		addbook.putExtras(b);
		startActivity(addbook);
	}
	
	private void refreshList()
	{
		adapter.setItems(items);
		adapter.notifyDataSetChanged();
	}

	private void deleteItem(final int position)
	{
		if(settings.isConfirmDelete())
		{
			AlertDialog.Builder delete_builder = new AlertDialog.Builder(MainActivity.this);
			delete_builder.setPositiveButton(getString(R.string.deleteYes) , new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					items.remove(position);
					displayList.remove(position);
					swipeAdapter.notifyDataSetChanged();
					Library.saveInfo(items);
				}
			});
			delete_builder.setNegativeButton(getString(R.string.deleteCancel), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					
				}
			});
			delete_builder.setMessage(getString(R.string.deleteConfirm) + ' ' + '"' + items.get(position).getName() + '"' + "?").setTitle(getString(R.string.deleteConfirmTitle));
			AlertDialog dialog = delete_builder.create();
			dialog.show();
		}
		else
		{
			items.remove(position);
			displayList.remove(position);
			swipeAdapter.notifyDataSetChanged();
			Library.saveInfo(items);
		}
	}
	
	private void returnItem(final int position)
	{
		Book item = items.get(position);
		item.setLendedTo("");
		item.setEmail("");
		item.setDateLended(-1);
		items.set(position, item);
		Library.saveInfo(items);
		refreshList();
	}
	
	private void returnOrDeleteItem(final int position)
	{
		AlertDialog.Builder delete_or_return_builder = new AlertDialog.Builder(MainActivity.this);
		delete_or_return_builder.setPositiveButton(getString(R.string.chooseReturn) , new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				returnItem(position);
			}
		});
		delete_or_return_builder.setNegativeButton(getString(R.string.chooseDelete), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				deleteItem(position);
			}
		});
		delete_or_return_builder.setMessage(getString(R.string.returnOrDelete));
		AlertDialog dialog = delete_or_return_builder.create();
		dialog.show();
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
		final List<Integer> matcheIds = new ArrayList<Integer>();
		List<Book> matches = new ArrayList<Book>();
		List<String> matchDisplay = new ArrayList<String>();
		int it = 0;
		for(Iterator<Book> i = items.iterator(); i.hasNext(); )
		{
			Book item = i.next();
			if(item.getName().equals(resultBook.getName()))
			{
				matcheIds.add(it);
				matches.add(item);
				matchDisplay.add("Lended To: " + item.getLendedTo());
			}
			it++;
		}
		if(matches.size() == 1)
		{
			returnOrDeleteItem(matcheIds.get(0));
		}
		else if(matches.size() == 0)
		{
			return;
		}
		else
		{
			String[] displayArray = new String[]{};
			displayArray = matchDisplay.toArray(displayArray);
			AlertDialog.Builder options_builder = new AlertDialog.Builder(MainActivity.this);
			options_builder.setTitle(getString(R.string.duplicateBooks)).setItems(displayArray, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					returnOrDeleteItem(matcheIds.get(which));
				}
			});
			
			AlertDialog dialog = options_builder.create();
			dialog.show();
		}
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		items = Library.loadData();
		refreshList();
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
