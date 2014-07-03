package com.codepath.apps.basictwitter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.basictwitter.models.Tweet;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetArrayAdapter extends ArrayAdapter<Tweet> {

	public TweetArrayAdapter(Context context, List<Tweet> tweets) {
		super(context, 0, tweets);
		ctxt = context;
		
	}
		
    public static Drawable savedProfileDrawable;
    public static Spanned savedUserName;
    public static String savedScreenName;
    
    private static boolean firstTime= true;
    
	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		
		// get the tweet data for item at position pos
		Tweet tweet = getItem(pos);
		// find or inflate the template
		View v;
		if (convertView==null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			v = inflater.inflate(R.layout.tweet_item, parent, false);
		} else {
			v = convertView;
		}
		
		// find the 3 views within the template
		TextView tvUserName = (TextView) v.findViewById(R.id.tvUserName);
		TextView tvUserBody = (TextView) v.findViewById(R.id.tvBody);
		TextView tvSince = (TextView) v.findViewById(R.id.tvSince);
		TextView tvScreenName = (TextView) v.findViewById(R.id.tvScreenName);
		
		ImageView ivProfileImage = (ImageView) v.findViewById(R.id.ivProfileImage);
		ivProfileImage.setImageResource(android.R.color.transparent);
		ivProfileImage.setTag(tweet.getUser().getUid());

		ImageLoader imageLoader = ImageLoader.getInstance();
		
		//populate the views with the data from model
		imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), ivProfileImage);
		addClickListener(ivProfileImage);
		Spanned boldedName = Html.fromHtml("<b>" + tweet.getUser().getUserName() + "</b>");
		tvScreenName.setText(" @"+tweet.getUser().getScreenName());
		
		if (firstTime) {
			savedProfileDrawable = ivProfileImage.getDrawable();    // save this for later
			savedUserName = boldedName;
			savedScreenName = tweet.getUser().getScreenName();
			firstTime=false;
		}
		
		tvUserName.setText( boldedName );
		tvUserBody.setText(tweet.getBody());
		tvSince.setText(tweet.getTimeSince());
		return v; 
	}
	
	private static Context ctxt; 
	
	OnClickListener ocl = new OnClickListener() {
		@Override
		public void onClick(View v) {
			long user_uid = (Long) v.getTag();			
	        // fire up the Profile Activity, passing this userid.
			Intent i = new Intent(ctxt, ProfileActivity.class);
			i.putExtra("uid", user_uid);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ctxt.startActivity(i);
		}
	};

	private void addClickListener(ImageView ivProfileImage) {
		ivProfileImage.setOnClickListener(ocl);
	}

}
