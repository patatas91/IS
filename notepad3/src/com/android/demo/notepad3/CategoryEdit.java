/*
 * Copyright (C) 2008 Google Inc.
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

package com.android.demo.notepad3;


import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Clase que implementa metodos que permiten la edicion de categorias.
 * 
 * @author Fernando Aliaga y Cristian Simon
 *
 */
public class CategoryEdit extends Activity {
   
	/** caja de texto para indicar el asunto */
    private EditText mTitleText;
    /** caja de texto con el ID de la nota */
    private TextView mId;
    /** ID de la nota*/
    private Long mRowId;
    /** comunicacion con la base de datos*/
    private NotesDbAdapter mDbHelper;
    

    /**
     * Metodo para la edicion de una nota
     * 
     * @param savedInstanceState objeto tipo Bundle
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();        

        setContentView(R.layout.edit_categoria);
        setTitle(R.string.edit_categoria);

        mId = (TextView) findViewById (R.id.textView1);
        mTitleText = (EditText) findViewById(R.id.title);

        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ID_CATEGORIA);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ID_CATEGORIA)
									: null;
		}

		populateFields();

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }

        });
    }

    /**
     * Modifica los campos, titulo y cuerpo, de la categoria
     */
    @SuppressWarnings("deprecation")
	private void populateFields() {
        if (mRowId != null) {
            Cursor categoria = mDbHelper.fetchCategoria(mRowId);
            startManagingCursor(categoria);
            String s = String.valueOf(mRowId);
            mId.setText("ID: " + s);
            mTitleText.setText(categoria.getString(
            		categoria.getColumnIndexOrThrow(NotesDbAdapter.KEY_CATEGORIA)));
        }
        else {
        	mId.setText("***");
        }
    }

    /**
     * Guarda el estado
     * 
     * @param outState objeto tipo Bundle
     */
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(NotesDbAdapter.KEY_ID_CATEGORIA, mRowId);
    }

    /**
     * Pausa la ejecucion
     */
    protected void onPause() {
        super.onPause();
        saveState();
    }

    /**
     * Reanuda la ejecucion
     */
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    /**
     * Guarda los nuevos datos de la nota
     */
    private void saveState() {
        String title = mTitleText.getText().toString();

        if (mRowId == null) {
        	Toast toast = Toast.makeText(getApplicationContext(), 
					"Nueva categoria" + 
					"\n\nNombre: " + title		
        			, Toast.LENGTH_LONG);            
			toast.show();
            long id = mDbHelper.createCategoria(title);
            if (id > 0) {
                mRowId = id;
            }
        } else {
        	Toast toast = Toast.makeText(getApplicationContext(), 
					"Actualizando categoria" + 
					"\n\nNombre: " + title		
        			, Toast.LENGTH_LONG);            
			toast.show();
            mDbHelper.updateCategoria(mRowId, title);
        }
    }

}
