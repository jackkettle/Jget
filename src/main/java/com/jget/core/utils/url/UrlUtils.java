package com.jget.core.utils.url;

import org.apache.commons.validator.routines.EmailValidator;

public class UrlUtils {

	public static boolean isEmailLink(String link){
		
		if(link.startsWith("mailto"))
			return true;
		
		return EmailValidator.getInstance().isValid(link);
		
	}
	
}
