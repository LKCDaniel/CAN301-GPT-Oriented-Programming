package com.example.xjtlumappromax.ui.friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xjtlumappromax.R;
import com.example.xjtlumappromax.ui.friends.FriendsViewModel.Friend;

import java.util.List;

public class FriendsAdapter extends ArrayAdapter<Friend> {

    private Context context;
    private int resource;
    private List<Friend> friendsList;

    public FriendsAdapter(Context context, int resource, List<Friend> friendsList) {
        super(context, resource, friendsList);
        this.context = context;
        this.resource = resource;
        this.friendsList = friendsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        Friend friend = friendsList.get(position);

        TextView nameTextView = convertView.findViewById(R.id.username);
        TextView scoreTextView = convertView.findViewById(R.id.person_time);
        TextView rankTextView = convertView.findViewById(R.id.rank);
        ImageView logoImageView = convertView.findViewById(R.id.logo);

        nameTextView.setText(friend.getName());
        scoreTextView.setText(String.valueOf(friend.getScore()));
        rankTextView.setText(String.valueOf(friend.getRank()));

        return convertView;
    }
}

