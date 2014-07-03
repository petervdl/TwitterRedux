package com.codepath.apps.basictwitter;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 *     There will be a REST client for each API you are going to consume
 *        and a method for each endpoint you will consume/use.
 * See a full list of supported API classes:  (e.g. TwitterAPI)
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // base API URL for twitter
	public static final String REST_UPDATE = "statuses/update.json";
	public static final String REST_VERIFY_CREDENTIALS = "account/verify_credentials.json";
	public static final String REST_HOME_TIMELINE = "statuses/home_timeline.json";
	public static final String REST_MENTIONS_TIMELINE = "statuses/mentions_timeline.json";
	public static final String REST_USER_TIMELINE = "statuses/user_timeline.json";
	public static final String REST_MY_INFO = "account/verify_credentials.json";
	public static final String REST_PERSON_INFO = "users/show.json";


	public static final String REST_CONSUMER_KEY = "WQjqApThfceUQk4BBZ5yhlkWr";       // Given by twitter
	public static final String REST_UID = "148152701";  // assigned by twitter
	public static final String REST_CONSUMER_SECRET = "0Sqcmv2UZKpA8ErFPalaHjUBDp2sLmIncYhVRoHTXrATZmorNZ"; // from Twit
	public static final String REST_CALLBACK_URL = "oauth://cpbasictweets"; // (Also in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}
    static public boolean setFirstRequest(boolean val) {
    	firstRequest = val;
    	return val;
    }
	static boolean firstRequest = true;
	
	public void postStatus(String status, AsyncHttpResponseHandler handler) {
		final String apiUrl = getApiUrl(REST_UPDATE);
		RequestParams params = new RequestParams();
		params.put("status", status); 
		client.post(apiUrl, params, handler);   
		// Log.d("simpletwitter", "have posted status update.  Look for result.");
	}

	public void getTimeline(String whichTimeline, int count, long sinceId, long maxId, AsyncHttpResponseHandler handler) {
		String apiPartOnly ="";
		
		RequestParams params = new RequestParams();
		// grab the uid if its in the timeline
		int i = whichTimeline.indexOf("+"); // chars after this are uid
	    if (i > -1 ) { // uid found, get it into a string
	    	String tmp  = whichTimeline.substring(0,i);
	    	String uid  = whichTimeline.substring(i+1);
	    	whichTimeline = tmp;
	    	params.put("user_id", uid);
	    }
		params.put("count", ""+count);  
		params.put("include_rts", "1"); // Twitter docs recommend using this, when using count

		
		final String apiUrl = getApiUrl( whichTimeline );

		if (firstRequest) {
			firstRequest = false;
		} else {
			// these params are only used on second and subsequent attempts
			// params.put("since_id", ""+sinceId);
			params.put("max_id", ""+maxId);
		}
        Log.d("simpletwitter", "sending get, oldest(max)="+maxId+", since(newest)="+sinceId);
		client.get(apiUrl, params, handler);   // warning, if NO params, pass null!

	}
	

	public void getPersonInfo(long uid, AsyncHttpResponseHandler handler) {
		final String apiUrl = getApiUrl( REST_PERSON_INFO );
		RequestParams params = new RequestParams();
		params.put("user_id", ""+uid);  
        client.get(apiUrl, params, handler);
	}
	
	public void getMyInfo(AsyncHttpResponseHandler handler) {
		final String apiUrl = getApiUrl( REST_MY_INFO );
        client.get(apiUrl, null, handler);
	}
	
	public void getHomeTimeline(int count, long sinceId, long maxId, AsyncHttpResponseHandler handler) {
		getTimeline(REST_HOME_TIMELINE, count, sinceId, maxId, handler);
	}

}