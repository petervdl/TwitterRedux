package com.codepath.apps.basictwitter.fragments;

import android.os.Bundle;

import com.codepath.apps.basictwitter.TwitterApplication;
import com.codepath.apps.basictwitter.TwitterClient;

public class MentionsTimelineFragment extends TweetsListFragment {

	private TwitterClient client;

	@Override
	public void onCreate(Bundle bun) {
		super.onCreate(bun);
		client = TwitterApplication.getRestClient();
	}

	@Override
	public String whichTimeline() {
		return client.REST_MENTIONS_TIMELINE;
	}

	public void onActivityCreated (Bundle bun) {
		super.onActivityCreated(bun);
		getNextPageOfTweets( );
	}

}
