package com.android.demo.mail;

import android.app.Activity;

/** Implementa la interfaz de la abstracción utilizando (delegando a) una referencia a un objeto de tipo implementor  */
public class MailAbstractionImpl implements MailAbstraction {

	/** objeto delegado que facilita la implementación del método send */
	private MailImplementor implementor;

	/** Constructor de la clase. Inicaliza el objeto delegado según el entorno de ejecución de la aplicación Android
	 * @param sourceActiviy actividad desde la cual se abrirá la actividad encargada de gestionar el correo
	 */
	public MailAbstractionImpl(Activity sourceActivity) {
		String brand = android.os.Build.BRAND;
		if (brand.compareTo("generic") == 0) {
			implementor = new JavaMailImplementor(sourceActivity);
			android.util.Log.d("MailAbstractionImpl", "Ejecutándose en el emulador");
		} else {
			implementor = new BuiltInMailImplementor(sourceActivity);
			android.util.Log.d("MailAbstractionImpl",
					"Ejecutándose en un dispositivo real");
		}

	}

	/** Envía el correo con el asunto (subject) y cuerpo (body) que se reciben como parámetros a través de un objeto delegado
	 * @param subject asunto
	 * @param body cuerpo del mensaje
	 */
	public void send(String subject, String body) {
		implementor.send(subject, body);
	}
}