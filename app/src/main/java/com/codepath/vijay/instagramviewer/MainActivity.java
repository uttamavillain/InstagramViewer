package com.codepath.vijay.instagramviewer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getName();
    private SwipeRefreshLayout swipeContainer;
    private InstagramAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Construct the data source
        ArrayList<Instagram> popular = new ArrayList<Instagram>();
        // Create the adapter to convert the array to views
        adapter = new InstagramAdapter(this, popular);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.lvInstagram);
        listView.setAdapter(adapter);
        fetchTimelineAsync();
    }

    public void fetchTimelineAsync() {
        String url = "https://api.instagram.com/v1/media/popular?client_id=e05c462ebd86446ea48a5af73769b602";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    ArrayList<Instagram> instagrams = Instagram.fromJson(response.getJSONArray("data"));
                    adapter.clear();
                    adapter.addAll(instagrams);
                    swipeContainer.setRefreshing(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }
        });
    }

    class InstagramAdapter extends ArrayAdapter<Instagram> {
        public InstagramAdapter(Context context, ArrayList<Instagram> Instagrams) {
            super(context, 0, Instagrams);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            final Instagram instagram = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_instagram_popular, parent, false);
            }

            final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            // Show progress bar
            progressBar.setVisibility(View.VISIBLE);

            // Lookup view for data population
            final ImageView ivPopular = (ImageView) convertView.findViewById(R.id.ivPopular);
            final ImageView ivAuthor = (ImageView) convertView.findViewById(R.id.ivAuthor);
            TextView tvFullName = (TextView) convertView.findViewById(R.id.tvFullName);
            final TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            TextView tvCaption = (TextView) convertView.findViewById(R.id.tvCaption);
            TextView tvLikes = (TextView) convertView.findViewById(R.id.tvLikes);
            TextView tvCommentsCount = (TextView) convertView.findViewById(R.id.tvCommentsCount);
            ImageView ivCommentAuthor = (ImageView) convertView.findViewById(R.id.ivCommentUserPic);
            TextView tvCommentMore = (TextView) convertView.findViewById(R.id.tvCommentMore);
            TextView tvCommentText = (TextView) convertView.findViewById(R.id.tvCommentText);
            TextView tvCommentDate = (TextView) convertView.findViewById(R.id.tvCommentDate);

            // Populate the data into the template view using the data object
            tvFullName.setText(instagram.getUser().getFullName());
            tvUserName.setText(instagram.getUser().getUserName());
            tvUserName.setClickable(true);
            tvUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), InstagramBrowserActivity.class);
                    intent.putExtra("url","https://www.instagram.com/"+tvUserName.getText()+"/");
                    startActivity(intent);
                }
            });
            tvCaption.setText(instagram.getCaption());
            tvLikes.setText(instagram.getLikeCount() + " " + "likes");
            tvLikes.setClickable(true);
            tvLikes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fm = getSupportFragmentManager();
                    LikeUserDialog likeUserDialog = LikeUserDialog.newInstance(instagram.getLikes());
                    likeUserDialog.show(fm, "fragment_likes_user");
                }
            });
            if(instagram.getComments().size()>0) {
                tvCommentDate.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(instagram.getComments().get(0).getDate()) * 1000));
                tvCommentsCount.setText(instagram.getCommentCount() + " " + "comments");
                tvCommentsCount.setClickable(true);
                tvCommentsCount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startCommentDialogFrag(instagram);
                    }
                });
                Picasso.with(getContext()).load(instagram.getComments().get(0).getUser().getProfilePic()).transform(new RoundedTransformationBuilder()
                        .borderColor(Color.BLACK)
                        .borderWidthDp(2)
                        .cornerRadiusDp(5)
                        .oval(false)
                        .build())
                        .placeholder(R.drawable.profile_pc).into(ivCommentAuthor);
                tvCommentText.setText(instagram.getComments().get(0).getComment());
                tvCommentMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startCommentDialogFrag(instagram);
                    }
                });
            }
            else {
                tvCommentDate.setVisibility(View.GONE);
                tvCommentMore.setVisibility(View.GONE);
                tvCommentText.setVisibility(View.GONE);
                ivCommentAuthor.setVisibility(View.GONE);
            }

            Transformation transformation = new RoundedTransformationBuilder()
                    .borderColor(Color.BLACK)
                    .borderWidthDp(3)
                    .cornerRadiusDp(30)
                    .oval(false)
                    .build();

            Picasso.with(getContext()).load(instagram.getUser().getProfilePic()).transform(transformation).placeholder(R.drawable.profile_pc).into(ivAuthor);

            if(instagram.isImage()) {
                Picasso.with(getContext()).load(instagram.getImageUrl()).placeholder(R.drawable.blurred_placeholder).into(ivPopular, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onError() {

                    }
                });
            }
            else {
                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Bitmap newImag = overlay(bitmap, BitmapFactory.decodeResource(getContext().getResources(), R.drawable.play));
                        ivPopular.setImageBitmap(newImag);
                        ivPopular.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                playVideo(instagram.getVideoUrl());
                            }
                        });
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                Picasso.with(getContext()).load(instagram.getImageUrl()).placeholder(R.drawable.blurred_placeholder).into(target);
            }

            //Return the completed view to render on screen
            return convertView;
        }

        private void startCommentDialogFrag(Instagram instagram) {
            FragmentManager fm = getSupportFragmentManager();
            CommentsDialog commentsDialog = CommentsDialog.newInstance(instagram.getComments());
            commentsDialog.show(fm, "fragment_comments");
        }

        public void playVideo(String url) {
            Intent i = new Intent(getContext(), VideoPlayerActivity.class);
            i.putExtra("url", url);
            startActivity(i);
        }

        private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
            Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
            Canvas canvas = new Canvas(bmOverlay);
            canvas.drawBitmap(bmp1, new Matrix(), null);
            canvas.drawBitmap(bmp2, new Matrix(), null);
            return bmOverlay;
        }
    }
}