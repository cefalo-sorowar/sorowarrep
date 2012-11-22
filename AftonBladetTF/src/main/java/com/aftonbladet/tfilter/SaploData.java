package com.aftonbladet.tfilter;

public class SaploData {

	private static SaploData instance = null;
	private String mBody = null; 
	private String mHeadline = null; 
	private String mPublishDate = null; 
	private String mUrl = null; 
	private String mAuthors = null;
	private String mExtTextId = null;
	
	private SaploData(String pBody, String pHeadline, String pPublishDate, String pUrl, String pAuthors, String pExtTextId) {
		mBody = pBody;
		mHeadline = pHeadline;
		mPublishDate = pPublishDate;
		mUrl = pUrl;
		mAuthors = pAuthors;
		mExtTextId = pExtTextId;
	}

	public static SaploData getInstance(String pBody, String pHeadline, String pPublishDate, String pUrl, String pAuthors, String pExtTextId) {
		if (instance == null) {
			instance = new SaploData(pBody, pHeadline, pPublishDate, pUrl, pAuthors, pExtTextId);
		}
		return instance;
	}
	
	public void setBody(String pBody) {
		mBody = pBody;
	}

	public String getBody() {
		return mBody;
	}
	
	public void setHeadline(String pHeadline) {
		mHeadline = pHeadline;
	}

	public String getHeadliney() {
		return mHeadline;
	}
	
	public void setPublishDate(String pPublishDate) {
		mPublishDate = pPublishDate;
	}

	public String getPublishDate() {
		return mPublishDate;
	}
	
	public void setUrl(String pUrl) {
		mUrl = pUrl;
	}

	public String getUrl() {
		return mUrl;
	}
	
	public void setAuthors(String pAuthors) {
		mAuthors = pAuthors;
	}

	public String getAuthors() {
		return mAuthors;
	}

	public void setExtTextId(String pExtTextId) {
		mExtTextId = pExtTextId;
	}

	public String getExtTextId() {
		return mExtTextId;
	}


}
