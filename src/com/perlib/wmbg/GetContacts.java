package com.perlib.wmbg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

public class GetContacts extends AsyncTask<ContentResolver, String, Object[]> {

	private Object[] results;
	private OnContactLoadingComplete listener;
	
	public GetContacts(OnContactLoadingComplete listener) {
		super();
        results = new Object[3];
		this.listener = listener;
	}
	
	@SuppressLint("UseSparseArrays")
	@Override
protected Object[] doInBackground(ContentResolver...cr) {
	
	try{
    	List<String> names = new ArrayList<String>();
    	Map<Integer, String> emails = new HashMap<Integer, String>();
    	Map <Integer, List<String>> contacts = new HashMap<Integer, List<String>>();
        /*********** Reading Contacts Name **********/

         
        //Query to get contact name
         
        Cursor cur = cr[0]
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
                Cursor cur1 = cr[0].query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
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
	
	@Override
	protected void onPostExecute(Object[] result)
	{
		this.results = result;
		try{
			listener.OnLoadingFinished(result);
		}
		catch(NullPointerException e)
		{
			e.printStackTrace();
		}
	}

	public Object[] getJsonResult() {
		return results;
	}

}
