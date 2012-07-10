package common.classes;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassUtil {

	public static Object instanceClass(Class<?> clazz) {
		if (clazz == null)
			return null;

		String className = clazz.getName();

		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			System.out.println("Error instanciando la clase " + className);
			return null;
		} catch (IllegalAccessException e) {
			System.out.println("Error accediendo a la clase " + className);
			return null;
		}
	}
	
	public static Class<?> getClassFromDirectory(String className, File dir) throws ClassNotFoundException, MalformedURLException {
		URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { dir.toURI().toURL() });
		File[] classFiles = dir.listFiles(new ClassFilenameFilter(className));

		if (classFiles == null)
			return null;

		int size = classFiles.length;
		if (size == 0)
			return null;

		return Class.forName("scripts."+className, true, classLoader);
	}
	
	public static Class<?> getClassFromFile(File file) {
		if( file == null || !file.isFile() || !file.canRead() )
			return null;
		
		return file.getClass();
	}
	
	public static String invokeMethod (Class<?> clazz, String methodName) {
		if (clazz == null)
			return null;
		Method method;
		try {
			method = clazz.getMethod("getName");
			Object tempInstance = clazz.newInstance();
			if(method != null)
				return (String)method.invoke(tempInstance);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "Error al acceder al nombre del script";
	}
	
}
