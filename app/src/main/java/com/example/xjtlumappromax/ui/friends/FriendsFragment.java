package com.example.xjtlumappromax.ui.friends;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.xjtlumappromax.DatabaseHelper;
import com.example.xjtlumappromax.DatabaseQueryHelper;
import com.example.xjtlumappromax.R;
import com.example.xjtlumappromax.ui.friends.FriendsViewModel.Friend;
import com.example.xjtlumappromax.databinding.FragmentFriendsBinding;

import java.util.List;

public class FriendsFragment extends Fragment {

    private FragmentFriendsBinding binding;
    private FriendsViewModel friendsViewModel;
    private SQLiteDatabase db;
    private DatabaseQueryHelper dbQueryHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        friendsViewModel = new ViewModelProvider(this).get(FriendsViewModel.class);

        // Set up database connection
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        try {
            db = databaseHelper.openDatabase();
            dbQueryHelper = new DatabaseQueryHelper(db);
        } catch (Exception e) {
            e.printStackTrace();
        }

        binding = FragmentFriendsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ListView listView = root.findViewById(R.id.view_more);
        final FriendsAdapter adapter = new FriendsAdapter(getContext(), R.layout.fragment_friends, friendsViewModel.getTopFriends(dbQueryHelper));
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Friend selectedFriend = (Friend) adapter.getItem(position);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (db != null) {
            db.close();
        }
    }
}
