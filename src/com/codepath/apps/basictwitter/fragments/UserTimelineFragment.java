package com.codepath.apps.basictwitter.fragments;

import android.os.Bundle;

import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.TwitterClient;

public class UserTimelineFragment extends TweetsListFragment {
	private TwitterClient client;
    private long uid;
    
	public UserTimelineFragment(long uid) {
		this.uid = uid;
	}
	@Override
	public void onCreate(Bundle bun) {
		super.onCreate(bun);
		client = TwitterApplication.getRestClient();
	}

    @Override
	public String whichTimeline() {
    	return client.REST_USER_TIMELINE + "+" + uid;
	}

	public void onActivityCreated (Bundle bun) {
		super.onActivityCreated(bun);
		getNextPageOfTweets( );
	}
	

}
