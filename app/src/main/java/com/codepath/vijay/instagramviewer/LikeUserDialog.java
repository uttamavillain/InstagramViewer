package com.codepath.vijay.instagramviewer;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
public class LikeUserDialog extends DialogFragment {

    public LikeUserDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static LikeUserDialog newInstance(ArrayList<User> users) {
        LikeUserDialog frag = new LikeUserDialog();
        Bundle args = new Bundle();
        args.putParcelableArrayList("likesuserdata",users);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_likes_main, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        // Fetch arguments from bundle and set title
        ArrayList<User> users = getArguments().getParcelableArrayList("likesuserdata");
        getDialog().setTitle("Likes");
        // Construct the data source
        // Create the adapter to convert the array to views
        LikesAdapter adapter = new LikesAdapter(getContext(), users);
        // Attach the adapter to a ListView
        ListView listView = (ListView) view.findViewById(R.id.lvLikes);
        listView.setAdapter(adapter);
    }

    class LikesAdapter extends ArrayAdapter<User> {
        public LikesAdapter(Context context, ArrayList<User> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            User user = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_likes_user, parent, false);
            }

            final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            // Show progress bar
            progressBar.setVisibility(View.VISIBLE);

            // Lookup view for data population
            ImageView ivAuthor = (ImageView) convertView.findViewById(R.id.ivAuthor);
            TextView tvFullName = (TextView) convertView.findViewById(R.id.tvFullName);
            TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);

            // Populate the data into the template view using the data object
            tvFullName.setText(user.getFullName());
            tvUserName.setText(user.getUserName());

            Transformation transformation = new RoundedTransformationBuilder()
                    .borderColor(Color.BLACK)
                    .borderWidthDp(3)
                    .cornerRadiusDp(30)
                    .oval(false)
                    .build();

            Picasso.with(getContext()).load(user.getProfilePic()).transform(transformation).placeholder(R.drawable.profile_pc).into(ivAuthor,
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