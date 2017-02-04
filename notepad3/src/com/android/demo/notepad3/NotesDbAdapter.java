/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.android.demo.notepad3;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * 
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class NotesDbAdapter {

	public static final String KEY_TITLE = "title";
	public static final String KEY_BODY = "body";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_CATEGORIA_NOTA = "cat";
	public static final String KEY_CATEGORIA = "nombre_categoria";
	public static final String KEY_ID_CATEGORIA = "_id";

	private static final String TAG = "NotesDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	/**
	 * Database creation sql statement
	 */
	private static final String NOTAS_CREATE =
			"create table notes (_id integer primary key autoincrement, "
					+ "title text not null, body text not null, cat text);";
	private static final String CATEGORIAS_CREATE =
			"create table categorias (_id integer primary key autoincrement, "
					+ "nombre_categoria text not null);";

	private static final String DATABASE_NAME = "data";
	private static final String DATABASE_TABLE = "notes";
	private static final String DATABASE_CATEGORIAS = "categorias";
	private static final int DATABASE_VERSION = 13;

	private final Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(NOTAS_CREATE);
			db.execSQL(CATEGORIAS_CREATE);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS notes");
			db.execSQL("DROP TABLE IF EXISTS categorias");
			onCreate(db);
		}
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx the Context within which to work
	 */
	public NotesDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the notes database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException if the database could be neither opened or created
	 */
	public NotesDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}


	/**
	 * Create a new note using the title and body provided. If the note is
	 * successfully created return the new rowId for that note, otherwise return
	 * a -1 to indicate failure.
	 * 
	 * @param title the title of the note
	 * @param body the body of the note
	 * @return rowId or -1 if failed
	 */
	public long createNote(String title, String body, String cat) {
		if(title!=null && title.length()>0 && body!=null){
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_TITLE, title);
			initialValues.put(KEY_BODY, body);
			initialValues.put(KEY_CATEGORIA_NOTA, cat);

			return mDb.insert(DATABASE_TABLE, null, initialValues);
		}
		else return -1;
	}

	/**
	 * Metodo que crea una categoria.
	 */
	public long createCategoria(String title) {
		if(title!=null && title.length()>0){
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_CATEGORIA, title);
			return mDb.insert(DATABASE_CATEGORIAS, null, initialValues);
		}
		else return -1;
	}

	/**
	 * Delete the note with the given rowId
	 * 
	 * @param rowId id of note to delete
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteNote(long rowId) {
		if(rowId>0){
			return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
		}
		else return false;
	}


	/**
	 * Delete the category with the given id
	 * 
	 * @param id id of category to delete
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteCategoria(String categoria) {
		if(categoria.length()>0 && categoria!=null){
			mDb.execSQL("update notes set cat='null' where cat='" + categoria + "' ");
			mDb.execSQL("delete from categorias where nombre_categoria='" + categoria + "' ");
			return true;
		}
		else return false;

	}


	/**
	 * Return a Cursor over the list of all notes in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchAllNotes(String categoria, boolean orden) {
		String ordenLista;
		if(orden==true){
			ordenLista = KEY_TITLE;
		}
		else ordenLista = KEY_CATEGORIA_NOTA;	
		if(categoria.length()==0){
			return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
					KEY_BODY, KEY_CATEGORIA_NOTA}, null, null, null, null, ordenLista);
		}
		else{
			return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
					KEY_BODY, KEY_CATEGORIA_NOTA}, KEY_CATEGORIA_NOTA + "='" + categoria + "'", 
					null, null, null, ordenLista);
		}
	}


	/**
	 * Return a Cursor over the list of all notes in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchAllCategorias() {

		return mDb.query(DATABASE_CATEGORIAS, new String[] {KEY_ID_CATEGORIA, KEY_CATEGORIA}
		, null, null, null, null, null);
	}


	/**
	 * Return a Cursor positioned at the note that matches the given rowId
	 * 
	 * @param rowId id of note to retrieve
	 * @return Cursor positioned to matching note, if found
	 * @throws SQLException if note could not be found/retrieved
	 */
	public Cursor fetchNote(long rowId) throws SQLException {

		Cursor mCursor =
				mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
						KEY_TITLE, KEY_BODY, KEY_CATEGORIA_NOTA}, KEY_ROWID + "=" + rowId, null,
						null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/**
	 * Return a Cursor positioned at the categoria that matches the given rowId
	 * 
	 * @param rowId id of note to retrieve
	 * @return Cursor positioned to matching note, if found
	 * @throws SQLException if note could not be found/retrieved
	 */
	public Cursor fetchCategoria(Long RowId) {
		Cursor mCursor =
				mDb.query(true, DATABASE_CATEGORIAS, new String[] {KEY_ID_CATEGORIA,
						KEY_CATEGORIA }, KEY_ID_CATEGORIA + "=" + RowId, null,
						null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	/**
	 * Update the note using the details provided. The note to be updated is
	 * specified using the rowId, and it is altered to use the title and body
	 * values passed in
	 * 
	 * @param rowId id of note to update
	 * @param title value to set note title to
	 * @param body value to set note body to
	 * @return true if the note was successfully updated, false otherwise
	 */
	public boolean updateNote(long rowId, String title, String body, String categoria) {
		if(title!=null && title.length()>0 && body!=null && rowId>0){
			ContentValues args = new ContentValues();
			args.put(KEY_TITLE, title);
			args.put(KEY_BODY, body);
			args.put(KEY_CATEGORIA_NOTA, categoria);
			return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
		}
		else return false;
	}

	/**
	 * Update the note using the details provided. The note to be updated is
	 * specified using the rowId, and it is altered to use the title and body
	 * values passed in
	 * 
	 * @param rowId id of note to update
	 * @param title value to set note title to
	 * @param body value to set note body to
	 * @return true if the note was successfully updated, false otherwise
	 */
	public boolean updateCategoria(long rowId, String title) {
		if(title!=null && title.length()>0 && rowId>0){
			ContentValues args = new ContentValues();
			args.put(KEY_CATEGORIA, title);
			return mDb.update(DATABASE_CATEGORIAS, args, KEY_ID_CATEGORIA + "=" + rowId, null) > 0;
		}
		else return false;
	}

	/**
	 * Devuelve una List<String> con las categorias.
	 */
	public List<String> listaCategorias(){
		List<String> list = new ArrayList<String>();
		Cursor mCursor = mDb.query(DATABASE_CATEGORIAS, new String[] {KEY_ID_CATEGORIA, KEY_CATEGORIA}
		, null, null, null, null, null);
		list.add("Sin cambio de categoría / Por defecto");
		list.add("Sin categoria");
		mCursor.moveToFirst();
		if (mCursor.moveToFirst()) {
			//Recorremos el cursor hasta que no haya más registros
			do {
				String nombre = mCursor.getString(1);
				list.add(nombre);
			} while(mCursor.moveToNext());
		}
		return list;

	}

	/**
	 * Devuelve una List<String> con las categorias (Spinner filtro).
	 */
	public List<String> listaCategoriasFiltro(){
		List<String> list = new ArrayList<String>();
		Cursor mCursor = mDb.query(DATABASE_CATEGORIAS, new String[] {KEY_ID_CATEGORIA, KEY_CATEGORIA}
		, null, null, null, null, null);
		list.add("Sin cambio");
		list.add("Todas las categorias");
		mCursor.moveToFirst();
		if (mCursor.moveToFirst()) {
			//Recorremos el cursor hasta que no haya más registros
			do {
				String nombre = mCursor.getString(1);
				list.add(nombre);
			} while(mCursor.moveToNext());
		}
		return list;

	}



}
