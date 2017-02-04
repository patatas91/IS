package com.android.demo.mail;

/** Define la interfaz de la abstracción */
public interface MailAbstraction {

	/** Definición del método que permite enviar el correo con el asunto (subject) y cuerpo (body) que se reciben como parámetros
     * @param subject asunto
     * @param body cuerpo del mensaje
     */
	public void send(String subject, String body);
}
