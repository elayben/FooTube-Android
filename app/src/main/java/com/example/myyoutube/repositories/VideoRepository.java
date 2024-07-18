package com.example.myyoutube.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.myyoutube.AppDB;
import com.example.myyoutube.Helper;
import com.example.myyoutube.api.VideoAPI;
import com.example.myyoutube.dao.VideoDao;
import com.example.myyoutube.entities.Video;

import java.util.LinkedList;
import java.util.List;

public class VideoRepository {
    private final VideoAPI videoAPI;
    private final VideoDao videoDao;
    private final VideoListData videoListData;
    private final AppDB db;

    public VideoRepository() {
        db = Room.databaseBuilder(Helper.context, AppDB.class, "FootubeDB")
                .fallbackToDestructiveMigration()
                .build();
        videoDao = db.videoDao();
        videoListData = new VideoListData();
        videoAPI = new VideoAPI(videoListData, videoDao);
        //reloadVideos();
    }

    public LiveData<List<Video>> getAllVideos() {
        videoAPI.getVideos();
        return videoListData;
    }

    public void addVideo(final Video video, String token) {
        videoAPI.addVideo(video, token);
    }

    public void updateVideo(final Video video, String token) {
        videoAPI.editVideo(video, token);
    }

    public void deleteVideo(final Video video, String token) {
        videoAPI.deleteVideo(video, token);
    }

    public Video getVideoById(String id) {
        return videoDao.getVideoById(id);
    }

//    public void reloadVideos() {
//        videoAPI.getVideos(videoListData);
//    }

    public class VideoListData extends MutableLiveData<List<Video>> {
        public VideoListData() {
            super();
            setValue(new LinkedList<>());
        }

        @Override
        protected void onActive() {
            super.onActive();
            new Thread(() -> {
                if (videoDao != null && videoDao.getAllVideos() != null) {
                    List<Video> videos = videoDao.getAllVideos();
                    postValue(videos);
                }
            }).start();
        }

        public void updateVideo(Video video) {
            List<Video> tempVideos = getValue();
            if (tempVideos == null)
                return;
            String videoId = video.getId();
            for (Video current : tempVideos) {
                if (current.getId().equals(videoId)) {
                    current.setPic(video.getPic());
                    current.setDescription(video.getDescription());
                    current.setTitle(video.getTitle());
                    current.setUrl(video.getUrl());
                    postValue(tempVideos);
                    break;
                }
            }
        }
        public void removeVideo(Video video) {
            List<Video> tempVideos = getValue();
            if (tempVideos == null)
                return;
            tempVideos.remove(video);
            postValue(tempVideos);
        }
        public void addVideo(Video newVideo) {
            List<Video> tempVideos = getValue();
            if (tempVideos == null)
                return;
            tempVideos.add(0, newVideo);
            postValue(tempVideos);
        }
    }
}
