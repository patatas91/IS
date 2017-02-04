package com.android.demo.mail;

import com.android.demo.notepad3.NoteEdit;
import com.android.demo.notepad3.NotesDbAdapter;

import android.app.Activity;
import android.content.Intent;

/**
 * Clase que implementa metodos que permiten la edicion de notas
 * 
 * @author Fernando Aliaga y Cristian Simon
 * 
 */
public class JavaMailImplementor implements MailImplementor {

	/** Variable para guardar la actividad padre */
	private Activity sourceActivity;

	/**
	 * Constructor de la clase
	 * @param sourceActivity actividad padre
	 */
	public JavaMailImplementor(Activity sourceActivity) {
		setSourceActivity(sourceActivity);
	}

	/**
	 * Define la actividad padre
	 * @param sourceActivity actividad padre
	 */
	public void setSourceActivity(Activity source) {
		this.sourceActivity = source;

	}

	/**
	 * Genera una actividad con la cual se podra mandar un mail
	 * con el titulo y cuerpo pasados por parametro
	 * @param subject titulo nota
	 * @param body cuerpo nota
	 */
	public void send(String subject, String body) {
		Intent i = new Intent(sourceActivity, JavaMailActivity.class);
		// BUNDLE
		i.putExtra("SUBJECT", subject);
		i.putExtra("BODY", body);
		sourceActivity.startActivity(i);

	}

}
