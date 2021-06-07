package com.example.oving8;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.oving8.datatypes.Difficulty;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

/*
    Displays main menu where we can change language, start a new game or view instructions (also available from options menu?
 */
public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    static String TAG = "MDT_MainActivity";
    Spinner difficultySelector;
    Difficulty currentDifficulty = Difficulty.MEDIUM;
    Menu cachedMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        createDifficultySelection();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    void createDifficultySelection(){
        difficultySelector = findViewById(R.id.spinner_difficulty_selector);
        ArrayList<String> elements = new ArrayList<>();
        elements.add(getString(R.string.difficulty_easy));
        elements.add(getString(R.string.difficulty_medium));
        elements.add(getString(R.string.difficulty_hard));
        ArrayAdapter<String> spinnerElements = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, elements);
        difficultySelector.setAdapter(spinnerElements);
        difficultySelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentDifficulty = Difficulty.values()[i];
                Log.d(TAG, "onItemSelected: difficulty: " + currentDifficulty);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });;
        Log.d(TAG, "createDifficultySelection: " + spinnerElements.getItem(0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dotmenu, menu);
        cachedMenu = menu;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Map<String, ?>  prefs = sharedPreferences.getAll();
        if(prefs.containsKey("language")){
            setLanguage((String)prefs.get("language"));
        }
        else{
            Locale locale = getResources().getConfiguration().locale;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("language", locale.getCountry());
            setLanguage(locale.getLanguage());
            editor.apply();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d(TAG, "onOptionsItemSelected: " + id);

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if(id == R.id.instructions_settings){
            Intent intent = new Intent(this, InstructionActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBtnPlayClick(View view){
        Log.d(TAG, "onBtnPlayClick: ");
        Intent intent = new Intent("com.example.oving8.SudokuActivity");
        intent.putExtra("play", true);
        intent.putExtra("difficulty", currentDifficulty.value);
        startActivity(intent);
    }

    public void obBtnCreateBoardClick(View view){
        Log.d(TAG, "obBtnCreateBoardClick: ");
        Intent intent = new Intent("com.example.oving8.SudokuActivity");
        intent.putExtra("play", false);
        intent.putExtra("difficulty", currentDifficulty.value);
        startActivity(intent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Log.d(TAG, "onSharedPreferenceChanged: " + s);

        if (s.equals("language")) {
            Map<String, ?>  prefs = sharedPreferences.getAll();
            setLanguage((String)prefs.get("language"));
        }
    }

    public void setLanguage(String language){
        Log.d(TAG, "setLanguage: " + language);
        Locale locale = new Locale(language);
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, displayMetrics);

        //Update values in UI
        setContentView(R.layout.activity_main);
        createDifficultySelection();
        cachedMenu.removeItem(R.id.action_settings);
        cachedMenu.removeItem(R.id.instructions_settings);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dotmenu, cachedMenu);
    }

    public void onDestroy(){
        super.onDestroy();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}