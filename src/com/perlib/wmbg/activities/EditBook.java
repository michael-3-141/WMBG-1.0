package com.perlib.wmbg.activities;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.perlib.wmbg.R;
import com.perlib.wmbg.book.Book;
import com.perlib.wmbg.fragments.BookFragment;
import com.perlib.wmbg.interfaces.BookContainerActivity;
import com.perlib.wmbg.misc.Library;

/**
 * The EditBook activity.
 */
public class EditBook extends ActionBarActivity implements BookContainerActivity {

	private List<Book> items = new ArrayList<Book>();
	
	private int editPos;
	private Book editedItem;

	private DatePicker dpDateLended;
	private Button btnReturnBook;
	private Button btnSendReminder;
	private Button btnDelete;
	private Button btnEdit;
	private BookFragment fmtBook;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_editbook);
	    
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	    
	    Bundle b = getIntent().getExtras();
	    items = b.getParcelableArrayList("items");
	    editPos = b.getInt("position");
	    
	    //Create view references
	    fmtBook = (BookFragment) getSupportFragmentManager().findFragmentById(R.id.bookFragment);
	    dpDateLended = (DatePicker)findViewById(R.id.dpDateLended);
	    btnDelete = (Button) findViewById(R.id.btnDelete);
	    btnReturnBook = (Button) findViewById(R.id.btnReturnBook);
	    btnSendReminder = (Button) findViewById(R.id.btnSendReminder);
	    btnEdit = (Button)findViewById(R.id.btnEditBook);
	    
	    //Get currently edited item and fill in fields with its data
	    editedItem = items.get(editPos);
	    GregorianCalendar editedDate = new GregorianCalendar();
	    editedDate.setTimeInMillis(editedItem.getDateLended()*1000);
	    dpDateLended.updateDate(editedDate.get(GregorianCalendar.YEAR), editedDate.get(GregorianCalendar.MONTH), editedDate.get(GregorianCalendar.DAY_OF_MONTH));
	    
	    //Remove unnecessary buttons. 
	    if(!editedItem.isLended())
	    {
	    	btnReturnBook.setVisibility(View.GONE);
	    	btnSendReminder.setVisibility(View.GONE);
	    }
	    
	    //Listeners
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
	    
	    btnEdit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Book book = fmtBook.getViewedBook();
				if(!(book.getName().length() == 0))
				{
					GregorianCalendar dateLendedGc = new GregorianCalendar(dpDateLended.getYear(), dpDateLended.getMonth(), dpDateLended.getDayOfMonth());
					book.setDateLended(dateLendedGc.getTimeInMillis()/1000);
					items.set(editPos, book);
					
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
	}
	
	@Override
	public Book getBook() {
		return editedItem;
	}

}
