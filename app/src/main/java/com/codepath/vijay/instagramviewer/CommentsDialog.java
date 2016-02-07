package com.codepath.vijay.instagramviewer;

/**
 * Created by uttamavillain on 2/6/16.
 */

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

/**
 * Created by uttamavillain on 2/6/16.
 */
public class CommentsDialog extends DialogFragment {

    public CommentsDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static CommentsDialog newInstance(ArrayList<Comment> comments) {
        CommentsDialog frag = new CommentsDialog();
        Bundle args = new Bundle();
        args.putParcelableArrayList("commentsdata",comments);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comments_main, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        // Fetch arguments from bundle and set title
        ArrayList<Comment> comment = getArguments().getParcelableArrayList("commentsdata");
        getDialog().setTitle("Comments");
        // Construct the data source
        // Create the adapter to convert the array to views
        CommentsAdapter adapter = new CommentsAdapter(getContext(), comment);
        // Attach the adapter to a ListView
        ListView listView = (ListView) view.findViewById(R.id.lvComments);
        listView.setAdapter(adapter);
    }

    class CommentsAdapter extends ArrayAdapter<Comment> {
        public CommentsAdapter(Context context, ArrayList<Comment> comments) {
            super(context, 0, comments);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Comment comment = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_comments, parent, false);
            }

            final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            // Show progress bar
            progressBar.setVisibility(View.VISIBLE);

            // Lookup view for data population
            ImageView ivCommentUserPic = (ImageView) convertView.findViewById(R.id.ivCommentUserPic);
            TextView tvCommentText = (TextView) convertView.findViewById(R.id.tvCommentText);
            TextView tvCommentDate = (TextView) convertView.findViewById(R.id.tvCommentDate);
            // Populate the data into the template view using the data object
            tvCommentText.setText(comment.getComment());
            tvCommentDate.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(comment.getDate()) * 1000));
            Transformation transformation = new RoundedTransformationBuilder()
                    .borderColor(Color.BLACK)
                    .borderWidthDp(3)
                    .cornerRadiusDp(30)
                    .oval(false)
                    .build();

            Picasso.with(getContext()).load(comment.getUser().getProfilePic()).transform(transformation).placeholder(R.drawable.profile_pc).into(ivCommentUserPic,
                    new com.squareup.picasso.Callback() {
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

            //Return the completed view to render on screen
            return convertView;
        }
    }
}