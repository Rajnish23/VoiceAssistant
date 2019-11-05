package com.speech.voiceassistant.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.UUID;

@Entity(tableName = "voice_chat")
public class VoiceChat {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "messgae")
    private String voiceMessage;

    @ColumnInfo(name = "type")
    private int type;

    @Ignore
    public VoiceChat(String voiceMessage, int type){
        this(UUID.randomUUID().toString(), voiceMessage, type);
    }
    public VoiceChat(String id, String voiceMessage, int type) {
        this.id = id;
        this.voiceMessage = voiceMessage;
        this.type = type;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getVoiceMessage() {
        return voiceMessage;
    }

    public int getType() {
        return type;
    }

    public enum TYPE {
        RECORD_VOICE,
        RESPOND_VOICE
    }


}
