package com.perlib.wmbg;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.perlib.wmbg.R;
import com.perlib.wmbg.book.Book;
import com.perlib.wmbg.book.Library;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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

public class EditBook extends Activity implements OnContactLoadingComplete {

	List<Book> items = new ArrayList<Book>();
	int editPos;
	Book editedItem;
	List<String> autoNames = new ArrayList<String>();
	AutoCompleteTextView etLendedTo;
	DatePicker dpDueDate;
	private ArrayAdapter<String> adapter;
	private Map<Integer, List<String>> contacts;
	OnContactLoadingComplete contactsListener;
	GetContacts contactLoader;
	Object[] contactResults;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_editbook);
	    
	    
	    Bundle b = getIntent().getExtras();
	    items = b.getParcelableArrayList("items");
	    editPos = b.getInt("position");
	    final EditText etbookname = (EditText)findViewById(R.id.etBookName);
	    final EditText etBookAuthor = (EditText)findViewById(R.id.etAuthorName);
	    etLendedTo = (AutoCompleteTextView)findViewById(R.id.etLendedTo);
	    final Button btnAddBook = (Button)findViewById(R.id.btnAddBook);
	    final EditText etEmail = (EditText)findViewById(R.id.etEmail);
	    dpDueDate = (DatePicker)findViewById(R.id.dpDueDate);
	    editedItem = items.get(editPos);
	    etbookname.setText(editedItem.getName());
	    etBookAuthor.setText(editedItem.getAuthor());
	    etLendedTo.setText(editedItem.getLendedTo());
	    etEmail.setText(editedItem.getEmail());
	    GregorianCalendar editedDate = new GregorianCalendar();
	    editedDate.setTimeInMillis(editedItem.getDueDate()*1000);
	    dpDueDate.updateDate(editedDate.get(GregorianCalendar.YEAR), editedDate.get(GregorianCalendar.MONTH), editedDate.get(GregorianCalendar.DAY_OF_MONTH));
	    contactLoader = new GetContacts(contactsListener);
	    contactLoader.execute(getContentResolver());
	    
	    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, autoNames);
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
					if(lendedTo != editedItem.getLendedTo())
					{
						GregorianCalendar lendedDateGc = new GregorianCalendar();
						lendedDate = lendedDateGc.getTimeInMillis()/1000;
					}
					else
					{
						lendedDate = editedItem.getDateLended();
					}
					GregorianCalendar dueDateGc = new GregorianCalendar(dpDueDate.getYear(), dpDueDate.getMonth(), dpDueDate.getDayOfMonth());
					long dueDate = dueDateGc.getTimeInMillis()/1000;
					items.set(editPos, new Book(bookname, bookAuthor ,lendedTo, email, lendedDate, dueDate));
					
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
	@SuppressWarnings("unchecked")
	@Override
	public void OnLoadingFinished(Object[] contactsArray) {
	    autoNames = (ArrayList<String>)contactsArray[0];
	    contacts = (Map<Integer, List<String>>)contactsArray[1];
	    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, autoNames);
	    etLendedTo.setAdapter(adapter);
	}
	
	@Override
	public void onDestroy()
	{
		contactLoader.cancel(true);
		super.onDestroy();
	}

}
