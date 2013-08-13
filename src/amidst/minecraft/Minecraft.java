package amidst.minecraft;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import amidst.Log;
import amidst.foreign.VersionInfo;

public class Minecraft {
	private Class<?> mainClass;
	private URLClassLoader classLoader;
	private String versionID; 
	private URL urlToJar;
	private static Minecraft activeMinecraft; 
	
	private HashMap<String, MinecraftClass> nameMap;
	private HashMap<String, MinecraftClass> classMap;
	
	public HashMap<String, MinecraftObject> globalMap;
	
	public VersionInfo version = VersionInfo.unknown;
	
	public Minecraft(URL jar) {
		urlToJar = jar;
		try {
			classLoader = new URLClassLoader(new URL[]{jar});
			use();
			if (classLoader.findResource("net/minecraft/client/Minecraft.class") != null)
				mainClass = classLoader.loadClass("net.minecraft.client.Minecraft");
			else if (classLoader.findResource("net/minecraft/server/MinecraftServer.class") != null)
				mainClass = classLoader.loadClass("net.minecraft.server.MinecraftServer");
			else
				throw new RuntimeException();
		} catch (Exception e) {
			e.printStackTrace();
			Log.kill("Attempted to load non-minecraft jar, or unable to locate starting point.");
			// TODO : add error - Not a minecraft.jar file
		}
		String typeDump = "";
		Field fields[] = mainClass.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			String typeString = fields[i].getType().toString();
			if (typeString.startsWith("class ") && !typeString.contains("."))
				typeDump += typeString.substring(6);
		}
		versionID = typeDump;
	}
	
	public void setGlobal(String name, MinecraftObject object) {
		globalMap.put(name, object);
	}
	public MinecraftObject getGlobal(String name) {
		return globalMap.get(name);
	}
	
	public void identifyClasses() {
		
		
	}
	public void isAtLeast(VersionInfo version) {
		
	}
	/*
	private void createFromMCInfo(MCInfo info) {
		nameMap = new HashMap<String, MinecraftClass>();
		classMap = new HashMap<String, MinecraftClass>();
		try {
			classLoader = new URLClassLoader(new URL[]{urlToJar});
			use();
			mainClass = classLoader.loadClass("net.minecraft.client.Minecraft");
			
			Vector<MinecraftClass> classes = info.getMinecraftClasses();
			Vector<MinecraftProperty> properties = info.getMinecraftProperties();
			Vector<MinecraftMethod> methods = info.getMinecraftMethods();
			Vector<MinecraftConstructor> constructors = info.getMinecraftConstructors();
			for (MinecraftClass clazz : classes) { //Load all the classes into the name dictionary.
				clazz.load(this);
				nameMap.put(clazz.getName(), clazz);
				classMap.put(clazz.getClassName(), clazz);
			}
			for (MinecraftClass clazz : classes) { //Load all the properties and methods.
				for (MinecraftProperty property : properties) {
					if (clazz.getName().equals(property.getParentName()))
						clazz.addProperty(property);
				}
				for (MinecraftMethod method : methods) {
					if (clazz.getName().equals(method.getParentName()))
						clazz.addMethod(method);
				}
				for (MinecraftConstructor constructor : constructors) {
					if (clazz.getName().equals(constructor.getParentName()))
						clazz.addConstructor(constructor);
				}
			}
			activeMinecraft = this;
			//classMap = info.getMinecraftClasses(this);
			//Log.i(classMap.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Log.i(nameMap.toString());
		
	}*/
	public URL getPath() {
		return urlToJar;
	}
	
	public void use() {
		Thread.currentThread().setContextClassLoader(classLoader);
	}
	
	public String getVersionID() {
		return versionID;
	}
	public MinecraftClass getClassByName(String name) {
		return nameMap.get(name);
	}
	public URLClassLoader getClassLoader() {
		return classLoader;
	}
	public Class<?> loadClass(String name) {
		try {
			return classLoader.loadClass(name);
		} catch (ClassNotFoundException e) {
			Log.e("Error loading a class (" + name + ")");
			e.printStackTrace();
		}
		return null;
	}
	public MinecraftClass getClassByType(String name) {
		return classMap.get(name);
		
	}
	
	public static Minecraft getActiveMinecraft() {
		return activeMinecraft;
	}
}