package com.perlib.wmbg.custom;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.perlib.wmbg.R;
import com.perlib.wmbg.asyncTasks.DownloadInfo;
import com.perlib.wmbg.book.Book;
import com.perlib.wmbg.book.Settings;
import com.perlib.wmbg.interfaces.OnDownloadComplete;

public class Library {
	
	
	public static int MODE_GET_NAMES = 0;
	public static int MODE_GET_EMAILS = 1;
	public static int MODE_GET_ALL = 2;
	
	public static void saveInfo(List<Book> list)
	{
		String eol = System.getProperty("line.separator");
		File externalStorage = Environment.getExternalStorageDirectory();
		if(externalStorage.canWrite())
		{
			File bookList = new File(externalStorage,"booklist.txt");
			try {
				FileWriter fw = new FileWriter(bookList);
				BufferedWriter bw = new BufferedWriter(fw);
				Book[] bookArray = new Book[]{};
				bookArray = list.toArray(bookArray);
				Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
				String json = gson.toJson(bookArray);
				//Log.d("json", json);
				bw.write(json+eol);
				bw.close();
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void saveSettings(Settings settings)
	{
		String eol = System.getProperty("line.separator");
		File externalStorage = Environment.getExternalStorageDirectory();
		if(externalStorage.canWrite())
		{
			File bookList = new File(externalStorage,"settings.txt");
			try {
				FileWriter fw = new FileWriter(bookList);
				BufferedWriter bw = new BufferedWriter(fw);
				Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
				String json = gson.toJson(settings);
				//Log.d("json", json);
				bw.write(json+eol);
				bw.close();
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@SuppressLint("UseSparseArrays")
	public static Object[] readContactData(ContentResolver cr) {
        Object[] results = new Object[3];
		try{
        	List<String> names = new ArrayList<String>();
        	Map<Integer, String> emails = new HashMap<Integer, String>();
        	Map <Integer, List<String>> contacts = new HashMap<Integer, List<String>>();
            /*********** Reading Contacts Name **********/

             
            //Query to get contact name
             
            Cursor cur = cr
                    .query(ContactsContract.Contacts.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
            // If data data found in contacts 
            if (cur.getCount() > 0) {
                
            	
                Log.i("AutocompleteContacts", "Reading   contacts........");
                 
                String name = "";
                String id = "";
                 
                while (cur.moveToNext()) 
                {
                    name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String email = "";
                    if(name != null)
                    {
						names.add(name.toString());
                    }
                    Cursor cur1 = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                    if(cur1.getCount()>0)
                    {
	                    while(cur1.moveToNext())
	                    {
	                    	email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
	                    	if(email != null)
	                    	{
								emails.put(Integer.parseInt(id), email);
	                    	}
	                    }
                    }
                    cur1.close();
                    List<String> line = new ArrayList<String>();
                    line.add(name);
                    line.add(email);
                    contacts.put(Integer.parseInt(id), line);
                }  // End while loop
 
            } // End Cursor value check
            else
            {
            	Log.i("contacts", "No contacts found");
            }
            cur.close();
            
            results[0] = names;
            results[1] = contacts;
            results[2] = emails;
	    } catch (NullPointerException e) {
	        Log.i("AutocompleteContacts","Exception : "+ e);
	    }
        return results;
   }
	
	public static Settings loadSettings(Context cx)
	{
		//String eol = System.getProperty("line.separator");
		String fs = System.getProperty("file.separator");
		File sd = Environment.getExternalStorageDirectory();
		File listfile = new File(sd+fs+"settings.txt");
		Settings settings = new Settings(cx);
		if(listfile.exists())
		{
			try {
				BufferedReader br = new BufferedReader(new FileReader(listfile));
				
				String line;
				Gson gson = new Gson();
				while((line = br.readLine()) != null)
				{
					settings = gson.fromJson(line, Settings.class);
				}
				br.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return settings;
	}
	
	public static boolean isConnectedToInternet(Context cx){
	    ConnectivityManager connectivity = (ConnectivityManager)cx.getSystemService(Context.CONNECTIVITY_SERVICE);
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
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void handleISBN(String isbn, Context cx, OnDownloadComplete listener)
	{
		if(isbn.length() == 0){Toast.makeText(cx, cx.getString(R.string.InvalidISBN) , Toast.LENGTH_SHORT).show();return;}
		if(!Library.isConnectedToInternet(cx)){Toast.makeText(cx, cx.getString(R.string.noConnection) , Toast.LENGTH_SHORT).show();return;}
		DownloadInfo downloader = new DownloadInfo(listener);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			downloader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, isbn);
		}
		else
		{
			downloader.execute(isbn);
		}
	}
	
	public static void deleteItem(final int position, Settings settings, Context cx, DialogInterface.OnClickListener listener, List<Book> items)
	{
		if(settings.isConfirmDelete())
		{
			AlertDialog.Builder delete_builder = new AlertDialog.Builder(cx);
			delete_builder.setPositiveButton(cx.getString(R.string.deleteYes) , listener);
			delete_builder.setNegativeButton(cx.getString(R.string.deleteCancel), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					
				}
			});
			delete_builder.setMessage(cx.getString(R.string.deleteConfirm) + ' ' + '"' + items.get(position).getName() + '"' + "?").setTitle(cx.getString(R.string.deleteConfirmTitle));
			AlertDialog dialog = delete_builder.create();
			dialog.show();
		}
		else
		{
			listener.onClick(null, 0);
		}
	}
	
	public static List<Book> returnItem(final int position, List<Book> items)
	{
		Book item = items.get(position);
		item.setLendedTo("");
		item.setEmail("");
		item.setDateLended(-1);
		items.set(position, item);
		Library.saveInfo(items);
		return items;
	}
	
	public static void returnOrDeleteItem(final int position, final Context cx, final List<Book> items, final OnClickListener listener, final Settings settings)
	{
		AlertDialog.Builder delete_or_return_builder = new AlertDialog.Builder(cx);
		delete_or_return_builder.setPositiveButton(cx.getString(R.string.chooseReturn) , new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				returnItem(position, items);
			}
		});
		delete_or_return_builder.setNegativeButton(cx.getString(R.string.chooseDelete), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				deleteItem(position, settings, cx, listener, items);
			}
		});
		delete_or_return_builder.setMessage(cx.getString(R.string.returnOrDelete));
		AlertDialog dialog = delete_or_return_builder.create();
		dialog.show();
	}
	
	public static List<Book> loadData()
	{
		String fs = System.getProperty("file.separator");
		File sd = Environment.getExternalStorageDirectory();
		File listfile = new File(sd+fs+"booklist.txt");
		List<Book> list = new ArrayList<Book>();
		
		if(listfile.exists())
		{
			try {
				BufferedReader br = new BufferedReader(new FileReader(listfile));
				
				String line;
				Gson gson = new Gson();
				Book[] bookArray = new Book[]{};
				while((line = br.readLine()) != null)
				{
					bookArray = gson.fromJson(line, Book[].class);
				}
				list = new ArrayList<Book>(Arrays.asList(bookArray));
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
}
