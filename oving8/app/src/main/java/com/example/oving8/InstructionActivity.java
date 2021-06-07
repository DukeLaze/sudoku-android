package com.example.oving8;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Locale;
import java.util.Map;

public class InstructionActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    Menu cachedMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instruction_activity);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        Map<String, ?> prefs = sharedPreferences.getAll();
        String lang = (String)prefs.get("language");
        generateInstructions(lang);
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dotmenu, menu);
        cachedMenu = menu;
        cachedMenu.removeItem(R.id.instructions_settings);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            Log.d("MDT", "onOptionsItemSelected: home pressed");
            onBackPressed();
            return true;
        }
        else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Log.d("MDT", "onSharedPreferenceChanged: SettingsFragment");
        if (s.equals("language")) {
            Map<String, ?> prefs = sharedPreferences.getAll();
            setLanguage((String)prefs.get("language"));
        }
    }

    public void setLanguage(String language){
        Locale locale = new Locale(language);
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, displayMetrics);

        //Update UI
        cachedMenu.removeItem(R.id.action_settings);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dotmenu, cachedMenu);
        cachedMenu.removeItem(R.id.instructions_settings);
        generateInstructions(language);
    }

    void generateInstructions(String language){
        language = language.toLowerCase();
        TextView textView = findViewById(R.id.tv_instructions);
        /*
            Tried VERY hard to place graphics inside of the instructions, but ultimately I couldn't get it to work properly.
            (Sometimes setSpan(new ImageSpan(Drawable)) would appear but be partly off-screen, be invisible, or not take up space at all)
         */
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if(language.equals("no")){
            builder.append("Velkommen til Awesomium Sudoku!\n\n");
            builder.append("Målet i (9x9) Sudoku er å fylle alle rutene med tall fra 1 til 9, " +
                    "samtidig som man sørger for å ha tallene 1 til 9 i både alle radene og kolonnene.\n\n");
            builder.append("Det blir ikke rett om det finnes to innslag av samme tall i gitt kolonne, rad, eller rute.\n\n");
            builder.append("Klikk på et felt for å endre verdien. Du kan huke av \"Marker som usikker\" for å gi feltet en blå bakgrunn" +
                    " for å minne deg selv på at du ikke er helt sikker på om dette tallet er rett. \n\n");
            builder.append("Du kan slette verdien fra et felt ved å klikke på feltet og så trykk \"Slett\".\n\n");
            builder.append("Feltene som er markert grønt er en del av spillbrettet som er ferdig utfylt og kan ikke endres på. (Disse er rett)\n\n");
            builder.append("Du vinner spillet dersom alle 81 rutene er utfylt og alle tallene mellom 1 og 9 dukker opp unikt i alle rutene, kolonnene og radene.");
            builder.append("\n\nLykke til!");
        }
        else{
            builder.append("Welcome to Awesomium Sudoku!\n\n");
            builder.append("The goal in (9x9) Sudoku is to fill the boxes (3x3 grid) with numbers ranging from 1 to 9, " +
                    "while making sure that the numbers 1 to 9 also appear in all rows and columns.\n\n");
            builder.append("The board is not correct if the same number occurs more than once in a given column, row or box.\n\n");
            builder.append("Click on a square in order to change the value in that square. Check the \"Mark as unsure\" box in order to give" +
                    " the square a blue background color to remind yourself that you are unsure about that value in that square.\n\n");
            builder.append("You can delete the value in a square by clicking the square and clicking \"erase\".\n\n");
            builder.append("The squares that are marked green is part of the board and are prefilled with the correct value. These cannot be changed.\n\n");
            builder.append("In order to win, all 81 squares must be filled, and all numbers (1-9) must uniquely appear in each box, column and row.\n\n");
            builder.append("Have fun!");
        }
        textView.setText(builder);
        textView.setMovementMethod(new ScrollingMovementMethod());
    }

    public void onDestroy(){
        super.onDestroy();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}
