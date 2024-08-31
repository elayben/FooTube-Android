package com.example.myyoutube.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myyoutube.api.CommentsAPI;
import com.example.myyoutube.entities.Comment;

import java.util.LinkedList;
import java.util.List;

public class CommentRepository {
    private final CommentsAPI commentsAPI;
    private final CommentListData commentsLiveData;
    private final String videoId;

    public CommentRepository(String videoId) {
        this.videoId = videoId;
        commentsLiveData = new CommentListData();
        commentsAPI = new CommentsAPI(commentsLiveData);
    }

    public void getCommentsForVideo() {
        commentsAPI.getCommentsForVideo(videoId);
    }

    public LiveData<List<Comment>> get() {
        return commentsLiveData;
    }

    public void addComment(Comment comment, String token) {
        commentsAPI.addComment(comment, token);
    }

    public void deleteComment(Comment comment, String token) {
        commentsAPI.deleteComment(comment, token);
    }

    public void updateComment(Comment comment, String token) {
        commentsAPI.updateComment(comment, token);
    }

    public class CommentListData extends MutableLiveData<List<Comment>> {
        public CommentListData() {
            super();
            setValue(new LinkedList<>());
        }

        @Override
        protected void onActive() {
            super.onActive();
        }

        public void addComment(Comment commentToAdd) {
            List<Comment> comments = getValue();
            if (comments == null) return;
            comments.add(0, commentToAdd);
            commentsLiveData.postValue(comments);
        }

        public void updateVideo(Comment comment) {
            List<Comment> comments = getValue();
            if (comments == null)
                return;
            String commentId = comment.get_id();
            for (Comment current : comments) {
                if (current.get_id().equals(commentId)) {
                    current.setText(comment.getText());
                    commentsLiveData.postValue(comments);
                    break;
                }
            }
        }

        public void updateComment(Comment comment) {
            List<Comment> comments = getValue();
            if (comments == null) return;
            String commentId = comment.get_id();
            for (Comment curr : comments) {
                if (curr.get_id().equals(commentId)) {
                    curr.setText(comment.getText());
                    postValue(comments);
                    break;
                }
            }
        }

        public void removeComment(String id) {
            List<Comment> comments = getValue();
            if (comments == null) return;
            for (Comment comment : comments) {
                if (comment.get_id().equals(id)) {
                    comments.remove(comment);
                    commentsLiveData.postValue(comments);
                    break;
                }
            }
        }

    }
}
