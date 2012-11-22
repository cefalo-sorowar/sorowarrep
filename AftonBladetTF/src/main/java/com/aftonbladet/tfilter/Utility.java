package com.aftonbladet.tfilter;
/**
* This class is designed to consist the helper functions those we use during related article addition
*
* @version 1.1  Nov 12012
* @author Sorowar Khan
*/
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import com.escenic.domain.ContentDescriptor;
import com.escenic.domain.ContentSummary;
import com.escenic.domain.Link;
import com.escenic.domain.PropertyDescriptor;
import com.saplo.api.escenic.SaploConstants;
import com.saplo.api.escenic.SaploController;

import neo.xredsys.api.Article;
import neo.xredsys.api.IOAPI;
import neo.xredsys.api.IOHashKey;
import neo.xredsys.api.ObjectLoader;
import neo.xredsys.api.Person;

public class Utility {
	private static Utility instance = null;
	private final Logger mLogger = Logger.getLogger(Utility.class);

	private Utility() {
	 	 
	}

	public static Utility getInstance() {
		if (instance == null) {
			instance = new Utility();
		}
		return instance;
	}


	public String GetArticleState(Article article) {
		return article.getState().getName();
	}

	public String GetArticleContentType(Article article) {
		return article.getArticleType().getName();
	}

	public int getPublicationID(Article article) {
		return  article.getArticleType().getPublicationId();
	}

	public Boolean isProposedBoxEmpty(Article article, String relationBoxName) {
		if (article.getContentSummaries(relationBoxName).size() == 0)
			return true;

		return false;
	}

	public Link getArticleLink(Article article) {
		return new Link(IOHashKey.createURI(neo.xredsys.api.IOAtom.ObjectType.article, article.getId()),Constants.RELATION_TYPE, null, null);
	}

	public Article createArticleFromID(int articleID) {
		IOAPI objIOAPI = IOAPI.getAPI();
		Article article = null;
		try
		{
			article = objIOAPI.getObjectLoader().getArticle(articleID);
		}
		catch(Exception ex)
		{
			mLogger.info("Article not found " + ex.toString());
		}
		return article;
	}

	public List<Article> createArticleListFromIDs(List<Integer> articleIDs) {
		List<Article> articleLists = new ArrayList<Article>();
		for (Integer articleID : articleIDs) {
			Article article = createArticleFromID(articleID);
			//Checking that article exists and article is published.
			if (article != null && GetArticleState(article).compareTo(Constants.ARTICLE_PUBLISHED_STATUS) == 0 )
				articleLists.add(article);
		}
		return articleLists;
	}

	public ContentSummary createContentSummary(Article article) {

		ArrayList<PropertyDescriptor> propertyDescriptor = new ArrayList<PropertyDescriptor>();
		String contenetType = GetArticleContentType(article);
		ContentDescriptor contentDescriptor = new ContentDescriptor(Constants.CONTENT_DESCRIPTOR_VERSION, contenetType,propertyDescriptor);
		Link link = getArticleLink(article);
		ContentSummary contentSummary = null;
		try {
			contentSummary = new ContentSummary(contentDescriptor, link);
		} 
		catch (Exception ex) {
			mLogger.warn("Exception in creating createContentSummary: ");
			ex.printStackTrace();
		}

		return contentSummary;
	}

	public List<ContentSummary> CreateContentSummaries(List<Article> articles) {

		/*We only need to create content summary from content descriptor if we ever need to modify 
		 * any property descriptor. Here, we are just retrieving default content summary*/
		List<ContentSummary> contentSummaryLists = new ArrayList<ContentSummary>();
		ContentSummary contentSummary = null;
		for (Article article : articles) {
			contentSummary = article.toContentSummary();
			if (contentSummary != null)
			{
				contentSummaryLists.add(contentSummary);
			}
		}
		return contentSummaryLists;
	}
	
