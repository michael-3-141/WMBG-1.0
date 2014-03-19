package com.perlib.wmbg.custom;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.perlib.wmbg.R;
import com.perlib.wmbg.book.Book;

public class BookAdapter extends SimpleAdapter {
	public BookAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to, List<Book> items) {
		super(context, data, resource, from, to);
		this.items = items;
		this.cx = context;
	}

	List<Book> items = new ArrayList<Book>();
	private int[] colors = new int[] { 0x30ffffff, 0x30808080 };
	Context cx;
	
	public BookAdapter(List<Book> items, Context cx) {
		super(cx, new ArrayList<Map<String, String>>(items.size()), R.layout.simple_list_item_3, new String[]{"name", "author", "lendedto", "date"}, new int[]{R.id.text1, R.id.text2, R.id.text3, R.id.text4});
		this.items = items;
		this.cx = cx;
	}
	
	public static BookAdapter CreateBookAdapter(List<Book> items, Context cx)
	{
		List<Map<String, String>> itemsMap = new ArrayList<Map<String, String>>();
		
		for(Iterator<Book> i = items.iterator() ; i.hasNext();)
		{
			Book item = i.next();
			Map<String, String> row = new HashMap<String, String>();
			row.put("name", item.getName());
			row.put("author", cx.getString(R.string.by) + item.getAuthor());
			
			String display = cx.getString(R.string.lendedToDisplay);
			if(item.isLended())
			{
				display += item.getAuthor();
			}
			else
			{
				display += cx.getString(R.string.none);
			}
			row.put("lendedto", display);
			
			if(item.isLended())
			{
				GregorianCalendar gcDateLended = new GregorianCalendar();
				gcDateLended.setTimeInMillis(item.getDateLended()*1000);
				SimpleDateFormat format = new SimpleDateFormat("d/M/y", Locale.US);
				row.put("date", cx.getString(R.string.dateLendedDisplay) + format.format(gcDateLended.getTime()));
			}
			else
			{
				row.put("date", "");
			}
			
			itemsMap.add(row);
		}
		
		return new BookAdapter(cx, itemsMap, R.layout.simple_list_item_3, new String[]{"name", "author", "lendedto", "date"}, new int[]{R.id.text1, R.id.text2, R.id.text3, R.id.text4}, items);
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
