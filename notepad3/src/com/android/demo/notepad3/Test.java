package com.android.demo.notepad3;

import android.util.Log;

public class Test {

	/** comunicacion con la base de datos*/
	private NotesDbAdapter mDbHelper;

	public Test (NotesDbAdapter bd){
		mDbHelper = bd;
	}

	public int cargarNotas (int numNotas){
		int errores = 0;
		for(int i = 0; i<numNotas; i++){
			if(mDbHelper.createNote("nota" + i, "body", null)==-1){
				errores++;
			}
		}
		return errores;
	}

	public long longitudNota (){
		long longitud = 0;
		String mensaje = "1234567890";
		try{
			while (mDbHelper.createNote("nota" + longitud, mensaje, null)!=-1){
				Log.i("MyActivity", "Longitud =  " + longitud);
				longitud++;
				mensaje = mensaje + mensaje;
			}
			return longitud;
		}
		catch (Exception e){
			return longitud;
		}
	}

}
