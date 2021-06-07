package com.example.oving8.datatypes;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;

public class ButtonAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Button> buttons;

    public ButtonAdapter(Context context, ArrayList<Button> buttons){
        this.context = context;
        this.buttons = buttons;
    }

    @Override
    public int getCount() {
        return buttons.size();
    }

    @Override
    public Object getItem(int i) {
        return buttons.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        return buttons.get(i);

    }
}
