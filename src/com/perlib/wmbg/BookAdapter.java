package com.perlib.wmbg;

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

import com.perlib.wmbg.book.Book;

public class BookAdapter extends SimpleAdapter {

	List<Book> items = new ArrayList<Book>();
	private int[] colors = new int[] { 0x30ffffff, 0x30808080 };

	
	public BookAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
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
		
		return new BookAdapter(cx, itemsMap, R.layout.simple_list_item_3, new String[]{"name", "author", "lendedto", "date"}, new int[]{R.id.text1, R.id.text2, R.id.text3, R.id.text4});
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	  View view = super.getView(position, convertView, parent);

	  int colorPos = position % colors.length;
	  view.setBackgroundColor(colors[colorPos]);
	  return view;
	}
	
}
