package com.speech.voiceassistant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.speech.voiceassistant.adapter.ChatAdapter;
import com.speech.voiceassistant.data.VoiceChat;
import com.speech.voiceassistant.util.Constants;
import com.speech.voiceassistant.util.NumberDialog;
import com.speech.voiceassistant.util.PermissionsUtility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.speech.voiceassistant.util.Constants.ALARM_CONSTANT;
import static com.speech.voiceassistant.util.Constants.CALL_CONSTANT;
import static com.speech.voiceassistant.util.Constants.NAME;
import static com.speech.voiceassistant.util.Constants.SAVE_CONSTANT;
import static com.speech.voiceassistant.util.Constants.TIME_CONSTANT;
import static com.speech.voiceassistant.util.Constants.WELCOME_CONSTANT;
import static com.speech.voiceassistant.util.Constants.WHATSAPP_CALL_CONSTANT;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, NumberDialog.SuccessListener {

    private static final String TAG = "MainActivity";
    private static final int VOICE_RECOGNIZE_RESULT = 100;

    private View rootLayout;
    private ChatAdapter mAdapter;
    private List<VoiceChat> messageList = new ArrayList();

    private TextToSpeech textToSpeech;
    private MainViewModel mViewmodel;

    private boolean SAVE_CONTACT = false;
    private STATUS CALL_STATUS = STATUS.NO_CALL; //true for normal and false for whatsapp call

    private enum STATUS {
        NORMAL_CALL,
        WHATSAPP_CALL,
        NO_CALL
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        setupToolbar();

        setupFabListener();

        PermissionsUtility.checkRequiredPermission(this);

        initializeTextToSpeech();

        setupRecyclerView(messageList);

        setupViewModel();
    }

    private void setupViewModel() {

        mViewmodel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(MainViewModel.class);
        mViewmodel.getChatList();

        mViewmodel.getChatListMutableLiveData().observe(this, this::setupRecyclerView);
    }

    private void setupRecyclerView(List<VoiceChat> voiceChats) {
        RecyclerView recyclerView = findViewById(R.id.voice_chat_recyclerview);
        LinearLayout emptyLayout = findViewById(R.id.empty_layout);
        mAdapter = new ChatAdapter(voiceChats);
        recyclerView.setAdapter(mAdapter);
        if (voiceChats == null || voiceChats.size() == 0) {
            emptyLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.scrollToPosition(voiceChats.size()-1);
        }

    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(this, this);
    }

    private void setupFabListener() {
        FloatingActionButton fab = findViewById(R.id.record_voice);
        fab.setOnClickListener((view) -> {
            startListening();
        });
    }

    private void startListening() {
        Intent voiceRecognizeIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voiceRecognizeIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voiceRecognizeIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi");
        startActivityForResult(voiceRecognizeIntent, VOICE_RECOGNIZE_RESULT);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initViews() {
        rootLayout = findViewById(R.id.root_layout);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermissionsUtility.RECORD_AUDIO_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission Granted
                    speak("Permission granted");
                } else {
                    speak("Permission denied!");
                    finishActivityAfterDelay();
                }
                break;
            case PermissionsUtility.READ_CONTACT_CALL_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    speak("Permission Granted");
                } else {
                    speak("Permission denied!");
                    finishActivityAfterDelay();
                }
                break;

            case PermissionsUtility.WRITE_CONTACT_CALL_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    speak("Permission Granted");
                } else {
                    speak("Permission denied!");
                }
                break;
        }

    }

    private void finishActivityAfterDelay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.shutdown();
                textToSpeech = null;
                finish();
            }
        }, 3500);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case VOICE_RECOGNIZE_RESULT:
                if (resultCode == RESULT_OK && data != null) {
                    List<String> output = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    respondToInputCommand(output);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textToSpeech.shutdown();
        textToSpeech = null;
    }

    private void respondToInputCommand(List<String> recognizedInput) {
        if (recognizedInput != null) {
            for (String voice : recognizedInput) {
                if (WELCOME_CONSTANT.contains(voice)) {
                    mViewmodel.insertVoiceCommand(new VoiceChat(voice, 0));
                    mViewmodel.insertVoiceCommand(new VoiceChat("आपका स्वागत है", 1));
                    speak("आपका स्वागत है");
                    break;
                } else if (CALL_CONSTANT.contains(voice)) {
                    mViewmodel.insertVoiceCommand(new VoiceChat(voice, 0));
                    mViewmodel.insertVoiceCommand(new VoiceChat("व्यक्ति का नाम बताओ", 1));
                    speak("व्यक्ति का नाम बताओ");
                    CALL_STATUS = STATUS.NORMAL_CALL;
                    break;
                }
                else if(WHATSAPP_CALL_CONSTANT.contains(voice)){
                    mViewmodel.insertVoiceCommand(new VoiceChat(voice, 0));
                    mViewmodel.insertVoiceCommand(new VoiceChat("व्यक्ति का नाम बताओ", 1));
                    speak("व्यक्ति का नाम बताओ");
                    CALL_STATUS = STATUS.WHATSAPP_CALL;
                    break;
                }
                else if (voice.contains(NAME)) {
                    mViewmodel.insertVoiceCommand(new VoiceChat(voice, 0));
                    if(CALL_STATUS == STATUS.NO_CALL){
                        mViewmodel.insertVoiceCommand(new VoiceChat("सामान्य कॉल के लिए बोलो कॉल करें| व्हाट्सएप कॉल के लिए बोलो व्हाट्सएप कॉल करें ",1));
                        speak("सामान्य कॉल के लिए बोलो कॉल करें");
                        speak("whatsapp कॉल के लिए बोलो whatsapp कॉल करें");
                    }
                    else {
                        findNameAndMakeCall(voice);
                    }
                    break;
                } else if (TIME_CONSTANT.contains(voice)) {
                    mViewmodel.insertVoiceCommand(new VoiceChat(voice, 0));
                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss", Locale.US);
                    mViewmodel.insertVoiceCommand(new VoiceChat("समय है " + dateFormat.format(new Date(System.currentTimeMillis())), 1));
                    speak("समय है " + dateFormat.format(new Date(System.currentTimeMillis())));
                    break;
                } else if (SAVE_CONSTANT.contains(voice)) {
                    mViewmodel.insertVoiceCommand(new VoiceChat(voice, 0));
                    mViewmodel.insertVoiceCommand(new VoiceChat("नाम बताएं और फिर नंबर दर्ज करें", 1));
                    SAVE_CONTACT = !SAVE_CONTACT;
                    speak("नाम बताएं और फिर नंबर दर्ज करें");
                }
            }
            mAdapter.notifyDataSetChanged();
        } else {

            speak("क्षमा करें, मैं समझ नहीं सका! कृपया पुनः प्रयास करें।");
        }
    }

    private void findNameAndMakeCall(String input) {

        String name = input.substring(input.indexOf("है") + 2).trim();

        if (!TextUtils.isEmpty(name)) {
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            if (SAVE_CONTACT) {
                saveContact(name);
            } else {
                findContact(name);
            }
        } else {
            speak("क्षमा करें, मैं समझ नहीं सका! कृपया पुनः प्रयास करें।");
        }
    }

    private void saveContact(String name) {
        NumberDialog numberDialog = NumberDialog.getInstance(name);
        numberDialog.setSuccessListener(this);
        numberDialog.setCancelable(false);
        numberDialog.show(getSupportFragmentManager(), "number");
    }

    private void findContact(String name) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                        == PackageManager.PERMISSION_GRANTED) {

            Cursor cursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI, null,
                    ContactsContract.Data.DISPLAY_NAME + " LIKE ?",
                    new String[]{name}, ContactsContract.Contacts.DISPLAY_NAME);
            if (cursor != null) {
                boolean isFound = false;
                while (cursor.moveToNext()) {
                    long _id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data._ID));
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                    String mimeType = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE));
                    if (displayName.trim().equalsIgnoreCase(name.trim())) {
                        if (Constants.VOICE_CALL_MIME_TYPE.equals(mimeType)) {
                            isFound = true;
                            mViewmodel.insertVoiceCommand(new VoiceChat("कॉलिंग " + displayName, 1));
                            mAdapter.notifyDataSetChanged();
                            if(CALL_STATUS == STATUS.NORMAL_CALL){
                                makeCall(_id, number.substring(0, number.indexOf('@')));
                            }else if (CALL_STATUS == STATUS.WHATSAPP_CALL){
                                makeCall(_id, "");
                            }
                            break;
                        }
                    }
                }
                if (!isFound) {
                    mViewmodel.insertVoiceCommand(new VoiceChat("आपकी संपर्क पुस्तक में यह नाम नहीं है", 1));
                    speak("आपकी संपर्क पुस्तक में यह नाम नहीं है");
                }
            }
        } else {
            PermissionsUtility.checkReadContactAndCallPerrmission(this);
        }

    }


    private void makeCall(long id, String number) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {

            if(CALL_STATUS == STATUS.NORMAL_CALL){
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel: "+number));
                startActivity(callIntent);
            }
            else if(CALL_STATUS == STATUS.WHATSAPP_CALL) {
                try {
                    String data = "content://com.android.contacts/data/" + id;
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_VIEW);
                    sendIntent.setDataAndType(Uri.parse(data), Constants.VOICE_CALL_MIME_TYPE);
                    sendIntent.setPackage("com.whatsapp");
                    startActivity(sendIntent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }

            CALL_STATUS = STATUS.NO_CALL;
        }
    }

    private void speak(String word) {
        textToSpeech.speak(word, TextToSpeech.QUEUE_ADD, null);

    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(new Locale("hin-IND"));
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {

            }
        }
    }

    @Override
    public void successOrFail(boolean isSuccess) {
        if(!isSuccess){
            mViewmodel.insertVoiceCommand(new VoiceChat("फिर से नाम बोलो", 1));
            speak("फिर से नाम बोलो");
        }
        else{
            mViewmodel.insertVoiceCommand(new VoiceChat("संपर्क पुस्तक में नंबर जोड़ा गया",1));
            speak("संपर्क पुस्तक में नंबर जोड़ा गया");
        }
        SAVE_CONTACT = !isSuccess;
    }
}
