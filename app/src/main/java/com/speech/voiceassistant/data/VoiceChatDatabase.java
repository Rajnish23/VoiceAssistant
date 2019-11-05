package com.speech.voiceassistant.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.speech.voiceassistant.data.dao.VoiceDao;

@Database(version = 1, exportSchema = false, entities = {VoiceChat.class})
public abstract class VoiceChatDatabase extends RoomDatabase {

    public static VoiceChatDatabase INSTANCE =  null;

    public abstract VoiceDao getVoiceDao();

    public static VoiceChatDatabase getInstance(Context context){
        if(INSTANCE == null){
            synchronized (VoiceChatDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            VoiceChatDatabase.class, "VoiceAssistant.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
