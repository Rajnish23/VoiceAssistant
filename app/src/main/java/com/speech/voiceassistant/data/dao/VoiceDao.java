package com.speech.voiceassistant.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.speech.voiceassistant.data.VoiceChat;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;

@Dao
public interface VoiceDao {

    @Query("SELECT * FROM voice_chat")
    Observable<List<VoiceChat>> getChats();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertNewChat(VoiceChat voiceChat);

    @Query("DELETE FROM VOICE_CHAT")
    Completable deleteAllRecords();
}
