package com.ecapi.utils;

public class TextoUtils {

	public static String formatoPrimeraLetraMayuscula(String texto) {
        if (texto == null || texto.isBlank()) {
            return texto;
        }

        texto = texto.trim().toLowerCase();
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }
	
	public static String formatoTodoMinuscula(String texto) {
        return texto == null ? null : texto.trim().toLowerCase();
    }
	
	public static String formatoTodoMayuscula(String texto) {
        return texto == null ? null : texto.trim().toUpperCase();
    }
}
