package com.binklac.tools.javadumper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class OnLoadTransformer implements ClassFileTransformer {
	private String className;
	private String saveFile;
	
	public static void write2File(String Dst, byte[] data) throws RuntimeException {
		File file = null;
		FileOutputStream outputstream = null;
		
		try {
			file = new File(Dst);
			outputstream = new FileOutputStream(file);
			outputstream.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				outputstream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public OnLoadTransformer(String className, String saveFile) {
		this.className = className;
		this.saveFile = saveFile;
	}

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		if(className.replace("/",".").equals(this.className)){
			System.out.println("Found the specified class <" + this.className + ">, save it in <" + this.saveFile + ">.");
			write2File(this.saveFile , classfileBuffer);
		}
		return null;
	}
}
