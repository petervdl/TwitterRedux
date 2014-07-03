package com.codepath.apps.basictwitter;

import org.json.JSONObject;

import com.codepath.apps.basictwitter.fragments.UserTimelineFragment;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class ProfileActivity extends ActionBarActivity {

	private TwitterClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		Intent i = getIntent();
		long uid = i.getLongExtra("uid", -1);
		if (uid == -1) uid = Long.valueOf(client.REST_UID).longValue();
		Log.d("debugME", "uid in ProfileActivity is " + uid);
		createFragment(uid);
		loadProfileInfo(uid);
	}

	private void createFragment(long uid) {
		// this is dynamic so we can pass the uid to the fragment constructor
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.placeholder, new UserTimelineFragment(uid));
		ft.commit();
	}

	private void loadProfileInfo(long uid) {
		client = TwitterApplication.getRestClient();
		client.getPersonInfo( uid,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject json) {
						User u = User.fromJSON(json);
						getActionBar().setTitle("@" + u.getScreenName());
						populateProfileHeader(u);
					}
					@Override
					public void onFailure(Throwable e, String s) {
						super.onFailure(e, s);
						Log.e("debug", "excn in loadProfileInfo - " +e.getMessage());
						Log.e("debug", "excn in loadProfileInfo (contd.) - " +s);
					}
				});
	}
	
	private void populateProfileHeader(User user) {
		TextView tvName = (TextView) findViewById(R.id.tvName);
		TextView tvTagline = (TextView) findViewById(R.id.tvTagline);		
		TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
		TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
		ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
		tvName.setText(user.getName());
		tvTagline.setText(user.getTagline());
		tvFollowers.setText("" + user.getFollowersCount() + " followers");
		tvFollowing.setText("" + user.getFollowingCount() + " following");
		ImageLoader.getInstance()
			.displayImage(user.getProfileImageUrl(), ivProfileImage);
	}

}
