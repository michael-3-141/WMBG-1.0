package com.perlib.wmbg.activities;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.perlib.wmbg.R;
import com.perlib.wmbg.asyncTasks.GetContactEmail;
import com.perlib.wmbg.asyncTasks.GetContactNames;
import com.perlib.wmbg.book.Book;
import com.perlib.wmbg.custom.Library;
import com.perlib.wmbg.interfaces.OnContactLoadingComplete;
import com.perlib.wmbg.interfaces.OnEmailLoadingListener;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

// TODO: Auto-generated Javadoc
/**
 * The Class EditBook.
 */
public class EditBook extends ActionBarActivity implements OnContactLoadingComplete, OnEmailLoadingListener {

	/** The items. */
	List<Book> items = new ArrayList<Book>();
	
	/** The edit pos. */
	int editPos;
	
	/** The edited item. */
	Book editedItem;
	
	/** The auto names. */
	List<String> autoNames = new ArrayList<String>();
	
	/** The et lended to. */
	AutoCompleteTextView etLendedTo;
	
	/** The dp date lended. */
	DatePicker dpDateLended;
	
	/** The adapter. */
	private ArrayAdapter<String> adapter;
	
	/** The contact name loader. */
	GetContactNames contactNameLoader;
	
	/** The contact email loader. */
	GetContactEmail contactEmailLoader;
	
	/** The contact email listener. */
	OnEmailLoadingListener contactEmailListener = this;
	
	/** The et email. */
	EditText etEmail;
	
	/** The btn return book. */
	Button btnReturnBook;
	
	/** The btn send reminder. */
	Button btnSendReminder;
	
	/** The btn delete. */
	Button btnDelete;
	
	/** The name id map. */
	private HashMap<Integer, String> nameIdMap;
	
