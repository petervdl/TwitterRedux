package com.codepath.apps.basictwitter.fragments;

import java.util.ArrayList;

import org.json.JSONArray;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.apps.basictwitter.EndlessScrollListener;
import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.TweetArrayAdapter;
import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.TwitterClient;
import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class TweetsListFragment extends Fragment {

	public static final int MAX_TWEETS_PER_CALL = 10;
	private TwitterClient client;
	private ArrayList<Tweet> tweets;
	private ArrayAdapter<Tweet> aTweets;
	private eu.erikw.PullToRefreshListView lvTweets;
	private ProgressBar pb;
	
	public TweetsListFragment() {
		super();
		client = TwitterApplication.getRestClient();
		client.setFirstRequest(true);
	}

	public String whichTimeline() {
		// this method must be overidden by child classes to specify timeline (user, home, mentions...)
		throw new Error("no timeline choice set");
		//return "";
	}

	OnScrollListener esl = new EndlessScrollListener() {
		@Override
		public void onLoadMore(int page, int totalItemsCount) {
			// Triggered only when new data needs to be appended to the list
			// invokes code to append new items to my AdapterView
			Log.d("simpletwitter", "totalItemsCount=" + totalItemsCount);
			Log.d("simpletwitter", "tweets ArrayList size=" + tweets.size());

			if (totalItemsCount >= tweets.size()) {
				getNextPageOfTweets();   

				// getNextPageOfTweets( MAX_TWEETS_PER_CALL, -1, getOldestSeen());   
			}
		}
	};


	public boolean isNetworkAvailable() {
		// from http://developer.android.com/training/basics/network-ops/managing.html
		ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}


	public void getNextPageOfTweets() {
		// this method asks for the next page of results.

		if ( ! isNetworkAvailable()) {
			Toast.makeText(getActivity(), getString(R.string.no_net_connection), Toast.LENGTH_LONG).show();
			return;
		}

		long newestInCurrentGroup = getNewestSeen();
		long oldestInCurrentGroup = getOldestSeen();
		Log.d("simpletwitter","START OF getNextPageOfTweets: newest id="+newestInCurrentGroup);
		Log.d("simpletwitter","START OF getNextPageOfTweets: oldest id="+oldestInCurrentGroup);
		if (pb!=null) pb.setVisibility(ProgressBar.VISIBLE);

		client.getTimeline( 
				whichTimeline(), 
				MAX_TWEETS_PER_CALL, 
				getNewestSeen(), 
				getOldestSeen(),    
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONArray json) {
						super.onSuccess(json);
						pb.setVisibility(ProgressBar.INVISIBLE);

						addAll(Tweet.fromJSONArray(json) );
		                lvTweets.onRefreshComplete();

						long newestInCurrentGroup = getNewestSeen();
						long oldestInCurrentGroup = getOldestSeen();

						Log.d("simpletwitter", "in getNextPageofTweets, read " +json.toString());
						newestInCurrentGroup = getNewestSeen();
						oldestInCurrentGroup = getOldestSeen();
						Log.d("simpletwitter","END OF getNextPageOfTweets: newest id="+newestInCurrentGroup);
						Log.d("simpletwitter","END OF getNextPageOfTweets: oldest id="+oldestInCurrentGroup);
					}

					@Override
					public void onFailure(Throwable e, String s) {
						super.onFailure(e, s);
						Log.d("debug", "excn in getNextPageOfTweets - " +e.getMessage());
						Log.d("debug", "excn in getNextPageOfTweets - " +s);
					}
				});
	}


	@Override
	public void onCreate(Bundle bun) {
		// non-View initialization
		super.onCreate(bun);
		tweets = new ArrayList<Tweet>();
		// the layout on the TweetArrayAdapter defines how a single tweet looks in list
		aTweets = new TweetArrayAdapter(getActivity(), tweets);  // use getActivity rarely, never to talk from Frag.
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		// View-related initialization
		View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false);

		// assign our view references
		lvTweets = (eu.erikw.PullToRefreshListView) v.findViewById(R.id.lvTweets);
		lvTweets.setAdapter(aTweets);
		lvTweets.setOnScrollListener( esl ); 
		lvTweets.setItemsCanFocus(true); // added to make images clickable

		pb = (ProgressBar) v.findViewById(R.id.pb);

		lvTweets.setOnRefreshListener(new OnRefreshListener() {
        @Override
        public void onRefresh() {
            getNextPageOfTweets();
        }
    });

		// return the inflated container with fragment content
		return v;
	}

	public void addAll(ArrayList<Tweet> tweets) {
		// delegate the adapter add, to the fragment class is
		// preferred, because better encapsulation within Fragment
		aTweets.addAll(tweets);
	}

	public long getOldestSeen() {
		long oldestInCurrentGroup = 0;
		if (aTweets==null || aTweets.getCount()==0 ) {
			oldestInCurrentGroup = 0;
		} else {
			oldestInCurrentGroup = aTweets.getItem(tweets.size() - 1).getUid() - 1;
		}
		return oldestInCurrentGroup;
	}

	public long getNewestSeen() {
		long newestInCurrentGroup = 0;
		if (aTweets==null || aTweets.getCount()==0 ) {
			newestInCurrentGroup= 0; 
		} else {
			newestInCurrentGroup = aTweets.getItem(0).getUid();
		}
		return newestInCurrentGroup;
	}
}
