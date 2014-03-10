package com.perlib.wmbg;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.perlib.wmbg.R;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.perlib.wmbg.book.Book;
import com.perlib.wmbg.book.BookJsonAdapter;
import com.perlib.wmbg.book.Library;

public class AddBook extends Activity implements OnDownloadComplete, OnContactLoadingComplete, OnEmailLoadingListener {

	List<Book> items = new ArrayList<Book>();
	OnDownloadComplete downloadListener = this;
	OnContactLoadingComplete contactsListener = this;
	OnEmailLoadingListener contactEmailListener = this;
    public ArrayList<String> nameValueArr = new ArrayList<String>();
    @SuppressLint("UseSparseArrays")
	DownloadInfo downloader;
	GetContactNames contactNameLoader;
	GetContactEmail contactEmailLoader;
    EditText etbookname;
    EditText etBookAuthor;
    AutoCompleteTextView etLendedTo;
    Button btnAddBook;
    EditText etISBN;
    Button btnDownloadInfo;
    Button btnScanISBN;
    EditText etEmail;
    DatePicker dpDueDate;
	private ArrayAdapter<String> adapter;
	Map<Integer, String> nameIdMap;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_addbook);
	    
	    Bundle b = getIntent().getExtras();
	    items = b.getParcelableArrayList("items");
	    
	    etbookname = (EditText)findViewById(R.id.etBookName);
	    etBookAuthor = (EditText)findViewById(R.id.etAuthorName);
	    etLendedTo = (AutoCompleteTextView)findViewById(R.id.etLendedTo);
	    btnAddBook = (Button)findViewById(R.id.btnAddBook);
	    etISBN = (EditText)findViewById(R.id.etISBN);
	    btnDownloadInfo = (Button)findViewById(R.id.btnDownloadInfo);
	    btnScanISBN = (Button)findViewById(R.id.btnScanISBN);
	    etEmail = (EditText)findViewById(R.id.etEmail);
	    dpDueDate = (DatePicker)findViewById(R.id.dpDueDate);
	    
	    final IntentIntegrator scanIntegrator = new IntentIntegrator(this);
	    contactNameLoader = new GetContactNames(contactsListener, getContentResolver());
	    startContactSearch();
	   
	    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, nameValueArr);
	    etLendedTo.setAdapter(adapter);
	    
	    btnAddBook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String bookname = etbookname.getText().toString();
				String bookAuthor = etBookAuthor.getText().toString();
				String lendedTo = etLendedTo.getText().toString();
				String email = etEmail.getText().toString();
				if(!(bookname.length() == 0))
				{
					long lendedDate;
					long dueDate;
					if(lendedTo.length() == 0)
					{
						lendedDate = -1;
						dueDate = -1;
					}
					else
					{
						GregorianCalendar lendedDateGc = new GregorianCalendar();
						lendedDate = lendedDateGc.getTimeInMillis()/1000;
						GregorianCalendar dueDateGc = new GregorianCalendar(dpDueDate.getYear(), dpDueDate.getMonth(), dpDueDate.getDayOfMonth());
						dueDate = dueDateGc.getTimeInMillis()/1000;
					}
					items.add(new Book(bookname, bookAuthor ,lendedTo, email, lendedDate, dueDate));
					btnScanISBN.setOnClickListener(this);
					
					Library.saveInfo(items);
					
					Intent main = new Intent(getApplicationContext(), MainActivity.class);
					Bundle b = new Bundle();
					b.putParcelableArrayList("items", (ArrayList<? extends Parcelable>) items);
					
					main.putExtras(b);
					main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(main);
					finish();
				}
				else
				{
					Toast.makeText(getApplicationContext(), getString(R.string.requiredBookNameError), Toast.LENGTH_SHORT).show();
				}
			}
		});
	    
	    
	    btnScanISBN.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				scanIntegrator.initiateScan();
				
			}
		});
	    
	    btnDownloadInfo.setOnClickListener(new OnClickListener() {
			
	    	
			@Override
			public void onClick(View v) {
				
					handleISBN(etISBN.getText().toString());
				
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

	@Override
	public void OnTaskFinished() {
		
		//Toast.makeText(getApplicationContext(), downloader.getJsonResult(), Toast.LENGTH_SHORT).show();
		Gson gson = new Gson();
		BookJsonAdapter adapter = gson.fromJson(downloader.getJsonResult(), BookJsonAdapter.class);
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
		etbookname.setText(resultBook.getName());
		etBookAuthor.setText(resultBook.getAuthor());
		
	}
	
	 
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanningResult != null) {
			String contents = scanningResult.getContents();
			if(contents != null)
			{
				handleISBN(contents);
			}
		}
		else{
		    Toast toast = Toast.makeText(getApplicationContext(), 
		        "No scan data received!", Toast.LENGTH_SHORT);
		    toast.show();
		}
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void handleISBN(String isbn)
	{
		if(isbn.length() == 0){Toast.makeText(getApplicationContext(), getString(R.string.InvalidISBN) , Toast.LENGTH_SHORT).show();return;}
		if(!Library.isConnectedToInternet(getApplicationContext())){Toast.makeText(getApplicationContext(), getString(R.string.noConnection) , Toast.LENGTH_SHORT).show();return;}
		downloader = new DownloadInfo(downloadListener);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			downloader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, isbn);
		}
		else
		{
			downloader.execute(isbn);
		}
	}


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
	
	
	@Override
	public void onDestroy()
	{
		contactNameLoader.cancel(true);
		if(contactEmailLoader != null)
		{
			contactEmailLoader.cancel(true);
		}
		if(downloader != null)
		{
			downloader.cancel(true);
		}
		super.onDestroy();
	}


	@Override
	public void OnEmailLoadingCompleted(String email) {
		
		etEmail.setText(email);
	}

}
