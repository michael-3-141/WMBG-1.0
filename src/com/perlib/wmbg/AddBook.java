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

public class AddBook extends Activity implements OnDownloadComplete, OnContactLoadingComplete {

	List<Book> items = new ArrayList<Book>();
	OnDownloadComplete downloadListener;
	OnContactLoadingComplete contactsListener;
    public ArrayList<String> nameValueArr = new ArrayList<String>();
    @SuppressLint("UseSparseArrays")
	public Map<Integer,List<String>> contacts = new HashMap<Integer, List<String>>();
	DownloadInfo downloader;
	GetContacts contactLoader;
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
	Object[] contactResults;
	
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
	    downloadListener = this;
	    contactsListener = this;
	    final IntentIntegrator scanIntegrator = new IntentIntegrator(this);
	    contactLoader = new GetContacts(contactsListener);
	    startContactSearch();
	    
	    btnAddBook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String bookname = etbookname.getText().toString();
				String bookAuthor = etBookAuthor.getText().toString();
				String lendedTo = etLendedTo.getText().toString();
				String email = etEmail.getText().toString();
				GregorianCalendar lendedDateGc = new GregorianCalendar();
				long lendedDate = lendedDateGc.getTimeInMillis()/1000;
				GregorianCalendar dueDateGc = new GregorianCalendar(dpDueDate.getYear(), dpDueDate.getMonth(), dpDueDate.getDayOfMonth());
				long dueDate = dueDateGc.getTimeInMillis()/1000;
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
				
				String name = etLendedTo.getText().toString();
				String selectedName = "";
				for(Entry<Integer, List<String>> row : contacts.entrySet())
				{
					selectedName = row.getValue().get(0);
					if(selectedName != null)
					{
						if(selectedName.equals(name))
						{
							etEmail.setText(row.getValue().get(1));
							break;
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
			contactLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getContentResolver());
		}
		else
		{
			contactLoader.execute(getContentResolver());
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


	@SuppressWarnings("unchecked")
	@Override
	public void OnLoadingFinished(Object[] contactsArray) {
		Log.i("autocompletecontacts", "Finished loading contacts");
	    nameValueArr = (ArrayList<String>)contactsArray[0];
	    contacts = (Map<Integer, List<String>>)contactsArray[1];
	    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, nameValueArr);
	    etLendedTo.setAdapter(adapter);
	    adapter.notifyDataSetChanged();
	}
	
	
	@Override
	public void onDestroy()
	{
		contactLoader.cancel(true);
		if(downloader != null)
		{
			downloader.cancel(true);
		}
		super.onDestroy();
	}

}
