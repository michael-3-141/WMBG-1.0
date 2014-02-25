package com.elgavi.michael.perlib.book;

public class JsonBook {
	
    public JsonBook(String awards_text, String lcc_number, String urls_text,
			String book_id, String publisher_name, String summary,
			String title, Author[] author_data, String isbn13, String isbn10,
			String language, String dewey_decimal,
			String physical_description_text, String[] subject_ids,
			String publisher_id, String dewey_normal, String title_long,
			String marc_enc_level, String title_latin, String publisher_text,
			String notes, String edition_info) {
		super();
		this.awards_text = awards_text;
		this.lcc_number = lcc_number;
		this.urls_text = urls_text;
		this.book_id = book_id;
		this.publisher_name = publisher_name;
		this.summary = summary;
		this.title = title;
		this.author_data = author_data;
		this.isbn13 = isbn13;
		this.isbn10 = isbn10;
		this.language = language;
		this.dewey_decimal = dewey_decimal;
		this.physical_description_text = physical_description_text;
		this.subject_ids = subject_ids;
		this.publisher_id = publisher_id;
		this.dewey_normal = dewey_normal;
		this.title_long = title_long;
		this.marc_enc_level = marc_enc_level;
		this.title_latin = title_latin;
		this.publisher_text = publisher_text;
		this.notes = notes;
		this.edition_info = edition_info;
	}

	public JsonBook() {
		super();
		this.awards_text = "";
		this.lcc_number = "";
		this.urls_text = "";
		this.book_id = "";
		this.publisher_name = "";
		this.summary = "";
		this.title = "";
		this.author_data = new Author[]{};
		this.isbn13 = "";
		this.isbn10 = "";
		this.language = "";
		this.dewey_decimal = "";
		this.physical_description_text = "";
		this.subject_ids = new String[]{};
		this.publisher_id = "";
		this.dewey_normal = "";
		this.title_long = "";
		this.marc_enc_level = "";
		this.title_latin = "";
		this.publisher_text = "";
		this.notes = "";
		this.edition_info = "";
	}
    
    

	private String awards_text;
	private String lcc_number;
	private String urls_text;
	private String book_id;
	private String publisher_name;
	private String summary;
	private String title;
	private Author[] author_data;
	private String isbn13;
	private String isbn10;
	private String language;
	private String dewey_decimal;
	private String physical_description_text;
	private String[] subject_ids;
	private String publisher_id;
	private String dewey_normal;
	private String title_long;
	private String marc_enc_level;
	private String title_latin;
	private String publisher_text;
	private String notes;
	private String edition_info;
	
	public String getAwards_text() {
		return awards_text;
	}

	public void setAwards_text(String awards_text) {
		this.awards_text = awards_text;
	}

	public String getLcc_number() {
		return lcc_number;
	}

	public void setLcc_number(String lcc_number) {
		this.lcc_number = lcc_number;
	}

	public String getUrls_text() {
		return urls_text;
	}

	public void setUrls_text(String urls_text) {
		this.urls_text = urls_text;
	}

	public String getBook_id() {
		return book_id;
	}

	public void setBook_id(String book_id) {
		this.book_id = book_id;
	}

	public String getPublisher_name() {
		return publisher_name;
	}

	public void setPublisher_name(String publisher_name) {
		this.publisher_name = publisher_name;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Author[] getAuthor_data() {
		return author_data;
	}

	public void setAuthor_data(Author[] author_data) {
		this.author_data = author_data;
	}

	public String getIsbn13() {
		return isbn13;
	}

	public void setIsbn13(String isbn13) {
		this.isbn13 = isbn13;
	}

	public String getIsbn10() {
		return isbn10;
	}

	public void setIsbn10(String isbn10) {
		this.isbn10 = isbn10;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getDewey_decimal() {
		return dewey_decimal;
	}

	public void setDewey_decimal(String dewey_decimal) {
		this.dewey_decimal = dewey_decimal;
	}

	public String getPhysical_description_text() {
		return physical_description_text;
	}

	public void setPhysical_description_text(String physical_description_text) {
		this.physical_description_text = physical_description_text;
	}

	public String[] getSubject_ids() {
		return subject_ids;
	}

	public void setSubject_ids(String[] subject_ids) {
		this.subject_ids = subject_ids;
	}

	public String getPublisher_id() {
		return publisher_id;
	}

	public void setPublisher_id(String publisher_id) {
		this.publisher_id = publisher_id;
	}

	public String getDewey_normal() {
		return dewey_normal;
	}

	public void setDewey_normal(String dewey_normal) {
		this.dewey_normal = dewey_normal;
	}

	public String getTitle_long() {
		return title_long;
	}

	public void setTitle_long(String title_long) {
		this.title_long = title_long;
	}

	public String getMarc_enc_level() {
		return marc_enc_level;
	}

	public void setMarc_enc_level(String marc_enc_level) {
		this.marc_enc_level = marc_enc_level;
	}

	public String getTitle_latin() {
		return title_latin;
	}

	public void setTitle_latin(String title_latin) {
		this.title_latin = title_latin;
	}

	public String getPublisher_text() {
		return publisher_text;
	}

	public void setPublisher_text(String publisher_text) {
		this.publisher_text = publisher_text;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getEdition_info() {
		return edition_info;
	}

	public void setEdition_info(String edition_info) {
		this.edition_info = edition_info;
	}
    
}
