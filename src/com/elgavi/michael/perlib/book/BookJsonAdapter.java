package com.elgavi.michael.perlib.book;

public class BookJsonAdapter {
	JsonBook[] data = new JsonBook[]{};
	String indexSearched;
	String error;
	
	public Book convertToBook()
	{
		Book convertedBook = new Book();
		if(data.length < 1)
		{
			return null;
		}
		else if(data[0].getAuthor_data().length < 1)
		{
			convertedBook.setName(data[0].getTitle());
		}
		else
		{
			convertedBook.setName(data[0].getTitle());
			convertedBook.setAuthor(data[0].getAuthor_data()[0].getName());
		}
		return convertedBook;
	}
}
