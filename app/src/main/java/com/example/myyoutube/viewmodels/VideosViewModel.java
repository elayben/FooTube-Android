package com.example.myyoutube.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myyoutube.repositories.VideoRepository;
import com.example.myyoutube.classes.Video;

import java.util.List;

public class VideosViewModel extends ViewModel {

    private final VideoRepository mRepository;
    private final LiveData<List<Video>> videos;

    //TODO: replace getAll with get Top Videos
    public VideosViewModel() {
        mRepository = new VideoRepository();
        videos = mRepository.getAllVideos();
    }

    public LiveData<List<Video>> get() {
        return videos;
    }

    public void add(Video video) {
        mRepository.addVideo(video);
    }

    public void delete(Video video) {
        mRepository.deleteVideo(video);
    }

    public void update(Video video){
        mRepository.updateVideo(video);
    }
    public void reloadVideos() {
        mRepository.reloadVideos();
    }


}