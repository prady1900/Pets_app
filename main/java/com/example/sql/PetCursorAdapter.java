package com.example.sql;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.sql.data.PetsContract;

public class PetCursorAdapter extends CursorAdapter {
    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTV = view.findViewById(R.id.name);
        TextView summaryTV = view.findViewById(R.id.summary);


        int namecolumnIndex = cursor.getColumnIndex(PetsContract.PetEntry.COLUMN_PET_NAME);
        int breedColumnIndex = cursor.getColumnIndex(PetsContract.PetEntry.COLUMN_PET_BREED);



        String petName = cursor.getString(namecolumnIndex);
        String petBreed = cursor.getString(breedColumnIndex);


        if (TextUtils.isEmpty(petBreed)) {
            petBreed = context.getString(R.string.unknown_breed);
        }

        nameTV.setText(petName);
        summaryTV.setText(petBreed);



    }

    @Override
    public Cursor getCursor() {
        return super.getCursor();
    }
}
