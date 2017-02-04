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

/**
 * 
 * Clase que genera una actividad para gestionar categorias.
 * Permite crear, borrar y editar categorias.
 * 
 * @author Fernando y Cristian
 *
 */
public class CategoryList extends ListActivity {
	
	/** Número asociado a la actividad 'CREATE' */
    private static final int ACTIVITY_CREATE=0;
    
    /** Número asociado a la actividad 'EDIT' */
    private static final int ACTIVITY_EDIT=1;

    /** Número asociado al botón "ADD CATEGORIA" */
    private static final int INSERT_ID = Menu.FIRST;
    
    /** Número asociado al botón ""BORRAR NOTA */
    private static final int DELETE_ID = Menu.FIRST + 1;

    /** Comunicación con base de datos */
    private NotesDbAdapter mDbHelper;
    
   

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_list);
        setTitle(R.string.category_list);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        
        fillData();
        registerForContextMenu(getListView());
    }

    /**
     * Rellena los datos de la lista a mostrar con las categorias existentes
     * en la base de datos.
     */
    @SuppressWarnings("deprecation")
	private void fillData() {
        Cursor categoriasCursor = mDbHelper.fetchAllCategorias();
        startManagingCursor(categoriasCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{NotesDbAdapter.KEY_CATEGORIA};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter categories = 
            new SimpleCursorAdapter(this, R.layout.category_row, categoriasCursor, from, to);
        setListAdapter(categories);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert_category);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case INSERT_ID:
                createCategoria();
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete_category);
        
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                Cursor categoria = mDbHelper.fetchCategoria(info.id);
            	String nombreCategoria = categoria.getString(categoria.getColumnIndexOrThrow(NotesDbAdapter.KEY_CATEGORIA));
            	mDbHelper.deleteCategoria(nombreCategoria);
                fillData();
                return true;  
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Método para lanzar una actividad de creación de nota.
     */
    private void createCategoria() {
        Intent i = new Intent(this, CategoryEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, CategoryEdit.class);
        i.putExtra(NotesDbAdapter.KEY_ID_CATEGORIA, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
}
