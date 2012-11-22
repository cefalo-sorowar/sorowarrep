package com.aftonbladet.tfilter;
/**
 * This is the entry points to bring related articles from saplo and then attach with article.
 *
 * @version 1.1  Nov 12012
 * @author Sorowar Khan
 */
import java.util.List;
import neo.xredsys.api.Article;
import neo.xredsys.api.ArticleTransaction;
import neo.xredsys.api.FilterException;
import neo.xredsys.api.IOTransaction;
import neo.xredsys.api.TransactionFilter;
import org.apache.log4j.Logger;
import com.escenic.domain.ContentSummary;


public class SaploTransactionFilter implements TransactionFilter {
	private final Logger mLogger = Logger.getLogger(SaploTransactionFilter.class);
	private String mContentTypesOfProposedBox[] = null;
	private String mProposedRelationBoxNameOfXML = "";
	private int mMaxRelatedItems = 0;
	private int mSaploTimeOutInSecond = 0;

	public SaploTransactionFilter() {
		/* If the value is not available in configuration file we will use
		 default constant value.*/

		if (mProposedRelationBoxNameOfXML.length() == 0) {
			mProposedRelationBoxNameOfXML = Constants.RELATION_BOX_NAME;
		}

		if (mMaxRelatedItems == 0) {
			mMaxRelatedItems = Constants.MAX_RELATED_ITEMS;
		}

		if (mSaploTimeOutInSecond == 0) {
			mSaploTimeOutInSecond = Constants.SAPLO_TIMEOUT_IN_SECOND;
		}

		if (mContentTypesOfProposedBox == null) {
			mContentTypesOfProposedBox = Constants.CONTENT_TYPES_WITH_PROPOSED_RELATION_BOX;
		}

		System.out.println(" proposedRelationBoxNameOfXML: "+ mProposedRelationBoxNameOfXML + " maxRelatedItems: "+ mMaxRelatedItems + " saploTimeOutInSecond: "+ mSaploTimeOutInSecond);
	}

	public void doCreate(IOTransaction pObject) throws FilterException {
		if (pObject instanceof ArticleTransaction) {
			processCurrentArticle((ArticleTransaction) pObject,Constants.ARTICLE_CREATION);
		}
	}

	public void doUpdate(IOTransaction pObject) throws FilterException {
		if (pObject instanceof ArticleTransaction) {
			processCurrentArticle((ArticleTransaction) pObject,Constants.ARTICLE_EDITION);
		}
	}

	public void setMaxRelatedItems(int pMaxRelatedItems) {
		mMaxRelatedItems = pMaxRelatedItems;
	}

	public int getMaxRelatedItems() {
		return mMaxRelatedItems;
	}

	public void setProposedRelationBoxNameOfXML(String pProposedRelationBoxNameInXML) {
		mProposedRelationBoxNameOfXML = pProposedRelationBoxNameInXML;
	}

	public String getproposedRelationBoxNameOfXML() {
		return mProposedRelationBoxNameOfXML;
	}

	public void setSaploTimeOutInSecond(int pSaploTimeOutInSecond) {
		mSaploTimeOutInSecond = pSaploTimeOutInSecond;
	}

	public int getsaploTimeOutInSecond() {
		return mSaploTimeOutInSecond;
	}

	public void setContentTypesOfProposedBox(String pContentTypesOfProposedBox[]) {
		mContentTypesOfProposedBox = pContentTypesOfProposedBox;
	}

	public String[] getContentTypesOfProposedBox() {
		return mContentTypesOfProposedBox;
	}

	private void processCurrentArticle(ArticleTransaction currentArticle,int currentArticleMode) {

		try {

			Utility objUtility = Utility.getInstance();
			if (!objUtility.isNeedToAddRelatedArticles(currentArticle, mContentTypesOfProposedBox, mProposedRelationBoxNameOfXML)) {
				mLogger.debug("No need to add related article for this article");
				System.out.println("No need to add related article for this article");
				return;
			}

			List<Integer> relatedArticleIDsFromSaplo = objUtility.addThisArticleAtSaplo(currentArticle);

			if (relatedArticleIDsFromSaplo == null	||	relatedArticleIDsFromSaplo.size() == 0) {
				mLogger.debug("No related IDs found for this article from saplo");
				System.out.println("No related IDs found for this article from saplo");
				return;
			}


			List<Article> relatedArticlesFromSaplo =  objUtility.createArticleListFromIDs(relatedArticleIDsFromSaplo);
			if (relatedArticlesFromSaplo == null	||	relatedArticlesFromSaplo.size() == 0) {
				relatedArticleIDsFromSaplo = null;
				mLogger.info("No articles exist for this article from saplo");
				System.out.println("No articles exist for this article from saplo");
				return;
			}

			List<ContentSummary> updatedImageSummaryLists = objUtility.CreateContentSummaries(relatedArticlesFromSaplo);

			// Setting the content summaries got from related articles.
			if (updatedImageSummaryLists != null) {
				currentArticle.setContentSummaries(mProposedRelationBoxNameOfXML,updatedImageSummaryLists);
				mLogger.info("Successfullay attached related articles");
			}

			relatedArticlesFromSaplo = null;
			updatedImageSummaryLists = null;


		} catch (Exception ex) {
			System.out.print("Something bad happend while tring to add related items."+ ex.toString());
			mLogger.info("Something bad happend while tring to add related items."+ ex.toString());
			ex.printStackTrace();
		}
	}


	public void doDelete(IOTransaction arg0) throws FilterException {
	}

	public boolean getErrorsAreFatal() {
		return true;
	}

	public boolean isEnabled() {
		return true;
	}

}
