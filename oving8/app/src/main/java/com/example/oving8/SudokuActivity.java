package com.example.oving8;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.oving8.datatypes.ButtonAdapter;
import com.example.oving8.datatypes.Difficulty;
import com.example.oving8.datatypes.PieceClickHandler;
import com.example.oving8.internal.FileIO;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/*
    Used to display/create the game board/play the game
 */

public class SudokuActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    int[][] board = new int[9][9];
    boolean[][] unsure = new boolean[9][9];
    int selectedCell = -1;
    int selectedButton = -1;
    ButtonAdapter[] boardButtons = new ButtonAdapter[9];
    ButtonAdapter numberSelector;
    CheckBox numberSelectorCheckBox;
    LinearLayout llNumberSelector;
    LinearLayout llWin;

    boolean playing;
    Difficulty gameDifficulty;

    FileIO fileHandler;

    Menu cachedMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        fileHandler = new FileIO(getApplicationContext());
        Intent intent = getIntent();
        gameDifficulty = Difficulty.values()[intent.getExtras().getInt("difficulty")];
        playing = intent.getExtras().getBoolean("play");
        Log.d("MDT", "onCreate: gameDifficulty: " + gameDifficulty + " , playing?" + (playing ? "yes" : "no") );

        if(playing){
            setContentView(R.layout.sudoku_activity);
            board = fileHandler.getBoard(gameDifficulty);
        }
        else{
            setContentView(R.layout.sudoku_activity_create);
            updateDifficultyTextCreateMode();
            for(int i = 0; i < 9; i++){
                for(int j = 0; j < 9; j++){
                    board[i][j] = 0;
                }
            }
        }
        llNumberSelector = findViewById(R.id.ll_under_board);
        llWin = findViewById((R.id.ll_win));

        createBoard();
        createNumberSelector();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dotmenu, menu);
        cachedMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d("MDT", "onOptionsItemSelected: " + id);

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if(id == android.R.id.home){
            Log.d("MDT", "onOptionsItemSelected: home pressed");
            onBackPressed();
            return true;
        }
        else if(id == R.id.instructions_settings){
            Intent intent = new Intent(this, InstructionActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void createBoard(){
        android.view.Display display = ((android.view.WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        //Scale gameboard
        LinearLayout llboard = findViewById(R.id.ll_board);
        ViewGroup.LayoutParams boardLayout = llboard.getLayoutParams();
        boardLayout.height = (int)(display.getHeight()*0.7);
        llboard.setLayoutParams(boardLayout);
        int width = display.getWidth()/10;
        int height = (int)(display.getHeight()*0.65)/10;
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(width , height);

        //Fetch 9 cells
        int[] cellIds = {
                R.id.gv_0_0, R.id.gv_0_1, R.id.gv_0_2,
                R.id.gv_1_0, R.id.gv_1_1, R.id.gv_1_2,
                R.id.gv_2_0, R.id.gv_2_1, R.id.gv_2_2
        };
        ArrayList<GridView> cells = new ArrayList<>();

        for(int i = 0; i < cellIds.length; i++){
            cells.add((GridView)findViewById(cellIds[i]));
            //Create 3 cells (3x3 buttons)
            ArrayList<Button> buttons = new ArrayList<Button>();
            for(int j = 1; j < 10; j++){
                Button b = new Button(getApplicationContext());
                b.setTextColor(getColor(R.color.white));
                // The player should fill these out
                if(board[i][j-1] == 0){
                    b.setBackground(getDrawable(R.drawable.piece_style));
                    b.setOnClickListener(new PieceClickHandler(this, i, j-1));

                    b.setText("");
                }
                // These are correct and part of the board initial state
                else{
                    b.setBackground(getDrawable(R.drawable.piece_correct_style));
                    b.setEnabled(false);
                    b.setText(Integer.toString(board[i][j-1]));
                }
                AbsListView.LayoutParams buttonParams = new AbsListView.LayoutParams(width , height);
                b.setLayoutParams(buttonParams);
                buttons.add(b);
            }
            GridView cell = cells.get(i);
            cell.setNumColumns(3);
            cell.setBackground(getDrawable(R.drawable.cell_border));

            cell.setHorizontalSpacing(5);
            cell.setVerticalSpacing(5);

            ViewGroup.LayoutParams cellLayout = cell.getLayoutParams();
            cellLayout.width = (display.getWidth()/3)-(int)(display.getWidth()*0.01);
            cell.setLayoutParams(cellLayout);


            boardButtons[i] = new ButtonAdapter(getApplicationContext(), buttons);
            cell.setAdapter(boardButtons[i]);
        }
        //Fill cells
    }

    void createNumberSelector(){
        GridView gvNumberSelector = findViewById(R.id.gv_number_selector);
        numberSelectorCheckBox = findViewById(R.id.checkbox_number_selector);
        gvNumberSelector.setNumColumns(5);
        ArrayList<Button> buttonArrayList = new ArrayList<>();
        for(int i = 1; i < 10; i++){
            Button b = new Button(getApplicationContext());
            b.setText(Integer.toString(i));
            b.setBackground(getDrawable(R.drawable.numberselector_style));
            b.setTextColor(getColor(R.color.white));
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button button = (Button)view;
                    updatePieceSelected(button.getText().toString());
                }
            });
            buttonArrayList.add(b);
        }
        Button b = new Button(getApplicationContext());
        b.setText(getString(R.string.btn_erase));
        b.setBackground(getDrawable(R.drawable.numberselector_style));
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button = (Button)view;
                updatePieceSelected(getString(R.string.btn_erase));
            }
        });
        b.setTextColor(getColor(R.color.white));
        buttonArrayList.add(b);
        numberSelector = new ButtonAdapter(getApplicationContext(), buttonArrayList);
        gvNumberSelector.setAdapter(numberSelector);
    }

    public void onPieceSelected(int cellIndex, int buttonIndex){
        if(playing){
            llNumberSelector.setVisibility(View.VISIBLE);
        }
        else{
            findViewById(R.id.gv_number_selector).setVisibility(View.VISIBLE);
        }
        //First selected piece of the game
        if(selectedButton == -1 && selectedCell == -1){
            selectedCell = cellIndex;
            selectedButton = buttonIndex;
        }
        //We've selected a new piece, clean up the old one
        else if(selectedCell != cellIndex || selectedButton != buttonIndex){
            Button b = (Button)boardButtons[selectedCell].getItem(selectedButton);
            b.setSelected(false);
            String bText = b.getText().toString();
            deselectOldNumberSelector(bText);
            if(playing){
                numberSelectorCheckBox.setChecked(false);
            }
            selectedCell = cellIndex;
            selectedButton = buttonIndex;
        }

        Button b = (Button)boardButtons[cellIndex].getItem(buttonIndex);
        b.setSelected(true);
        String bText = b.getText().toString();
        Integer choice;
        Button selected;
        if(bText.equals("")){
            selected = (Button)numberSelector.getItem(9);
        }
        else{
            choice = Integer.parseInt(bText);
            selected = (Button)numberSelector.getItem(choice-1);
        }
        selected.setSelected(true);
        if(playing){
            if(unsure[selectedCell][selectedButton]){
                numberSelectorCheckBox.setChecked(true);
            }
            else{
                numberSelectorCheckBox.setChecked(false);
            }
        }
    }

    void updatePieceSelected(String pieceText){
        Button b = (Button)boardButtons[selectedCell].getItem(selectedButton);
        String bText = b.getText().toString();
        //Deselect old numberSelector
        deselectOldNumberSelector(bText);
        //Mark new numberSelector as selected
        if(pieceText.equals(getString(R.string.btn_erase))){
            Button selected = (Button)numberSelector.getItem(9);
            selected.setSelected(true);
            //Can't be unsure about clearing a piece
            //Slightly roundabout way of doing it?
            unsure[selectedCell][selectedButton] = false;
            if(playing)
            {
                numberSelectorCheckBox.setChecked(false);
                onCheckBoxToggle(numberSelectorCheckBox);
            }
            board[selectedCell][selectedButton] = 0;
            b.setText("");
        }
        else{
            Integer number = Integer.parseInt(pieceText);
            Button selected = (Button)numberSelector.getItem(number-1);
            selected.setSelected(true);
            board[selectedCell][selectedButton] = number;
            b.setText(pieceText);
        }

        if(playing){
            llNumberSelector.setVisibility(View.INVISIBLE);
        }
        else{
            findViewById(R.id.gv_number_selector).setVisibility(View.INVISIBLE);
        }
        Log.d("MDT", "updatePieceSelected: " + (complete() ? "Done" : "Not done"));
        if(boardFull()){
            if(complete()){
                llNumberSelector.setVisibility(View.GONE);
                llWin.setVisibility(View.VISIBLE);
                for(int i = 0; i < 9; i++){
                    for(int j = 0; j < 9; j++){
                        ((Button)boardButtons[i].getItem(j)).setBackground(getDrawable(R.drawable.piece_correct_style));
                    }
                }
            }
            else{
                Toast.makeText(this, getString(R.string.board_full_wrong), Toast.LENGTH_LONG).show();
            }
        }
    }

    void deselectOldNumberSelector(String currentText){
        if(currentText.equals("")){
            Button oldSelected = (Button)numberSelector.getItem(9);
            oldSelected.setSelected(false);
        }
        else{
            Button oldSelected = (Button)numberSelector.getItem(Integer.parseInt(currentText)-1);
            oldSelected.setSelected(false);
        }
    }

    public void onCheckBoxToggle(View view){
        CheckBox cb = (CheckBox)view;
        Button b = (Button)boardButtons[selectedCell].getItem(selectedButton);
        //Change background of selected button depending on checkbox
        if(cb.isChecked()){
            b.setBackground(getDrawable(R.drawable.piece_unsure_style));
            unsure[selectedCell][selectedButton] = true;
        }
        else{
            b.setBackground(getDrawable(R.drawable.piece_style));
            unsure[selectedCell][selectedButton] = false;
        }
    }

    boolean boardFull(){
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                if(board[i][j] == 0){
                    return false;
                }
            }
        }
        return true;
    }

    // Checks that the board is filled up correctly.
    boolean complete(){
        // Cell completion (Due to one row being split into 3 rows x 3 columns in display)
        for(int i = 0; i < 9; i++){
            ArrayList<Integer> values = new ArrayList<>();
            for(int j = 0; j < 9; j++){
                if(board[i][j] != 0 && !values.contains(board[i][j])){
                    values.add(board[i][j]);
                }
            }
            Log.d("MDT", "Cellcheck:  " + values.toString() + ", i: " + i);
            if(values.size() != 9){
                Log.d("MDT", "complete: row.size() " + values.size() + ", i: " + i);
                return false;
            }
        }

        // Check rows & columns
        ArrayList<Integer> row1 = new ArrayList<>();
        ArrayList<Integer> row2 = new ArrayList<>();
        ArrayList<Integer> row3 = new ArrayList<>();
        ArrayList<Integer> columns1 = new ArrayList<>();
        ArrayList<Integer> columns2 = new ArrayList<>();
        ArrayList<Integer> columns3 = new ArrayList<>();
        //Cells
        for(int i = 0; i < 9; i++){
            //Check rows
            for(int j = 0; j < 3; j++){
                if(board[i][j] != 0 && !row1.contains(board[i][j])){
                    row1.add(board[i][j]);
                }
                if(board[i][j+3] != 0 && !row2.contains(board[i][j+3])){
                    row2.add(board[i][j+3]);
                }
                if(board[i][j+6] != 0 && !row3.contains(board[i][j+6])){
                    row3.add(board[i][j+6]);
                }
            }
            //Check columns
            int[] cell_column_indexes = {0, 3, 6, 1, 4, 7, 2, 5, 8};
            for(int j = 0; j < 3; j++){
                if(board[cell_column_indexes[i]][j] != 0 && !columns1.contains(board[i][j])){
                    columns1.add(board[i][j]);
                }
                if(board[cell_column_indexes[i]][j+1] != 0 && !columns2.contains(board[i][j+3])){
                    columns2.add(board[i][j+3]);
                }
                if(board[cell_column_indexes[i]][j+2] != 0 && !columns3.contains(board[i][j+6])){
                    columns3.add(board[i][j+6]);
                }
            }
            //Done with 3 rows & columns
            if((i+1)%3 == 0){
                if(!(row1.size() == 9) || !(row2.size() == 9) || !(row3.size() == 9) ||
                    !(columns1.size() == 9) || !(columns2.size() == 9) || !(columns3.size() == 9)){
                    return false;
                }
                else{
                    row1.clear();
                    row2.clear();
                    row3.clear();
                    columns1.clear();
                    columns2.clear();
                    columns3.clear();
                }
            }
        }
        return true;
    }

    public void onAddBoardClick(View view){
        boolean ok = fileHandler.saveBoard(gameDifficulty, board);
        if(ok){
            Toast.makeText(this, getString(R.string.save_ok), Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, getString(R.string.save_not_ok), Toast.LENGTH_LONG).show();
        }
        finish();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("language")) {
            Map<String, ?> prefs = sharedPreferences.getAll();
            setLanguage((String)prefs.get("language"));
        }
    }

    void updateDifficultyTextCreateMode(){
        TextView difficultyText = findViewById(R.id.tv_sudoku_create_difficulty);
        switch (gameDifficulty){
            case EASY:
                difficultyText.setText(getString(R.string.difficulty_word) + " " + getString(R.string.difficulty_easy));
                break;
            case MEDIUM:
                difficultyText.setText(getString(R.string.difficulty_word) + " " + getString(R.string.difficulty_medium));
                break;
            case HARD:
                difficultyText.setText(getString(R.string.difficulty_word) + " " + getString(R.string.difficulty_hard));
                break;
        }
    }

    public void setLanguage(String language){
        Locale locale = new Locale(language);
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, displayMetrics);
        //Update values in UI
        if(playing)
        {
            ((TextView)findViewById(R.id.tv_unsure)).setText(R.string.mark_unsure);
        }
        else{
            ((TextView)findViewById(R.id.btn_add_board)).setText(R.string.btn_save_board);
            updateDifficultyTextCreateMode();
        }
        cachedMenu.removeItem(R.id.action_settings);
        cachedMenu.removeItem(R.id.instructions_settings);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dotmenu, cachedMenu);
        createNumberSelector();
    }

    public void onDestroy(){
        super.onDestroy();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}
