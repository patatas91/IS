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


import com.android.demo.mail.MailAbstraction;
import com.android.demo.mail.MailAbstractionImpl;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;

/**
 * 
 * Clase que genera una aplicación para guardar notas.
 * Permite crear, borrar y editar notas. A su vez ofrece la 
 * opcion de enviar las notas por correo electrónico. 
 * 
 * @author Fernando y Cristian
 *
 */
@SuppressLint("NewApi")
public class Notepadv3 extends ListActivity {

	/** Número asociado a la actividad 'CREATE' */
	private static final int ACTIVITY_CREATE=0;

	/** Número asociado a la actividad 'EDIT' */
	private static final int ACTIVITY_EDIT=1;

	/** Número asociado a la actividad 'EDIT' */
	private static final int ACTIVITY_CATEGORIA=2;

	/** Número asociado a la actividad 'SELECT' */
	private static final int ACTIVITY_SELECT=3;

	/** Número asociado al botón "ADD NOTE" */
	private static final int INSERT_ID = Menu.FIRST;

	/** Número asociado al botón ""BORRAR NOTA */
	private static final int DELETE_ID = Menu.FIRST + 1;

	/** Número asociado al botón "MANDAR MAIL" */
	private static final int MAIL_ID = Menu.FIRST + 2;

	/** Número asociado al botón "MENU CATEGORIAS" */
	private static final int MENU_CATEGORIAS_ID = Menu.FIRST + 3;

	/** Número asociado al botón "MENU CATEGORIAS" */
	private static final int MENU_ORDENAR_ID = Menu.FIRST + 4;

	/** Número asociado al botón "MENU FILTRADO" */
	private static final int MENU_FILTRAR = Menu.FIRST + 5;
	
	/** Número asociado al botón "MENU PRUEBAS" */
	private static final int MENU_PRUEBAS = Menu.FIRST + 6;

	/** Comunicación con base de datos */
	private NotesDbAdapter mDbHelper;

	/** Objeto MailAbstraction que gestiona el envio de notas por mail */
	private MailAbstraction mailAbs;

	/** Objeto de tipo boolean que indica el tipo de ordenacion 
	 * (true->Titulo, false->Categoria).
	 */
	private boolean TituloCategoria = true;

	/**
	 * Objeto de tipo String que indica la categoria por la cual se filtra la
	 * lista de notas. Si dicho valor es "", es decir, ningún caracter, la lista
	 * mostrara todas las notas sin filtrado.
	 */
	private String filtroCategorias;
	
	/**
	 * Objeto que genera la carga de pruebas.
	 */
	private Test tests;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		filtroCategorias = "";
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notes_list);
		mDbHelper = new NotesDbAdapter(this);
		mDbHelper.open();
		mailAbs = new MailAbstractionImpl(this);
		fillData();
		registerForContextMenu(getListView());
	}

	/**
	 * Rellena los datos de la lista a mostrar con las notas existentes
	 * en la base de datos.
	 */
	@SuppressWarnings("deprecation")
	private void fillData() {
		Cursor notesCursor = mDbHelper.fetchAllNotes(filtroCategorias,TituloCategoria);
		startManagingCursor(notesCursor);

		// Create an array to specify the fields we want to display in the list (only TITLE)
		String[] from = new String[]{NotesDbAdapter.KEY_TITLE};

		// and an array of the fields we want to bind those fields to (in this case just text1)
		int[] to = new int[]{R.id.text1};

		// Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter notes = 
				new SimpleCursorAdapter(this, R.layout.notes_row, notesCursor, from, to);
		setListAdapter(notes);

	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, R.string.menu_insert);
		menu.add(0, MENU_CATEGORIAS_ID, 1, R.string.menu_categorias);
		menu.add(0, MENU_ORDENAR_ID, 2, R.string.menu_ordenar);
		menu.add(0, MENU_FILTRAR, 3, R.string.select_category);
		menu.add(0, MENU_PRUEBAS, 4, R.string.pruebas);

		return true;
	}



	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
		case INSERT_ID:
			createNote();
			return true;
		case MENU_CATEGORIAS_ID:
			lanzarMenuCategorias();
			return true;
		case MENU_ORDENAR_ID:
			ordenarPor();
			fillData();
			return true;
		case MENU_FILTRAR:
			filtrarPor();
			fillData();
			return true;
		case MENU_PRUEBAS:
			tests = new Test(mDbHelper);
			int errores = tests.cargarNotas(1000);
			Toast toast = Toast.makeText(getApplicationContext(), 
					"Errores al añadir 1000 notas = " + errores , 
					Toast.LENGTH_LONG);    
			toast.show();
			/*
			long longitud = tests.longitudNota();
			Toast toast = Toast.makeText(getApplicationContext(), 
					"Longitud máxima de nota = " + longitud , 
					Toast.LENGTH_LONG);  
			toast.show();
			fillData();
			*/
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	/**
	 * Método auxiliar para la opcion "Filtrar por"
	 */
	private void filtrarPor() {
		Intent i = new Intent(this, CategorySelect.class);
		startActivityForResult(i, ACTIVITY_SELECT);
	}

	/**
	 * Método auxiliar para la opcion "Ordenar por"
	 */
	private void ordenarPor() {
		if(TituloCategoria==true){	
			TituloCategoria=false;
			Toast toast = Toast.makeText(getApplicationContext(), 
					"Ordenado por categoria", Toast.LENGTH_LONG);            
			toast.show();
		}
		else{	
			TituloCategoria=true;
			Toast toast = Toast.makeText(getApplicationContext(), 
					"Ordenado por título", Toast.LENGTH_LONG);            
			toast.show();
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
		menu.add(0, MAIL_ID, 1, R.string.menu_mail);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			mDbHelper.deleteNote(info.id);
			fillData();
			return true;
		case MAIL_ID:
			AdapterContextMenuInfo infoMail = (AdapterContextMenuInfo) item.getMenuInfo();
			Cursor note = mDbHelper.fetchNote(infoMail.id);
			String titulo = note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE));
			String cuerpo = note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY));
			mailAbs.send(titulo, cuerpo);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * Método para lanzar una actividad de creación de nota.
	 */
	private void createNote() {
		Intent i = new Intent(this, NoteEdit.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	/**
	 * Método para lanzar una actividad de creación de nota.
	 */
	private void lanzarMenuCategorias() {
		Intent i = new Intent(this, CategoryList.class);
		startActivityForResult(i, ACTIVITY_CATEGORIA);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, NoteEdit.class);
		i.putExtra(NotesDbAdapter.KEY_ROWID, id);
		startActivityForResult(i, ACTIVITY_EDIT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if ((requestCode == ACTIVITY_SELECT) && (resultCode == RESULT_OK)){
			String categoria = intent.getDataString();
			if(categoria!="-1"){
				filtroCategorias = categoria;
				if(categoria.length()==0){
					Toast toast = Toast.makeText(getApplicationContext(), 
							"Mostrando todas las categorias" , Toast.LENGTH_LONG);            
					toast.show();
				}
				else{
					Toast toast = Toast.makeText(getApplicationContext(), 
							"Filtro categoria: " + categoria, Toast.LENGTH_LONG);            
					toast.show();
				}
			}
		}
		fillData();
	}
}
