package edu.osu.wifiscannernew;

import java.util.Map;
import java.util.TreeMap;

public class Constants {

    // Private constructor for Constants class (we'll reference all values as "static final")
    private Constants() {}

    static final String DELIMITER = "|", OUI_FILENAME = "oui_mfr.txt";
    static final int NUM_PROGRESS_LEVELS = 100;
    static final float MHZ_PER_GHZ = 1000.0f;

    // Frequency-channel lookup map. WLAN channels from Wikipedia. Declaration code from:
    // https://alvinalexander.com/java/how-to-populate-predefined-static-data-map-hashmap-in-java .
    // Some WLAN channels are unavailable in certain countries (due to local laws/regulations).
    static final Map<Integer, Integer> FREQUENCY_CHANNEL_MAP = new TreeMap<Integer, Integer>() {{
        put(2412, 1);
        put(2417, 2);
        put(2422, 3);
        put(2427, 4);
        put(2432, 5);
        put(2437, 6);
        put(2442, 7);
        put(2447, 8);
        put(2452, 9);
        put(2457, 10);
        put(2462, 11);
        put(2467, 12);
        put(2472, 13);
        put(2484, 14);
        put(4915, 183);
        put(4920, 184);
        put(4925, 185);
        put(4935, 187);
        put(4940, 188);
        put(4945, 189);
        put(4960, 192);
        put(4980, 196);
        put(5035, 7);
        put(5040, 8);
        put(5045, 9);
        put(5055, 11);
        put(5060, 12);
        put(5080, 16);
        put(5160, 32);
        put(5170, 34);
        put(5180, 36);
        put(5190, 38);
        put(5200, 40);
        put(5210, 42);
        put(5220, 44);
        put(5230, 46);
        put(5240, 48);
        put(5250, 50);
        put(5260, 52);
        put(5270, 54);
        put(5280, 56);
        put(5290, 58);
        put(5300, 60);
        put(5310, 62);
        put(5320, 64);
        put(5340, 68);
        put(5480, 96);
        put(5500, 100);
        put(5510, 102);
        put(5520, 104);
        put(5530, 106);
        put(5540, 108);
        put(5550, 110);
        put(5560, 112);
        put(5570, 114);
        put(5580, 116);
        put(5590, 118);
        put(5600, 120);
        put(5610, 122);
        put(5620, 124);
        put(5630, 126);
        put(5640, 128);
        put(5660, 132);
        put(5670, 134);
        put(5680, 136);
        put(5690, 138);
        put(5700, 140);
        put(5710, 142);
        put(5720, 144);
        put(5745, 149);
        put(5755, 151);
        put(5765, 153);
        put(5775, 155);
        put(5785, 157);
        put(5795, 159);
        put(5805, 161);
        put(5825, 165);
        put(5845, 169);
        put(5865, 173);
    }};

}
