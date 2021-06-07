package com.example.oving8.datatypes;

import android.util.Log;
import android.view.View;

import com.example.oving8.SudokuActivity;

public class PieceClickHandler implements View.OnClickListener {
    int cellIndex;
    int pieceIndex;
    SudokuActivity callbackActivity;
    public PieceClickHandler(SudokuActivity callbackActivity, int cellIndex, int pieceIndex){
        this.callbackActivity = callbackActivity;
        this.cellIndex = cellIndex;
        this.pieceIndex = pieceIndex;
    }
    @Override
    public void onClick(View view) {
        Log.d("MDT", "onClick: cell: " + cellIndex + ", piece: " + pieceIndex);
        callbackActivity.onPieceSelected(cellIndex, pieceIndex);
    }
}
