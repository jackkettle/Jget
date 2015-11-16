package com.jget.core.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider
	implements ApplicationContextAware
{
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext (ApplicationContext applicationContext)
	{
		ApplicationContextProvider.applicationContext = applicationContext;
	}

	public static ApplicationContext getApplicationContext ()
	{
		return ApplicationContextProvider.applicationContext;
	}
	
	public static void resetApplicationContext (ApplicationContext applicationContext)
	{
		ApplicationContextProvider.applicationContext = applicationContext;
	}
	
	public static <T> T getBean (Class<T> clazz)
	{
		return ApplicationContextProvider.applicationContext.getBean (clazz);
	}

	public static <T> T getBean (String name, Class<T> clazz)
	{
		return ApplicationContextProvider.applicationContext.getBean (name, clazz);
	}
}