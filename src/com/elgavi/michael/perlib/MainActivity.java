package com.elgavi.michael.perlib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.elgavi.michael.perlib.book.Book;
import com.elgavi.michael.perlib.book.Library;
import com.google.gson.Gson;

public class MainActivity extends Activity {

	//Book[] items = new Book[]{};
	List<Book> items = new ArrayList<Book>(); 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final ListView bookList = (ListView)findViewById(R.id.bookList);
		
		loadData();
		
		if(getIntent().getExtras() != null)
		{
			Bundle b = getIntent().getExtras();
			items = b.getParcelableArrayList("items");
		}
		
		refreshList(bookList);
		
		bookList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, final View view,
					final int position, long id) {
				
				AlertDialog.Builder options_builder = new AlertDialog.Builder(MainActivity.this);
				options_builder.setTitle(getString(R.string.options)).setItems(getResources().getStringArray(R.array.options_array), new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						switch(which){
						case 0:
							deleteItem(position, bookList);
							break;
						case 1:
							String uriText = null;
							uriText =
							"mailto:"+items.get(position).getEmail() + 
							"?subject=" + Uri.encode(getString(R.string.emailSubject), "UTF-8") + 
							"&body=" + Uri.encode(getString(R.string.emailBodyA) + items.get(position).getName() + getString(R.string.emailBodyB), "UTF-8");
							Uri uri = Uri.parse(uriText);
					        Intent myActivity2 = new Intent(Intent.ACTION_SENDTO);                              
					        myActivity2.setData(uri);
					        startActivity(Intent.createChooser(myActivity2, "Send Email"));
							break;
						default:
							break;
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.addbook:
				goto_newbook();
				return true;
			default:
				return false;
		
		}
	}
	

	private void goto_newbook() {
		
		Intent addbook = new Intent(getApplicationContext(), AddBook.class);
		Bundle b = new Bundle();
		b.putParcelableArrayList("items", (ArrayList<? extends Parcelable>) items);
		addbook.putExtras(b);
		startActivity(addbook);
	}
	
	private void goto_editbook(int position) {
		
		Intent addbook = new Intent(getApplicationContext(), EditBook.class);
		Bundle b = new Bundle();
		b.putParcelableArrayList("items", (ArrayList<? extends Parcelable>) items);
		b.putInt("position", position);
		addbook.putExtras(b);
		startActivity(addbook);
	}
	
	private void loadData()
	{
		//String eol = System.getProperty("line.separator");
		String fs = System.getProperty("file.separator");
		File sd = Environment.getExternalStorageDirectory();
		File listfile = new File(sd+fs+"booklist.txt");
		
		if(listfile.exists())
		{
			try {
				BufferedReader br = new BufferedReader(new FileReader(listfile));
				
				String line;
				List<Book> list = new ArrayList<Book>();
				Gson gson = new Gson();
				Book[] bookArray = new Book[]{};
				while((line = br.readLine()) != null)
				{
					bookArray = gson.fromJson(line, Book[].class);
				}
				list = new ArrayList<Book>(Arrays.asList(bookArray));
				items = list;
				br.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void refreshList(ListView bookList)
	{
		List<Map<String,String>> displayList = new ArrayList<Map<String,String>>();
		for(Iterator<Book> i = items.iterator(); i.hasNext(); ) {
		    Book item = i.next();
		    Map<String,String> stringMap = new HashMap<String,String>();
		    stringMap.put("name", item.getName());
		    stringMap.put("lendedto", getString(R.string.lendedToDisplay) + item.getLendedTo());
		    stringMap.put("Author", getString(R.string.by) + item.getAuthor());
		    displayList.add(stringMap);
		}
		//itemsArray = displayList.toArray(itemsArray);
		SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), displayList, R.layout.simple_list_item_3, new String[] {"name", "Author", "lendedto"}, new int[] {R.id.text1,R.id.text2,R.id.text3});
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, itemsArray);
		
		bookList.setAdapter(adapter);
	}

	private void deleteItem(final int position, final ListView bookList)
	{
		AlertDialog.Builder delete_builder = new AlertDialog.Builder(MainActivity.this);
		delete_builder.setPositiveButton(getString(R.string.deleteYes) , new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				items.remove(position);
				refreshList(bookList);
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
}
