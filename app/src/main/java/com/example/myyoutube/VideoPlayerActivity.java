package com.example.myyoutube;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myyoutube.adapters.VideoListAdapter;
import com.example.myyoutube.classes.Comment;
import com.example.myyoutube.classes.User;
import com.example.myyoutube.classes.Video;
import com.example.myyoutube.managers.UserManager;
import com.example.myyoutube.managers.VideoManager;
import com.example.myyoutube.login.logInScreen1;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VideoPlayerActivity extends AppCompatActivity {

    private Video video;
    private CommentsAdapter commentsAdapter;
    private List<Comment> commentsList;
    private VideoListAdapter videoListAdapter;
    private List<Video> otherVideos;
    private ImageButton likeButton, dislikeButton;
    private RecyclerView rvOtherVideos;
    private User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        MenuItem profilePictureItem = bottomNavigationView.getMenu().findItem(R.id.nav_login);
        profilePictureItem.setIcon(R.drawable.login);
        profilePictureItem.setTitle("Login");

        currentUser = MainActivity.getCurrentUser();
        if (currentUser != null) {
            Bitmap bitmap = decodeImage(currentUser.getProfileImage());
            NavigationView navigationView = findViewById(R.id.nav_view);
            profilePictureItem.setIcon(R.drawable.nav_logout);
            profilePictureItem.setTitle("Logout");
        }

        int videoId = getIntent().getIntExtra("videoId", -1);
        video = VideoManager.getVideoManager().getVideoById(videoId);
        if (video == null) {
            finish();
            return;
        }

        Uri videoUri;
        VideoView videoView = findViewById(R.id.videoView);
        if (isFileInRaw(video.getMp4file())) {
            String videoPath = "android.resource://" + getPackageName() + "/raw/" + video.getMp4file();
            videoUri = Uri.parse(videoPath);
        } else {
            try {
                videoUri = decodeBase64ToVideoFile(video.getMp4file());
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load video", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        videoView.setVideoURI(videoUri);
        commentsList = video.getComments();

        if (commentsList == null) {
            commentsList = new ArrayList<>();
        }

        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        videoView.start();

        TextView titleView = findViewById(R.id.tvTitle);
        TextView channelNameView = findViewById(R.id.tvChannelName);
        TextView viewsView = findViewById(R.id.tvViews);
        TextView likesView = findViewById(R.id.tvLikes);
        TextView timeAgoView = findViewById(R.id.tvTimeAgo);
        ImageView IVChannelPic = findViewById(R.id.IVChannelPic);
        Bitmap channelPicBitmap = decodeImage(UserManager.getUserByEmail(video.getChannelEmail()).getProfileImage());
        IVChannelPic.setImageBitmap(channelPicBitmap);

        timeAgoView.setText(video.getTimeAgo());

        titleView.setText(video.getTitle());
        channelNameView.setText(Objects.requireNonNull(UserManager.getUserByEmail(video.getChannelEmail())).getUserName());
        viewsView.setText("Views: " + video.getViews());
        likesView.setText("" + video.getLikes());

        ImageButton shareBtn = findViewById(R.id.IBShare);
        likeButton = findViewById(R.id.IBLike);
        dislikeButton = findViewById(R.id.IBDisLike);
        updateLikeDislikeButtons();

        shareBtn.setOnClickListener(v -> {
            // Sharing
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this video!");
            shareIntent.putExtra(Intent.EXTRA_TEXT, videoUri.toString());
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });

        likeButton.setOnClickListener(v -> {
            if (currentUser != null) {
                if (currentUser.hasLikedVideo(video.getId())) {
                    video.decrementLikes();
                    currentUser.removeLikedVideo(video.getId());
                } else {
                    video.incrementLikes();
                    video.decrementDislikes();
                    currentUser.addLikedVideo(video.getId());
                    if (currentUser.hasDislikedVideo(video.getId())) {
                        currentUser.removeDislikedVideo(video.getId());
                    }
                }
                likesView.setText("" + video.getLikes());
                updateLikeDislikeButtons();
            } else {
                Toast.makeText(VideoPlayerActivity.this, "User not connected", Toast.LENGTH_SHORT).show();
            }
        });

        dislikeButton.setOnClickListener(v -> {
            if (currentUser != null) {
                if (currentUser.hasDislikedVideo(video.getId())) {
                    video.decrementDislikes();
                    currentUser.removeDislikedVideo(video.getId());
                } else {
                    video.incrementDislikes();
                    video.decrementLikes();
                    currentUser.addDislikedVideo(video.getId());
                    if (currentUser.hasLikedVideo(video.getId())) {
                        currentUser.removeLikedVideo(video.getId());
                    }
                }
                likesView.setText("" + video.getLikes());
                updateLikeDislikeButtons();
            } else {
                Toast.makeText(VideoPlayerActivity.this, "User not connected", Toast.LENGTH_SHORT).show();
            }
        });

        ListView commentsListView = findViewById(R.id.commentsListView);
        commentsAdapter = new CommentsAdapter(this, video.getComments());
        commentsListView.setAdapter(commentsAdapter);

        findViewById(R.id.btnAddComment).setOnClickListener(view -> {
            if (currentUser != null) {
                EditText commentInput = findViewById(R.id.etComment);
                String newComment = commentInput.getText().toString();
                if (!newComment.isEmpty()) {
                    String profileImageBase64 = currentUser.getProfileImage();
                    Comment comment = new Comment(newComment, profileImageBase64, currentUser.getEmail());
                    video.addComment(comment);
                    commentsAdapter.notifyDataSetChanged();
                    commentInput.setText("");
                }
            } else {
                Toast.makeText(VideoPlayerActivity.this, "User not connected", Toast.LENGTH_SHORT).show();
            }
        });


        rvOtherVideos = findViewById(R.id.rvOtherVideos);
        rvOtherVideos.setLayoutManager(new LinearLayoutManager(this));

        // Fetch and display recommended videos
        fetchRecommendedVideos();

        bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.custom_red));
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent homeIntent = new Intent(VideoPlayerActivity.this, MainActivity.class);
                startActivity(homeIntent);
                return true;
            } else if (id == R.id.nav_add_video) {
                Intent intent;
                if (currentUser != null) {
                    intent = new Intent(VideoPlayerActivity.this, AddEditVideoActivity.class);
                } else {
                    intent = new Intent(VideoPlayerActivity.this, logInScreen1.class);
                }
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_login) {
                Intent intent = new Intent(VideoPlayerActivity.this, logInScreen1.class);
                startActivity(intent);
            }
            return false;
        });

        bottomNavigationView.getMenu().setGroupCheckable(0, false, true);
    }

    private void fetchRecommendedVideos() {
        int currentVideoId = getIntent().getIntExtra("videoId", -1);
        video = VideoManager.getVideoManager().getVideoById(currentVideoId);

        if (video != null) {
            otherVideos = new ArrayList<>(VideoManager.getVideoManager().getVideos());
            otherVideos.remove(video);
            videoListAdapter = new VideoListAdapter(this);
            videoListAdapter.setVideos(otherVideos);
            rvOtherVideos.setAdapter(videoListAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedVideoId", video.getId());
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }

    private boolean isFileInRaw(String fileName) {
        Resources res = getResources();
        int resourceId = res.getIdentifier(fileName, "raw", getPackageName());
        return resourceId != 0;
    }

    private void updateLikeDislikeButtons() {
        if (currentUser != null) {
            if (currentUser.hasLikedVideo(video.getId())) {
                likeButton.setImageResource(R.drawable.liked);
            } else {
                likeButton.setImageResource(R.drawable.unliked);
            }
            if (currentUser.hasDislikedVideo(video.getId())) {
                dislikeButton.setImageResource(R.drawable.disliked);
            } else {
                dislikeButton.setImageResource(R.drawable.undisliked);
            }
        } else {
            likeButton.setImageResource(R.drawable.unliked);
            dislikeButton.setImageResource(R.drawable.undisliked);
        }
    }

    private Uri decodeBase64ToVideoFile(String base64Str) throws IOException {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        File tempFile = File.createTempFile("tempVideo", ".mp4", getCacheDir());
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(decodedBytes);
        fos.close();
        return Uri.fromFile(tempFile);
    }

    private Bitmap decodeImage(String encodedImage) {
        if (encodedImage != null) {
            byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        }
        return null;
    }

    private class CommentsAdapter extends ArrayAdapter<Comment> {

        private Context context;
        private List<Comment> comments;

        public CommentsAdapter(Context context, List<Comment> comments) {
            super(context, 0, comments);
            this.context = context;
            this.comments = comments;
        }

        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_item, parent, false);
            }

            EditText commentEdit = convertView.findViewById(R.id.etComment);
            ImageButton btnMenu = convertView.findViewById(R.id.btnMenu);
            ImageView ivProfilePic = convertView.findViewById(R.id.ivProfilePic);

            Comment comment = getItem(position);
            assert comment != null;
            commentEdit.setText(comment.getCommentContent());
            commentEdit.setEnabled(false);

            // Decode and set the profile image
            String profileImageBase64 = comment.getCommentPic();
            if (profileImageBase64 != null && !profileImageBase64.isEmpty()) {
                byte[] decodedString = Base64.decode(profileImageBase64, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                ivProfilePic.setImageBitmap(decodedByte);
            } else {
                ivProfilePic.setImageResource(R.drawable.login);
            }

            btnMenu.setOnClickListener(v -> {
                if (currentUser != null) {
                    if (currentUser.getEmail().equalsIgnoreCase(comment.getCommentPublisher())) {
                        showEditDeleteDialog(position);
                    } else {
                        Toast.makeText(getContext(), "You can only edit or delete your own comments", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "User not connected", Toast.LENGTH_SHORT).show();
                }
            });

            return convertView;
        }

        private void showEditDeleteDialog(int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Choose an option")
                    .setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
                        if (which == 0) {
                            showEditCommentDialog(position);
                        } else if (which == 1) {
                            deleteComment(position);
                        }
                    })
                    .show();
        }

        private void showEditCommentDialog(int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Edit Comment");

            View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_edit_comment,
                    (ViewGroup) ((Activity) context).findViewById(android.R.id.content), false);
            EditText input = viewInflated.findViewById(R.id.input);
            input.setText(comments.get(position).getCommentContent());

            builder.setView(viewInflated);

            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                dialog.dismiss();
                String editedComment = input.getText().toString();
                if (!editedComment.isEmpty()) {
                    comments.set(position, new Comment(editedComment, comments.get(position).getCommentPic(), currentUser.getEmail()));
                    notifyDataSetChanged();
                }
            });
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

            builder.show();
        }

        private void deleteComment(int position) {
            comments.remove(position);
            notifyDataSetChanged();
        }
    }
}
