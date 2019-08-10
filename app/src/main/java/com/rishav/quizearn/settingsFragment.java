package com.rishav.quizearn;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.widget.Toast;

import androidx.annotation.Nullable;

import static android.content.Context.MODE_PRIVATE;

public class settingsFragment extends PreferenceFragment {
    SwitchPreference music;
    SharedPreferences settings;
    boolean b_music;
    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        settings = getActivity().getSharedPreferences("MUSIC_SETTING",MODE_PRIVATE);
        b_music=settings.getBoolean("MUSIC",true);

        music= (SwitchPreference) findPreference("switch_musicPreference");

        music.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean musicOn = (boolean) newValue;
                if (musicOn){
                    b_music=true;
                    saveSettingsBoolean("MUSIC",b_music);
                }else {
                    b_music=false;
                    saveSettingsBoolean("MUSIC",b_music);
                }
                return true;
            }
        });
    }
    private void saveSettingsBoolean(String music, boolean b_music) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(music, b_music);
        editor.apply();
    }

}
