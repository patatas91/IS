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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

/**
 * Clase que implementa metodos que permiten la selección de una categoria 
 * mediante un spinner.
 * 
 * @author Fernando Aliaga y Cristian Simon
 *
 */
public class CategorySelect extends Activity {


	/** comunicacion con la base de datos*/
	private NotesDbAdapter mDbHelper;
	/** spinner de categorias */
	private Spinner spinner;
	/** Categoria seleccionada para realizar el filtro */
	private String categoria;

	/**
	 * Metodo para la seleccion de una categoria
	 * 
	 * @param savedInstanceState objeto tipo Bundle
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new NotesDbAdapter(this);
		mDbHelper.open();        

		setContentView(R.layout.category_select);
		setTitle(R.string.select_category);

		spinner = (Spinner) findViewById(R.id.spinner1);

		addCategoriasSpinner();

		Button confirmButton = (Button) findViewById(R.id.confirm);

		confirmButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				categoria = String.valueOf(spinner.getSelectedItem());
				if(categoria.equals("Todas las categorias")){
					categoria="";
				}
				else if (categoria.equals("Sin cambio")){
					categoria="-1";
				}
				/*
				 * Enviamos la categoria seleccionada a la actividad 'Notepadv3'.
				 */
				Intent data = new Intent();
				data.setData(Uri.parse(categoria));
				setResult(RESULT_OK, data);
				finish();
			}

		});
	}

	/**
	 * Añade las categorias al spinner.
	 */
	public void addCategoriasSpinner() {
		spinner = (Spinner) findViewById(R.id.spinner1);
		List<String> list = mDbHelper.listaCategoriasFiltro();
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
	}

	/**
	 * Guarda el estado
	 * 
	 * @param outState objeto tipo Bundle
	 */
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
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
	}

	/**
	 * Guarda los nuevos datos de la categoria
	 */
	private void saveState() {
		categoria = String.valueOf(spinner.getSelectedItem());
	}

}


