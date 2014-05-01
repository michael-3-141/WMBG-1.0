package com.perlib.wmbg.book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoogleBooksBook {

	private String kind;
	private Integer totalItems;
	private List<Item> items = new ArrayList<Item>();
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	
	public Book toBook(){
		if(items.size() < 1){
			return null;
		}
		else if(items.get(0).getKind().equals("books#volume"))
		{
			return null;
		}
		else{
			VolumeInfo book = items.get(0).getVolumeInfo();
			Book result = new Book();
			result.setName(book.getTitle());
			if(book.getAuthors().size() >= 1)result.setAuthor(book.getAuthors().get(0));
			if(book.getImageLinks().getSmallThumbnail() != null && book.getImageLinks().getSmallThumbnail() != "")result.setThumbnailUrl(book.getImageLinks().getSmallThumbnail());
			return result;
		}
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public Integer getTotalItems() {
		return totalItems;
	}

	public void setTotalItems(Integer totalItems) {
		this.totalItems = totalItems;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	public class AccessInfo {

		private String country;
		private String viewability;
		private Boolean embeddable;
		private Boolean publicDomain;
		private String textToSpeechPermission;
		private Epub epub;
		private Pdf pdf;
		private String webReaderLink;
		private String accessViewStatus;
		private Boolean quoteSharingAllowed;
		private Map<String, Object> additionalProperties = new HashMap<String, Object>();

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String getViewability() {
			return viewability;
		}

		public void setViewability(String viewability) {
			this.viewability = viewability;
		}

		public Boolean getEmbeddable() {
			return embeddable;
		}

		public void setEmbeddable(Boolean embeddable) {
			this.embeddable = embeddable;
		}

		public Boolean getPublicDomain() {
			return publicDomain;
		}

		public void setPublicDomain(Boolean publicDomain) {
			this.publicDomain = publicDomain;
		}

		public String getTextToSpeechPermission() {
			return textToSpeechPermission;
		}

		public void setTextToSpeechPermission(String textToSpeechPermission) {
			this.textToSpeechPermission = textToSpeechPermission;
		}

		public Epub getEpub() {
			return epub;
		}

		public void setEpub(Epub epub) {
			this.epub = epub;
		}

		public Pdf getPdf() {
			return pdf;
		}

		public void setPdf(Pdf pdf) {
			this.pdf = pdf;
		}

		public String getWebReaderLink() {
			return webReaderLink;
		}

		public void setWebReaderLink(String webReaderLink) {
			this.webReaderLink = webReaderLink;
		}

		public String getAccessViewStatus() {
			return accessViewStatus;
		}

		public void setAccessViewStatus(String accessViewStatus) {
			this.accessViewStatus = accessViewStatus;
		}

		public Boolean getQuoteSharingAllowed() {
			return quoteSharingAllowed;
		}

		public void setQuoteSharingAllowed(Boolean quoteSharingAllowed) {
			this.quoteSharingAllowed = quoteSharingAllowed;
		}

		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		public void setAdditionalProperty(String name, Object value) {
			this.additionalProperties.put(name, value);
		}

	}

	public class Epub {

		private Boolean isAvailable;
		private Map<String, Object> additionalProperties = new HashMap<String, Object>();

		public Boolean getIsAvailable() {
			return isAvailable;
		}

		public void setIsAvailable(Boolean isAvailable) {
			this.isAvailable = isAvailable;
		}

		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		public void setAdditionalProperty(String name, Object value) {
			this.additionalProperties.put(name, value);
		}

	}

	public class ImageLinks {

		private String smallThumbnail;
		private String thumbnail;
		private Map<String, Object> additionalProperties = new HashMap<String, Object>();

		public String getSmallThumbnail() {
			return smallThumbnail;
		}

		public void setSmallThumbnail(String smallThumbnail) {
			this.smallThumbnail = smallThumbnail;
		}

		public String getThumbnail() {
			return thumbnail;
		}

		public void setThumbnail(String thumbnail) {
			this.thumbnail = thumbnail;
		}

		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		public void setAdditionalProperty(String name, Object value) {
			this.additionalProperties.put(name, value);
		}

	}

	public class IndustryIdentifier {

		private String type;
		private String identifier;
		private Map<String, Object> additionalProperties = new HashMap<String, Object>();

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getIdentifier() {
			return identifier;
		}

		public void setIdentifier(String identifier) {
			this.identifier = identifier;
		}

		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		public void setAdditionalProperty(String name, Object value) {
			this.additionalProperties.put(name, value);
		}

	}

	public class Item {

		private String kind;
		private String id;
		private String etag;
		private String selfLink;
		private VolumeInfo volumeInfo;
		private SaleInfo saleInfo;
		private AccessInfo accessInfo;
		private SearchInfo searchInfo;
		private Map<String, Object> additionalProperties = new HashMap<String, Object>();

		public String getKind() {
			return kind;
		}

		public void setKind(String kind) {
			this.kind = kind;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getEtag() {
			return etag;
		}

		public void setEtag(String etag) {
			this.etag = etag;
		}

		public String getSelfLink() {
			return selfLink;
		}

		public void setSelfLink(String selfLink) {
			this.selfLink = selfLink;
		}

		public VolumeInfo getVolumeInfo() {
			return volumeInfo;
		}

		public void setVolumeInfo(VolumeInfo volumeInfo) {
			this.volumeInfo = volumeInfo;
		}

		public SaleInfo getSaleInfo() {
			return saleInfo;
		}

		public void setSaleInfo(SaleInfo saleInfo) {
			this.saleInfo = saleInfo;
		}

		public AccessInfo getAccessInfo() {
			return accessInfo;
		}

		public void setAccessInfo(AccessInfo accessInfo) {
			this.accessInfo = accessInfo;
		}

		public SearchInfo getSearchInfo() {
			return searchInfo;
		}

		public void setSearchInfo(SearchInfo searchInfo) {
			this.searchInfo = searchInfo;
		}

		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		public void setAdditionalProperty(String name, Object value) {
			this.additionalProperties.put(name, value);
		}

	}

	public class Pdf {

		private Boolean isAvailable;
		private Map<String, Object> additionalProperties = new HashMap<String, Object>();

		public Boolean getIsAvailable() {
			return isAvailable;
		}

		public void setIsAvailable(Boolean isAvailable) {
			this.isAvailable = isAvailable;
		}

		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		public void setAdditionalProperty(String name, Object value) {
			this.additionalProperties.put(name, value);
		}

	}

	public class SaleInfo {

		private String country;
		private String saleability;
		private Boolean isEbook;
		private Map<String, Object> additionalProperties = new HashMap<String, Object>();

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String getSaleability() {
			return saleability;
		}

		public void setSaleability(String saleability) {
			this.saleability = saleability;
		}

		public Boolean getIsEbook() {
			return isEbook;
		}

		public void setIsEbook(Boolean isEbook) {
			this.isEbook = isEbook;
		}

		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		public void setAdditionalProperty(String name, Object value) {
			this.additionalProperties.put(name, value);
		}

	}

	public class SearchInfo {

		private String textSnippet;
		private Map<String, Object> additionalProperties = new HashMap<String, Object>();

		public String getTextSnippet() {
			return textSnippet;
		}

		public void setTextSnippet(String textSnippet) {
			this.textSnippet = textSnippet;
		}

		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		public void setAdditionalProperty(String name, Object value) {
			this.additionalProperties.put(name, value);
		}

	}

	public class VolumeInfo {

		private String title;
		private List<String> authors = new ArrayList<String>();
		private String publisher;
		private String publishedDate;
		private String description;
		private List<IndustryIdentifier> industryIdentifiers = new ArrayList<IndustryIdentifier>();
		private Integer pageCount;
		private String printType;
		private List<String> categories = new ArrayList<String>();
		private Double averageRating;
		private Integer ratingsCount;
		private String contentVersion;
		private ImageLinks imageLinks;
		private String language;
		private String previewLink;
		private String infoLink;
		private String canonicalVolumeLink;
		private Map<String, Object> additionalProperties = new HashMap<String, Object>();

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public List<String> getAuthors() {
			return authors;
		}

		public void setAuthors(List<String> authors) {
			this.authors = authors;
		}

		public String getPublisher() {
			return publisher;
		}

		public void setPublisher(String publisher) {
			this.publisher = publisher;
		}

		public String getPublishedDate() {
			return publishedDate;
		}

		public void setPublishedDate(String publishedDate) {
			this.publishedDate = publishedDate;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public List<IndustryIdentifier> getIndustryIdentifiers() {
			return industryIdentifiers;
		}

		public void setIndustryIdentifiers(List<IndustryIdentifier> industryIdentifiers) {
			this.industryIdentifiers = industryIdentifiers;
		}

		public Integer getPageCount() {
			return pageCount;
		}

		public void setPageCount(Integer pageCount) {
			this.pageCount = pageCount;
		}

		public String getPrintType() {
			return printType;
		}

		public void setPrintType(String printType) {
			this.printType = printType;
		}

		public List<String> getCategories() {
			return categories;
		}

		public void setCategories(List<String> categories) {
			this.categories = categories;
		}

		public Double getAverageRating() {
			return averageRating;
		}

		public void setAverageRating(Double averageRating) {
			this.averageRating = averageRating;
		}

		public Integer getRatingsCount() {
			return ratingsCount;
		}

		public void setRatingsCount(Integer ratingsCount) {
			this.ratingsCount = ratingsCount;
		}

		public String getContentVersion() {
			return contentVersion;
		}

		public void setContentVersion(String contentVersion) {
			this.contentVersion = contentVersion;
		}

		public ImageLinks getImageLinks() {
			return imageLinks;
		}

		public void setImageLinks(ImageLinks imageLinks) {
			this.imageLinks = imageLinks;
		}

		public String getLanguage() {
			return language;
		}

		public void setLanguage(String language) {
			this.language = language;
		}

		public String getPreviewLink() {
			return previewLink;
		}

		public void setPreviewLink(String previewLink) {
			this.previewLink = previewLink;
		}

		public String getInfoLink() {
			return infoLink;
		}

		public void setInfoLink(String infoLink) {
			this.infoLink = infoLink;
		}

		public String getCanonicalVolumeLink() {
			return canonicalVolumeLink;
		}

		public void setCanonicalVolumeLink(String canonicalVolumeLink) {
			this.canonicalVolumeLink = canonicalVolumeLink;
		}

		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		public void setAdditionalProperty(String name, Object value) {
			this.additionalProperties.put(name, value);
		}

	}
}
