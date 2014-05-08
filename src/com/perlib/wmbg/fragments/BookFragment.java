package com.perlib.wmbg.fragments;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.perlib.wmbg.R;
import com.perlib.wmbg.asynctasks.GetContactEmailTask;
import com.perlib.wmbg.asynctasks.GetContactNamesTask;
import com.perlib.wmbg.book.Book;
import com.perlib.wmbg.interfaces.BookContainerActivity;
import com.perlib.wmbg.interfaces.OnContactLoadingComplete;
import com.perlib.wmbg.interfaces.OnEmailLoadingListener;
import com.squareup.picasso.Picasso;

public class BookFragment extends Fragment implements OnContactLoadingComplete, OnEmailLoadingListener {

	private EditText etBookName;
	private EditText etBookAuthor;
	private AutoCompleteTextView etBookLendedTo;
	private EditText etBookLendedToEmail;
	
	private ImageView ivBookThumbnail;
	private EditText etBookThumbnailUri;
	private Button btnTakePicture;
	
	private Book viewedBook;
	private ArrayAdapter<String> adapter;
	private HashMap<Integer, String> nameIdMap;
	private GetContactNamesTask getContactNames;
	private GetContactEmailTask getContactEmail;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_book, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		//View References
		ivBookThumbnail = (ImageView) findViewById(R.id.image);
		etBookLendedToEmail = (EditText) findViewById(R.id.etEmail);
		etBookLendedTo = (AutoCompleteTextView) findViewById(R.id.etLendedTo);
		etBookName = (EditText) findViewById(R.id.etBookName);
		btnTakePicture = (Button) findViewById(R.id.btnTakePicture);
		etBookAuthor = (EditText) findViewById(R.id.etAuthorName);
		etBookThumbnailUri = (EditText) findViewById(R.id.etImageUrl);
		
		//Start loading contact names into LendedTo field
		getContactNames = new GetContactNamesTask(this, getApplicationContext().getContentResolver());
		startContactSearch();
		
		//Listeners
		etBookLendedTo.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				String selectedName = (String) parent.getItemAtPosition(position);
				String currentName;
				for(Entry<Integer, String> row : nameIdMap.entrySet())
				{
					currentName = row.getValue();
					if(selectedName != null)
					{
						if(selectedName.equals(currentName))
						{
							getContactEmail = new GetContactEmailTask(getApplicationContext().getContentResolver(), BookFragment.this);
							executeEmailLoader(row.getKey());
						}
					}
				}
				
			}
		});
		
		etBookThumbnailUri.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String text = etBookThumbnailUri.getText().toString();
				if(text == null || text.equals(""))return;
				File file = new File(text);
				if(file.exists())
				{
					Picasso.with(getApplicationContext()).load(file).into(ivBookThumbnail);
				}
				else
				Picasso.with(getApplicationContext()).load(text).into(ivBookThumbnail);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
	    btnTakePicture.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if(takePicture.resolveActivity(getApplicationContext().getPackageManager()) != null)
				{
					startActivityForResult(takePicture, 0);
				}
				
			}
		});
	    
	    //Set the adapter for lendedTo autocomplete
	    etBookLendedTo.setAdapter(adapter);
	    
	    
	    //Get the book from the parent activity
	    setViewedBook(((BookContainerActivity)getActivity()).getBook());
	}
	

	protected Context getApplicationContext() {
		return getActivity().getApplicationContext();
	}

	private View findViewById(int id) {
		return getView().findViewById(id);
	}

	public Book getViewedBook() {
		viewedBook.setName(etBookName.getText().toString());
		viewedBook.setAuthor(etBookAuthor.getText().toString());
		viewedBook.setLendedTo(etBookLendedTo.getText().toString());
		viewedBook.setEmail(etBookLendedToEmail.getText().toString());
		viewedBook.setThumbnailUrl(etBookThumbnailUri.getText().toString());
		return viewedBook;
	}

	public void setViewedBook(Book viewedBook) {
		this.viewedBook = viewedBook;
		
		etBookName.setText(viewedBook.getName());
		etBookAuthor.setText(viewedBook.getAuthor());
		etBookLendedTo.setText(viewedBook.getLendedTo());
		etBookLendedToEmail.setText(viewedBook.getEmail());
		etBookThumbnailUri.setText(viewedBook.getThumbnailUrl());
	}
	
	@SuppressLint("SimpleDateFormat")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(resultCode == Activity.RESULT_OK)
		{
			Bitmap imageBitmap = (Bitmap) intent.getExtras().get("data");
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		    String imageFileName = "JPEG_" + timeStamp + "_";
		    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		    File image = new File(storageDir, imageFileName+".jpg");
		    try {
				FileOutputStream out = new FileOutputStream(image);
				imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			} catch (Exception e) {
				e.printStackTrace();
			}
		    
		    etBookThumbnailUri.setText(image.getAbsolutePath());
		}
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void startContactSearch()
	{
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			getContactNames = (GetContactNamesTask) new GetContactNamesTask(this, getApplicationContext().getContentResolver()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		else
		{
			getContactNames = (GetContactNamesTask) new GetContactNamesTask(this, getApplicationContext().getContentResolver()).execute();
		}
	}
	
	@Override
	public void OnNameLoadingFinished(HashMap<Integer, String> result) {
		nameIdMap = result;
		for(Entry<Integer, String> row : nameIdMap.entrySet())
		{
			if(row.getValue().length() != 0)
			{
				adapter.add(row.getValue());
			}
		}
	    adapter.notifyDataSetChanged();
	}


	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void executeEmailLoader(int id)
	{
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			getContactEmail.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id);
		}
		else
		{
			getContactEmail.execute(id);
		}
	}
	
	
	@Override
	public void OnEmailLoadingCompleted(String email) {
		etBookLendedToEmail.setText(email);
	}
	
	
	@Override
	public void onDestroy()
	{
		getContactNames.cancel(true);
		if(getContactEmail != null)
		{
			getContactEmail.cancel(true);
		}
		super.onDestroy();
	}
}
