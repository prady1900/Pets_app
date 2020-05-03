/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.sql;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;

import android.database.Cursor;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import com.example.sql.data.PetsContract.PetEntry;
import com.example.sql.data.PetDBHelper;
import com.example.sql.R;

import com.example.sql.data.PetsContract;
import com.example.sql.data.PetsContract.PetEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PET_LOADER = 1;
    PetCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);


        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);





        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        final PetDBHelper mDBHelper = new PetDBHelper(this);
        ListView petListView = findViewById(R.id.list_view_pet);

        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);


        mCursorAdapter= new PetCursorAdapter(this,null);
        petListView.setAdapter(mCursorAdapter);


        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(CatalogActivity.this,EditorActivity.class);
                Uri currentPetUri = ContentUris.withAppendedId(PetEntry.CONTENT_URI,id);
//                intent.putExtra("NAME",);
                Cursor cursor = mCursorAdapter.getCursor();


                for (String s : cursor.getColumnNames()){
                    Log.i("ca",s);
                }
                int namecolumnIndex = cursor.getColumnIndex(PetsContract.PetEntry.COLUMN_PET_NAME);
                int breedColumnIndex = cursor.getColumnIndex(PetsContract.PetEntry.COLUMN_PET_BREED);
                int weightColumnIndex = cursor.getColumnIndex(PetsContract.PetEntry.COLUMN_PET_WEIGHT);
                int genderColumnIndex = cursor.getColumnIndex(PetsContract.PetEntry.COLUMN_PET_GENDER);






                String petName = cursor.getString(namecolumnIndex);
                String petBreed = cursor.getString(breedColumnIndex);
                String petWeight = cursor.getString(weightColumnIndex);
                int petGender = cursor.getInt(genderColumnIndex);




                Bundle bundle = new Bundle();
                bundle.putString("name",petName);
                bundle.putString("breed",petBreed);
                bundle.putString("weight",petWeight);
                bundle.putInt("gender",petGender);



                intent.setData(currentPetUri);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });

        LoaderManager.getInstance(this).initLoader(PET_LOADER,null,this);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */

    private void insertPet() {
        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);

        Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option

            case R.id.action_insert_dummy_data:
                insertPet();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(PetEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
        mCursorAdapter.swapCursor(null);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_WEIGHT,
                PetEntry.COLUMN_PET_GENDER};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                PetEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