	/**
	 *  Called when the activity is first created.
	 *
	 * @param savedInstanceState the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_editbook);
	    
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	    
	    Bundle b = getIntent().getExtras();
	    items = b.getParcelableArrayList("items");
	    editPos = b.getInt("position");
	    
	    final EditText etbookname = (EditText)findViewById(R.id.etBookName);
	    final EditText etBookAuthor = (EditText)findViewById(R.id.etAuthorName);
	    etLendedTo = (AutoCompleteTextView)findViewById(R.id.etLendedTo);
	    final Button btnAddBook = (Button)findViewById(R.id.btnAddBook);
	    etEmail = (EditText)findViewById(R.id.etEmail);
	    dpDateLended = (DatePicker)findViewById(R.id.dpDateLended);
	    btnDelete = (Button) findViewById(R.id.btnDelete);
	    btnReturnBook = (Button) findViewById(R.id.btnReturnBook);
	    btnSendReminder = (Button) findViewById(R.id.btnSendReminder);
	    
	    editedItem = items.get(editPos);
	    etbookname.setText(editedItem.getName());
	    etBookAuthor.setText(editedItem.getAuthor());
	    etLendedTo.setText(editedItem.getLendedTo());
	    etEmail.setText(editedItem.getEmail());
	    GregorianCalendar editedDate = new GregorianCalendar();
	    editedDate.setTimeInMillis(editedItem.getDateLended()*1000);
	    dpDateLended.updateDate(editedDate.get(GregorianCalendar.YEAR), editedDate.get(GregorianCalendar.MONTH), editedDate.get(GregorianCalendar.DAY_OF_MONTH));
	    contactNameLoader = new GetContactNames(this, getContentResolver());
	    startContactSearch();
	    
	    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, autoNames);
	    etLendedTo.setAdapter(adapter);
	    
	    if(!editedItem.isLended())
	    {
	    	btnReturnBook.setVisibility(View.GONE);
	    	btnSendReminder.setVisibility(View.GONE);
	    }
	    
	    btnDelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Library.deleteItem(editPos, Library.loadSettings(getApplicationContext()), EditBook.this , new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						items.remove(editPos);
						Library.saveInfo(items);
						Intent main = new Intent(getApplicationContext(), MainActivity.class);
						main.putParcelableArrayListExtra("items", (ArrayList<? extends Parcelable>) items);
						startActivity(main);
					}
				}, items);
				
			}
		});
	    
	    btnReturnBook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				items = Library.returnItem(editPos, items);
				Intent main = new Intent(getApplicationContext(), MainActivity.class);
				main.putParcelableArrayListExtra("items", (ArrayList<? extends Parcelable>) items);
				startActivity(main);
			}
		});
	    
	    btnSendReminder.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String uriText = null;
				uriText =
				"mailto:" + editedItem.getEmail() + 
				"?subject=" + Uri.encode(getString(R.string.emailSubject), "UTF-8") + 
				"&body=" + Uri.encode(Library.loadSettings(getApplicationContext()).getEmailMessage().replaceAll("@book@", editedItem.getName()));
				Uri uri = Uri.parse(uriText);
		        Intent myActivity2 = new Intent(Intent.ACTION_SENDTO);                              
		        myActivity2.setData(uri);
		        startActivity(Intent.createChooser(myActivity2, getString(R.string.sendEmail)));
			}
		});
	    
	    btnAddBook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String bookname = etbookname.getText().toString();
				String bookAuthor = etBookAuthor.getText().toString();
				String lendedTo = etLendedTo.getText().toString();
				String email = etEmail.getText().toString();
				if(!(bookname.length() == 0))
				{
					GregorianCalendar dateLendedGc = new GregorianCalendar(dpDateLended.getYear(), dpDateLended.getMonth(), dpDateLended.getDayOfMonth());
					long dateLended = dateLendedGc.getTimeInMillis()/1000;
					items.set(editPos, new Book(bookname, bookAuthor ,lendedTo, email, dateLended));
					
					Library.saveInfo(items);
					
					Intent main = new Intent(getApplicationContext(), MainActivity.class);
					Bundle b = new Bundle();
					b.putParcelableArrayList("items", (ArrayList<? extends Parcelable>) items);
					main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					main.putExtras(b);
					startActivity(main);
					finish();
				}
				else
				{
					Toast.makeText(getApplicationContext(), getString(R.string.requiredBookNameError), Toast.LENGTH_SHORT).show();
				}
			}
				
		});
	    
	    etLendedTo.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				 
				String selectedName = (String) parent.getItemAtPosition(position);
				String currentName;
				for(Entry<Integer, String> row : nameIdMap.entrySet())
				{
					currentName = row.getValue();
					if(selectedName != null)
					{
						if(selectedName.equals(currentName))
						{
							contactEmailLoader = new GetContactEmail(getContentResolver(), contactEmailListener);
							executeEmailLoader(row.getKey());
						}
					}
				}
			}
		});
	    
	    
	}
	
	/**
	 * Start contact search.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void startContactSearch()
	{
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			contactNameLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		else
		{
			contactNameLoader.execute();
		}
	}
	
	/**
	 * Execute email loader.
	 *
	 * @param id the id
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void executeEmailLoader(int id)
	{
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			contactEmailLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id);
		}
		else
		{
			contactEmailLoader.execute(id);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see com.perlib.wmbg.interfaces.OnContactLoadingComplete#OnNameLoadingFinished(java.util.HashMap)
	 */
	@Override
	public void OnNameLoadingFinished(HashMap<Integer, String> result) {
		Log.i("autocompletecontacts", "Finished loading contacts");
		nameIdMap = result;
		for(Entry<Integer, String> row : nameIdMap.entrySet())
		{
			if(row.getValue().length() != 0)
			{
				adapter.add(row.getValue());
			}
		}
	    adapter.notifyDataSetChanged();
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onDestroy()
	 */
	@Override
	public void onDestroy()
	{
		contactNameLoader.cancel(true);
		if(contactEmailLoader != null)
		{
			contactEmailLoader.cancel(true);
		}
		super.onDestroy();
	}
	
	/* (non-Javadoc)
	 * @see com.perlib.wmbg.interfaces.OnEmailLoadingListener#OnEmailLoadingCompleted(java.lang.String)
	 */
	@Override
	public void OnEmailLoadingCompleted(String email) {
		
		etEmail.setText(email);
	}

}
