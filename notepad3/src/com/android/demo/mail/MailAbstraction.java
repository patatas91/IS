package com.android.demo.mail;

/** Define la interfaz de la abstracci�n */
public interface MailAbstraction {

	/** Definici�n del m�todo que permite enviar el correo con el asunto (subject) y cuerpo (body) que se reciben como par�metros
     * @param subject asunto
     * @param body cuerpo del mensaje
     */
	public void send(String subject, String body);
}
