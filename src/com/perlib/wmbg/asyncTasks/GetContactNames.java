package com.perlib.wmbg.asyncTasks;

import java.util.HashMap;
import java.util.Map;

import com.perlib.wmbg.interfaces.OnContactLoadingComplete;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

public class GetContactNames extends AsyncTask<Void, String, HashMap<Integer, String>> {

	private OnContactLoadingComplete listener;
	ContentResolver cr;
	
	public GetContactNames(OnContactLoadingComplete listener, ContentResolver cr) {
		super();
		this.listener = listener;
		this.cr = cr;
	}
	
	@SuppressLint("UseSparseArrays")
	@Override
	protected HashMap<Integer, String> doInBackground(Void...nothing) {
		Map<Integer, String> names = new HashMap<Integer, String>();
		try{
	        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
	        if (cur.getCount() > 0) {
	            
	        	
	            Log.i("AutocompleteContacts", "Reading   contacts........");
	             
	            String name = "";
	            int id;
	             
	            while (cur.moveToNext()) 
	            {
	                name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
	                id = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts._ID));
	                names.put(id, name);
	            }
	 
	        }
	        else
	        {
	        	Log.i("contacts", "No contacts found");
	        }
	        cur.close();
	        
	    } catch (NullPointerException e) {
	        Log.i("AutocompleteContacts","Exception : "+ e);
	    }
	    return (HashMap<Integer, String>) names;
	}
	
	@Override
	protected void onPostExecute(HashMap<Integer, String> result)
	{
		try{
			listener.OnNameLoadingFinished(result);
		}
		catch(NullPointerException e)
		{
			e.printStackTrace();
		}
	}

}
