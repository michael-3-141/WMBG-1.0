package com.elgavi.michael.perlib.book;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import com.google.gson.Gson;

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
				Gson gson = new Gson();
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
	
	@SuppressLint("UseSparseArrays")
	public static Object readContactData(int mode, Context cx) {
		
        
        try {
             
        	
        	List<String> names = new ArrayList<String>();
        	Map<Integer, String> emails = new HashMap<Integer, String>();
        	Map <Integer, List<String>> contacts = new HashMap<Integer, List<String>>();
            /*********** Reading Contacts Name **********/
             
            ContentResolver cr = cx.getContentResolver();
             
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
                    while(cur1.moveToNext())
                    {
                    	email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    	if(email != null)
                    	{
							emails.put(Integer.parseInt(id), email);
                    	}
                    }
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
            
            if(mode == MODE_GET_NAMES)
            {
            	return names;
            }
            else if(mode == MODE_GET_EMAILS)
            {
            	return emails;
            }
            else if(mode == MODE_GET_ALL)
            {
            	return contacts;
            }
                     
        } catch (NullPointerException e) {
            Log.i("AutocompleteContacts","Exception : "+ e);
        }
        return null;
   }
}
