package com.perlib.wmbg.activities;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.perlib.wmbg.R;
import com.perlib.wmbg.asynctasks.DownloadBookInfoTask;
import com.perlib.wmbg.book.Book;
import com.perlib.wmbg.fragments.BookFragment;
import com.perlib.wmbg.interfaces.OnDownloadComplete;
import com.perlib.wmbg.misc.Library;

/**
 * Add book activity. Used from scan book and for manual mode.
 */
public class AddBook extends ActionBarActivity implements OnDownloadComplete {

	private List<Book> items = new ArrayList<Book>();
	private DownloadBookInfoTask downloader;
    
    private EditText etISBN;
    private Button btnDownloadInfo;
    private BookFragment fmtBook;
	private Button btnAddBook;
	
    //Modes for the activity. Auto doesn't show the manual isbn option while manual does.
	private int mode;
	
	public static final int MODE_AUTO = 0;
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
	    
		//Create view references
	    btnAddBook = (Button)findViewById(R.id.btnAddBook);
	    etISBN = (EditText)findViewById(R.id.etISBN);
	    fmtBook = (BookFragment) getSupportFragmentManager().findFragmentById(R.id.bookFragment);
	    btnDownloadInfo = (Button)findViewById(R.id.btnDownloadInfo);
	    
	    //Get mode and apply changes
	    Bundle b = getIntent().getExtras();
	    items = b.getParcelableArrayList("items");
	    mode = b.getInt("mode");
	    Book book = b.getParcelable("book");
	    if(mode == MODE_AUTO)
	    {
	    	fmtBook.setViewedBook(book);
	    	etISBN.setVisibility(View.GONE);
	    	btnDownloadInfo.setVisibility(View.GONE);
	    }
	    
	    //Listeners
	    btnAddBook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Book book = fmtBook.getViewedBook();
				if(!(book.getName().length() == 0))
				{
					GregorianCalendar dateLendedGc = new GregorianCalendar();
					book.setDateLended(dateLendedGc.getTimeInMillis()/1000);
					items.add(book);
					
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
	    
	}

	//On book download finished.
	@Override
	public void OnTaskFinished(Book result) {
		
		if(result == null)
		{
			Toast.makeText(getApplicationContext(), getString(R.string.InvalidISBN) , Toast.LENGTH_SHORT).show();
			return;
		}
		fmtBook.setViewedBook(result);
		
	}
	
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void handleISBN(String isbn)
	{
		if(isbn.length() == 0){Toast.makeText(getApplicationContext(), getString(R.string.InvalidISBN) , Toast.LENGTH_SHORT).show();return;}
		if(!Library.isConnectedToInternet(getApplicationContext())){Toast.makeText(getApplicationContext(), getString(R.string.noConnection) , Toast.LENGTH_SHORT).show();return;}
		downloader = new DownloadBookInfoTask(this);
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
	public void onDestroy()
	{
		if(downloader != null)
		{
			downloader.cancel(true);
		}
		super.onDestroy();
	}

}
