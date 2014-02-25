package com.elgavi.michael.perlib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elgavi.michael.perlib.book.Book;
import com.elgavi.michael.perlib.book.BookJsonAdapter;
import com.elgavi.michael.perlib.book.Library;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class AddBook extends Activity implements OnDownloadComplete {

	List<Book> items = new ArrayList<Book>();
	OnDownloadComplete activity;
    public ArrayList<String> nameValueArr = new ArrayList<String>();
    @SuppressLint("UseSparseArrays")
	public Map<Integer,List<String>> contacts = new HashMap<Integer, List<String>>();
	DownloadInfo downloader;
    EditText etbookname;
    EditText etBookAuthor;
    AutoCompleteTextView etLendedTo;
    Button btnAddBook;
    EditText etISBN;
    Button btnDownloadInfo;
    Button btnScanISBN;
    EditText etEmail;
	private ArrayAdapter<String> adapter;
	
	/** Called when the activity is first created. */
	@SuppressWarnings("unchecked")
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
	    activity = this;
	    final IntentIntegrator scanIntegrator = new IntentIntegrator(this);
	    nameValueArr = (ArrayList<String>) Library.readContactData(Library.MODE_GET_NAMES, getBaseContext());
	    contacts = (Map<Integer, List<String>>) Library.readContactData(Library.MODE_GET_ALL, getBaseContext());
	    
	    //String[] contacts = getContacts();
	    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, nameValueArr);
	    etLendedTo.setAdapter(adapter);
	    
	    
	    btnAddBook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String bookname = etbookname.getText().toString();
				String bookAuthor = etBookAuthor.getText().toString();
				String lendedTo = etLendedTo.getText().toString();
				String email = etEmail.getText().toString();
				/*String[] splitBookAuthor = bookAuthor.split("\\s+");
				if(!(splitBookAuthor.length == 2 && splitBookAuthor[0] != null && splitBookAuthor[1] != null))
				{
					Toast.makeText(getApplicationContext(), "Please specify a valid author name. (First and Last name)", Toast.LENGTH_SHORT).show();
					return;
				}
				items.set(editPos, new Book(bookname,splitBookAuthor[0],splitBookAuthor[1],lendedTo));*/
				items.add(new Book(bookname, bookAuthor ,lendedTo, email));
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
					if(selectedName.equals(name))
					{
						etEmail.setText(row.getValue().get(1));
						break;
					}
				}
			}
		});
	   
	    
	}


	@Override
	public void OnTaskFinished() {
		
		//Toast.makeText(getApplicationContext(), downloader.getJsonResult(), Toast.LENGTH_SHORT).show();
		Gson gson = new Gson();
		BookJsonAdapter adapter = gson.fromJson(downloader.getJsonResult(), BookJsonAdapter.class);
		Book resultBook = adapter.convertToBook();
		if(resultBook == null)
		{
			Toast.makeText(getApplicationContext(), getString(R.string.InvalidISBN) , Toast.LENGTH_SHORT).show();
			return;
		}
		etbookname.setText(resultBook.getName());
		etBookAuthor.setText(resultBook.getAuthor());
		
	}
	
	 public boolean isConnectedToInternet(){
		    ConnectivityManager connectivity = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		      if (connectivity != null) 
		      {
		          NetworkInfo[] info = connectivity.getAllNetworkInfo();
		          if (info != null) 
		              for (int i = 0; i < info.length; i++) 
		                  if (info[i].getState() == NetworkInfo.State.CONNECTED)
		                  {
		                      return true;
		                  }

		      }
		      return false;
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
	
	private void handleISBN(String isbn)
	{
		if(isbn.length() == 0){Toast.makeText(getApplicationContext(), getString(R.string.InvalidISBN) , Toast.LENGTH_SHORT).show();return;}
		if(!isConnectedToInternet()){Toast.makeText(getApplicationContext(), getString(R.string.noConnection) , Toast.LENGTH_SHORT).show();return;}
		downloader = new DownloadInfo(activity);
		downloader.execute(isbn);
	}
	
	

}
