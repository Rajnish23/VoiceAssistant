package com.speech.voiceassistant.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final List<String> WELCOME_CONSTANT = new ArrayList<>(Arrays.asList("नमस्ते",
            "नमस्कार",
            "सुप्रभात"));

    public static final List<String> CALL_CONSTANT = new ArrayList<>(Arrays.asList("मैं किसी को कॉल करना चाहता हूं",
            "कॉल करना चाहता हूं",
            "कॉल करें",
            "फोन लगाये",
            "फोन करना चाहता हूं"));

    public static final List<String> WHATSAPP_CALL_CONSTANT = new ArrayList<>(Arrays.asList("व्हाट्सएप कॉल करना चाहता हूं",
            "व्हाट्सएप कॉल करें",
            "व्हाट्सएप फोन लगाये",
            "व्हाट्सएप फोन करना चाहता हूं"));

    public static final String NAME = "नाम है";

    public static final List<String> TIME_CONSTANT = new ArrayList<>(Arrays.asList("समय क्या हुआ है",
            "समय",
            "समय क्या है",
            "वर्तमान समय क्या है",
            "समय बताओ"));

    public static final String ALARM_CONSTANT = "अलार्म लगाये";

    public static final List<String> SAVE_CONSTANT = new ArrayList<>(Arrays.asList("नंबर जोड़ें", "नंबर सेव करें"));

    public static final String VOICE_CALL_MIME_TYPE = "vnd.android.cursor.item/vnd.com.whatsapp.voip.call";
}
