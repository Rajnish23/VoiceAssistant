package com.speech.voiceassistant;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.speech.voiceassistant.data.VoiceChat;
import com.speech.voiceassistant.data.VoiceChatDatabase;
import com.speech.voiceassistant.data.dao.VoiceDao;

import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = "MainViewModel";
    private VoiceDao mDao;
    private VoiceChatDatabase mDatabase;
    private CompositeDisposable disposable = new CompositeDisposable();

    private MutableLiveData<List<VoiceChat>> chatListMutableLiveData = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        mDatabase = VoiceChatDatabase.getInstance(application);
        mDao = mDatabase.getVoiceDao();
    }

    public void getChatList() {

        Disposable dis = (mDao.getChats())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(voiceChats -> {
                    chatListMutableLiveData.setValue(voiceChats);
                });

        disposable.add(dis);

    }

    public void insertVoiceCommand(VoiceChat chat) {

        mDao.insertNewChat(chat).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: Inserted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    public LiveData<List<VoiceChat>> getChatListMutableLiveData() {
        return chatListMutableLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.dispose();
    }
}
