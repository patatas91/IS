package com.android.demo.mail;

import android.app.Activity;

/** 
 * Define la interfaz para las clases de la implementación. 
 * La interfaz no se tiene que corresponder directamente con la interfaz de la abstracción.
 *  
 */
public interface MailImplementor {
	   
   /**  Actualiza la actividad desde la cual se abrirá la actividad de gestión de correo */
   public void setSourceActivity(Activity source);
   
   /** Permite lanzar la actividad encargada de gestionar el correo enviado */
   public void send (String subject, String body);

}
