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

import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Clase que implementa metodos que permiten la edicion de notas
 * 
 * @author Fernando Aliaga y Cristian Simon
 *
 */
public class NoteEdit extends Activity {

	/** caja de texto para indicar el asunto */
	private EditText mTitleText;
	/** caja de texto para el cuerpo de la nota */
	private EditText mBodyText;
	/** caja de texto con el ID de la nota */
	private TextView mId;
	/** numero de identificacion de la nota*/
	private Long mRowId;
	/** comunicacion con la base de datos*/
	private NotesDbAdapter mDbHelper;
	/** spinner de categorias */
	private Spinner spinner;

	/**
	 * Metodo para la edicion de una nota
	 * 
	 * @param savedInstanceState objeto tipo Bundle
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new NotesDbAdapter(this);
		mDbHelper.open();

		setContentView(R.layout.note_edit);
		setTitle(R.string.edit_note);

		mId = (TextView) findViewById (R.id.textView1);
		mTitleText = (EditText) findViewById(R.id.title);
		mBodyText = (EditText) findViewById(R.id.body);
		spinner = (Spinner) findViewById(R.id.spinner1);

		addItemsOnSpinner();

		Button confirmButton = (Button) findViewById(R.id.confirm);

		mRowId = (savedInstanceState == null) ? null :
			(Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID)
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
	 * Añade las categorias al spinner de seleccion
	 */
	public void addItemsOnSpinner() {

		spinner = (Spinner) findViewById(R.id.spinner1);
		List<String> list = mDbHelper.listaCategorias();
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);

	}


	/**
	 * Modifica los campos, titulo y cuerpo, de la nota
	 */
	@SuppressWarnings("deprecation")
	private void populateFields() {
		if (mRowId != null) {
			Cursor note = mDbHelper.fetchNote(mRowId);
			startManagingCursor(note);
			String s = String.valueOf(mRowId);
			String categoria = note.getString(
					note.getColumnIndexOrThrow(NotesDbAdapter.KEY_CATEGORIA_NOTA));
			if(categoria==null){
				categoria = "Sin categoria";
			}
			mId.setText("ID: " + s + " - Categoria: " + categoria);
			mTitleText.setText(note.getString(
					note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
			mBodyText.setText(note.getString(
					note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
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
		outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
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
	@SuppressWarnings("deprecation")
	private void saveState() {
		String title = mTitleText.getText().toString();
		String body = mBodyText.getText().toString();
		String categoria = String.valueOf(spinner.getSelectedItem());
		if(title.length()>0){
			if (mRowId == null) {
				long id;
				if(categoria.equals("Sin categoria") || 
						categoria.equals("Sin cambio de categoría / Por defecto")){
					id = mDbHelper.createNote(title, body, null);
				}
				else{
					id = mDbHelper.createNote(title, body, categoria);

				}
				if (id > 0) {
					mRowId = id;
					Toast.makeText(this,
							"Nueva nota " +	
									"\n\nTitulo: " + title +
									"\nCuerpo: " + body +
									"\nCategoria: " + categoria ,
									Toast.LENGTH_SHORT).show();					
				}
				else{
					Toast.makeText(this,
							"Error al añadir nota" ,
							Toast.LENGTH_SHORT).show();
				}
			} else {
				boolean exito;
				
				if(categoria.equals("Sin categoria")){
					exito = mDbHelper.updateNote(mRowId, title, body, null);
				}
				else if(categoria.equals("Sin cambio de categoría / Por defecto")){
					Cursor note = mDbHelper.fetchNote(mRowId);
					startManagingCursor(note);
					categoria = note.getString(
							note.getColumnIndexOrThrow(NotesDbAdapter.KEY_CATEGORIA_NOTA));
					exito = mDbHelper.updateNote(mRowId,title, body, categoria);

				}
				else{
					exito = mDbHelper.updateNote(mRowId, title, body, categoria);
				}
				if(exito){
					Toast.makeText(this,
							"Nota actualizada " +
									"\n\nTitulo: " + title +
									"\nCuerpo: " + body +
									"\nCategoria: " + categoria ,
									Toast.LENGTH_SHORT).show();
				}
				else {
					Toast.makeText(this,
							"Error al actualizar la nota" ,
									Toast.LENGTH_SHORT).show();
				}
			}
		}
		else {
			Toast.makeText(this,
					"Nota no guardada. La nota no tiene título." ,
					Toast.LENGTH_SHORT).show();
		}
	}
}


