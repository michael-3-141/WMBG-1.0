package com.elgavi.michael.perlib.book;

public class Author {
	public Author(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	private String id;
	private String name;
	
	public Author() {
		id = "";
		name = "";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
