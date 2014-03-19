package com.perlib.wmbg.custom;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.perlib.wmbg.R;
import com.perlib.wmbg.book.Book;

public class BookAdapter extends ArrayAdapter<Book> {
	
	List<Book> items = new ArrayList<Book>();
	private int[] colors = new int[] { 0x30ffffff, 0x30808080 };
	Context cx;

	public BookAdapter(Context context, int resource, List<Book> objects) {
		super(context, resource, objects);
		items = objects;
		this.cx = context;
	}
	
	public BookAdapter(List<Book> items, Context cx) {
		super(cx, R.layout.simple_list_item_3, items);
		this.items = items;
		this.cx = cx;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	  View v = super.getView(position, convertView, parent);

	  int colorPos = position % colors.length;
	  v.setBackgroundColor(colors[colorPos]);
	  
	  Book item = items.get(position);
	  
	  TextView text1 = (TextView) v.findViewById(R.id.text1);
	  TextView text2 = (TextView) v.findViewById(R.id.text2);
	  TextView text3 = (TextView) v.findViewById(R.id.text3);
	  TextView text4 = (TextView) v.findViewById(R.id.text4);
	  
	  String displayAuthor = cx.getString(R.string.by) + item.getAuthor();
	  String displayLendedTo = cx.getString(R.string.lendedToDisplay);
	  String displayDateLended = "";
	  
	  if(item.isLended())
	  {
		  displayLendedTo += item.getLendedTo();
	  }
	  else
	  {
		  displayLendedTo += cx.getString(R.string.none);
	  }

	  if(item.isLended())
	  {
		  GregorianCalendar gcDateLended = new GregorianCalendar();
		  gcDateLended.setTimeInMillis(item.getDateLended()*1000);
		  SimpleDateFormat format = new SimpleDateFormat("d/M/y", Locale.US);
		  displayDateLended = cx.getString(R.string.dateLendedDisplay) + format.format(gcDateLended.getTime());
	  }

	  text1.setText(item.getName());
	  text2.setText(displayAuthor);
	  text3.setText(displayLendedTo);
	  text4.setText(displayDateLended);

	  return v;
	}

	public List<Book> getItems() {
		return items;
	}

	public void setItems(List<Book> items) {
		this.items = items;
	}
	
	
	
}
