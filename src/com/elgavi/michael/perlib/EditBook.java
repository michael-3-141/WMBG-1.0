package com.elgavi.michael.perlib;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.elgavi.michael.perlib.book.Book;
import com.elgavi.michael.perlib.book.Library;

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
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;

public class EditBook extends Activity implements OnContactLoadingComplete {

	List<Book> items = new ArrayList<Book>();
	int editPos;
	Book editedItem;
	List<String> autoNames = new ArrayList<String>();
	AutoCompleteTextView etLendedTo;
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
	    editedItem = items.get(editPos);
	    etbookname.setText(editedItem.getName());
	    etBookAuthor.setText(editedItem.getAuthor());
	    etLendedTo.setText(editedItem.getLendedTo());
	    etEmail.setText(editedItem.getEmail());
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
				/*String[] splitBookAuthor = bookAuthor.split("\\s+");
				if(!(splitBookAuthor.length == 2 && splitBookAuthor[0] != null && splitBookAuthor[1] != null))
				{
					Toast.makeText(getApplicationContext(), "Please specify a valid author name. (First and Last name)", Toast.LENGTH_SHORT).show();
					return;
				}
				items.set(editPos, new Book(bookname,splitBookAuthor[0],splitBookAuthor[1],lendedTo));*/
				items.set(editPos, new Book(bookname, bookAuthor ,lendedTo, email));
				
				Library.saveInfo(items);
				
				Intent main = new Intent(getApplicationContext(), MainActivity.class);
				Bundle b = new Bundle();
				b.putParcelableArrayList("items", (ArrayList<? extends Parcelable>) items);
				main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				main.putExtras(b);
				startActivity(main);
				finish();
				
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
	}

}