	public List<Integer> addThisArticleAtSaplo(Article article) {

		List<String> relatedIdListFromSaploAsString = null;
		String body = null;
		String headline = null;
		Timestamp publishedDateTS = null;
		String publishedDate = null;
		String url = null;
		Person authors = null;
		String authorName = null;
		int extTextId = 0;

		try{

			body = article.getElementText("body");
			headline = article.getElementText("");
			publishedDateTS = article.getPublishDate();
			if(publishedDateTS != null){
				publishedDate = new SimpleDateFormat(Constants.DATE_FORMAT).format(new Date(publishedDateTS.getTime()));
			}
			else{
				publishedDate = new SimpleDateFormat(Constants.DATE_FORMAT).format(new Date());
			}
			url = article.getUrl();
			authors = article.getAuthor();
			if(authors != null){
				authorName = authors.getFirstName();
			}
			//extTextId = article.getId();

		}
		catch(Exception ex){
			ex.printStackTrace();
		}

		try {
			// Initiate SaploController for temporary collection
			SaploController controller = new SaploController(SaploConstants.CollectionType.Temporary, SaploConstants.CollectionLanguage.en);

			// Add this article to Saplo Collection
			controller.addTextToCollection(body, headline, publishedDate, url, authorName);

			// Fetch related text, this will take some time, around 3-5 seconds
			// Synchronous call
			// Call Method: getRelatedTexts(int wait, int limit)
			// Unit of wait is in seconds and limit is the number of related contents
			// The method can also be called without parameters, then the default
			// values of wait and limit parameters will work.
			relatedIdListFromSaploAsString = (ArrayList<String>) controller.getRelatedTexts(5, 5);

			// End of work, close the session
			controller.closeSaploSession();

		} catch (Exception ex) {
			ex.printStackTrace();
			mLogger.error("Error during saplo communication: "  + ex.toString());
			return null;
		}


		body = null;
		headline = null;
		publishedDateTS = null;
		publishedDate = null;
		url = null;
		authors = null;
		authorName = null;


		//Cooking related IDs for now, will remove once saplo api and change log works
		relatedIdListFromSaploAsString = new ArrayList<String>();
		relatedIdListFromSaploAsString.add(104 + "");
		relatedIdListFromSaploAsString.add(105 + "");
		relatedIdListFromSaploAsString.add(117 + "");
		relatedIdListFromSaploAsString.add(1173 + ""); /*This ID does not exist in this publication. Just for testing*/
		relatedIdListFromSaploAsString.add(1147 + ""); /*This ID does not exist in this publication. Just for testing*/

		List<Integer> relatedArticleIDsFromSaplo = cleanRelatedIDs(relatedIdListFromSaploAsString);
		relatedIdListFromSaploAsString = null;
		return relatedArticleIDsFromSaplo;

	}

	/*This method clean the articles IDS found from saplo*/
	public List<Integer> cleanRelatedIDs(List<String> relatedArticleIDsFromSaploAsString) {
		List<Integer> relatedArticleIDsFromSaplo = new ArrayList<Integer>();
		for (String articleID : relatedArticleIDsFromSaploAsString) {
			try {
				int ID = Integer.parseInt(articleID);
				relatedArticleIDsFromSaplo.add(ID);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return relatedArticleIDsFromSaplo;
	}
	
	/*
	 * This method checks whether we need to add related article for this
	 * transaction. First checked whether the content type is listed and then
	 * checked whether proposed relation box is empty
	 */
	public Boolean isNeedToAddRelatedArticles(Article article, String contentTypesOfProposedBox[], String proposedRelationBoxNameOfXML) {

		String curArticleContentTypeName = GetArticleContentType(article);

		for (String contentType : contentTypesOfProposedBox) {
			if (contentType.compareToIgnoreCase(curArticleContentTypeName) == 0) {
				if (isProposedBoxEmpty(article,proposedRelationBoxNameOfXML)) {
					return true;
				}
				return false;
			}
		}

		return false;
	}



	
}
