package com.perlib.wmbg.book;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {
	
	private String name;
	private String author;
	private String lendedTo;
	private String email;
	private long dateLended;
	
	public Book(String name, String author, String lendedTo, String email, long dateLended)
	{
		this.name = name;
		this.author = author;
		this.lendedTo = lendedTo;
		this.email = email;
		this.dateLended = dateLended;
	}
	
	public Book()
	{
		this.name = "";
		this.author = "";
		this.lendedTo = "";
		this.email = "";
	}
	
	private Book(Parcel dest)
	{
		this.name = dest.readString();
		//dest.readStringArray(this.author);
		this.author = dest.readString();
		this.lendedTo = dest.readString();
		this.email = dest.readString();
		this.dateLended = dest.readLong();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	

	public String getLendedTo() {
		return lendedTo;
	}

	public void setLendedTo(String lendedTo) {
		this.lendedTo = lendedTo;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
		dest.writeString(this.name);
		dest.writeString(this.author);
		dest.writeString(this.lendedTo);
		dest.writeString(this.email);
		dest.writeLong(this.dateLended);
		
	}
	
	public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>()
	{
		public Book createFromParcel(Parcel in)
		{
			return new Book(in);
		}
		
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getDateLended() {
		return dateLended;
	}

	public void setDateLended(long dateLended) {
		this.dateLended = dateLended;
	}
	
	public boolean isLended()
	{
		return this.lendedTo.length() > 0;
	}
}
