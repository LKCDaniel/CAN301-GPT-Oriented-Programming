package com.example.xjtlumappromax.ui.friends;

import androidx.lifecycle.ViewModel;
import com.example.xjtlumappromax.DatabaseQueryHelper;
import com.example.xjtlumappromax.DatabaseQueryHelper.Friend;

import java.util.List;

public class FriendsViewModel extends ViewModel {

    // Method to get top 3 friends from the database
    public List<Friend> getTopFriends(DatabaseQueryHelper dbQueryHelper) {
        // return dbQueryHelper.getTopFriends();
        return java.util.Collections.emptyList();
    }

    // Friend class to hold friend data
    public static class Friend {
        private String name;
        private int score;
        private int rank;

        public Friend(String name, int score, int rank) {
            this.name = name;
            this.score = score;
            this.rank = rank;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }

        public int getRank() {
            return rank;
        }
    }
}

