package com.perlib.wmbg.activities;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.perlib.wmbg.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.perlib.wmbg.asyncTasks.DownloadBookInfo;
import com.perlib.wmbg.asyncTasks.GetContactEmail;
import com.perlib.wmbg.asyncTasks.GetContactNames;
import com.perlib.wmbg.book.Book;
import com.perlib.wmbg.custom.Library;
import com.perlib.wmbg.interfaces.OnContactLoadingComplete;
import com.perlib.wmbg.interfaces.OnDownloadComplete;
import com.perlib.wmbg.interfaces.OnEmailLoadingListener;
import com.squareup.picasso.Picasso;

/**
 * The Class AddBook.
 */
public class AddBook extends ActionBarActivity implements OnDownloadComplete, OnContactLoadingComplete, OnEmailLoadingListener {

	/** The items. */
	List<Book> items = new ArrayList<Book>();
	
	/** The download listener. */
	OnDownloadComplete downloadListener = this;
	
	/** The contacts listener. */
	OnContactLoadingComplete contactsListener = this;
	
	/** The contact email listener. */
	OnEmailLoadingListener contactEmailListener = this;
    
    /** The name value arr. */
    public ArrayList<String> nameValueArr = new ArrayList<String>();
    
    /** The downloader. */
    @SuppressLint("UseSparseArrays")
	DownloadBookInfo downloader;
	
	/** The contact name loader. */
	GetContactNames contactNameLoader;
	
	/** The contact email loader. */
	GetContactEmail contactEmailLoader;
    
    /** The etbookname. */
    EditText etbookname;
    
    /** The et book author. */
    EditText etBookAuthor;
    
    /** The et lended to. */
    AutoCompleteTextView etLendedTo;
    
    EditText etImageUrl;
    
    ImageView ivImage;
    
    /** The btn add book. */
    Button btnAddBook;
    
    /** The et isbn. */
    EditText etISBN;
    
    /** The btn download info. */
    Button btnDownloadInfo;
    
    /** The et email. */
    EditText etEmail;
	
	/** The adapter. */
	ArrayAdapter<String> adapter;
	
	/** The name id map. */
	Map<Integer, String> nameIdMap;
	
	/** The mode. */
	int mode;
	
	/** The Constant MODE_AUTO. */
	public static final int MODE_AUTO = 0;
	
	/** The Constant MODE_MANUAL. */
	public static final int MODE_MANUAL = 1;
	
	/**
	 *  Called when the activity is first created.
	 *
	 * @param savedInstanceState the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_addbook);
	    
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	    
	    etbookname = (EditText)findViewById(R.id.etBookName);
	    etBookAuthor = (EditText)findViewById(R.id.etAuthorName);
	    etLendedTo = (AutoCompleteTextView)findViewById(R.id.etLendedTo);
	    btnAddBook = (Button)findViewById(R.id.btnAddBook);
	    etISBN = (EditText)findViewById(R.id.etISBN);
	    btnDownloadInfo = (Button)findViewById(R.id.btnDownloadInfo);
	    etEmail = (EditText)findViewById(R.id.etEmail);
	    etImageUrl = (EditText) findViewById(R.id.etImageUrl);
	    ivImage = (ImageView) findViewById(R.id.image);
	    
	    Bundle b = getIntent().getExtras();
	    items = b.getParcelableArrayList("items");
	    mode = b.getInt("mode");
	    if(mode == MODE_AUTO)
	    {
	    	etbookname.setText(b.getString("name"));
	    	etBookAuthor.setText(b.getString("author"));
	    	etISBN.setVisibility(View.GONE);
	    	btnDownloadInfo.setVisibility(View.GONE);
	    }
	    
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
					GregorianCalendar dateLendedGc = new GregorianCalendar();
					long dateLended = dateLendedGc.getTimeInMillis()/1000;
					items.add(new Book(bookname, bookAuthor ,lendedTo, email, dateLended, etImageUrl.getText().toString()));
					
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
	    
	    etImageUrl.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void afterTextChanged(Editable s) {
				
				Picasso.with(getApplicationContext()).load(s.toString()).into(ivImage);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
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
	 * @see com.perlib.wmbg.interfaces.OnDownloadComplete#OnTaskFinished(java.lang.String)
	 */
	@Override
	public void OnTaskFinished(Book result) {
		
		if(result == null)
		{
			Toast.makeText(getApplicationContext(), getString(R.string.InvalidISBN) , Toast.LENGTH_SHORT).show();
			return;
		}
		etbookname.setText(result.getName());
		etBookAuthor.setText(result.getAuthor());
		etImageUrl.setText(result.getThumbnailUrl());
		
	}
	
	 
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int, android.content.Intent)
	 */
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
	
	/**
	 * Handle isbn.
	 *
	 * @param isbn the isbn
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void handleISBN(String isbn)
	{
		if(isbn.length() == 0){Toast.makeText(getApplicationContext(), getString(R.string.InvalidISBN) , Toast.LENGTH_SHORT).show();return;}
		if(!Library.isConnectedToInternet(getApplicationContext())){Toast.makeText(getApplicationContext(), getString(R.string.noConnection) , Toast.LENGTH_SHORT).show();return;}
		downloader = new DownloadBookInfo(downloadListener);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			downloader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, isbn);
		}
		else
		{
			downloader.execute(isbn);
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
		if(downloader != null)
		{
			downloader.cancel(true);
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
