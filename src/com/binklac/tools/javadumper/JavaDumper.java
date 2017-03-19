package com.binklac.tools.javadumper;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class JavaDumper {
	private static final String helpMessage = "\nUseage: \n\tJava {all your javaagent, agentlib, agentpath} -javaagent:JavaDumper.jar=<class to dump>;<where to save> {other} \nE.g.:\n\tjava -javaagent:JavaDumper.jar=java.lang.Shutdown;Shutdown.class -jar JavaDumper.jar";
	
	private static boolean checkArgs(String[] Args){
		File file = null;
		
		if(Args == null || Args.length != 2){
			System.err.println("Parameter error : You must provide two parameters.");
			return false;
		}
		file = new File(Args[1]);
		if(file.exists()){
			if(file.isDirectory()){
				System.err.println("Parameter error : The save location can not be a folder.");
				return false;
			} else {
				if(!file.canWrite()){
					System.err.println("IO error : Attempt to modify target file failed.");
					return false;
				}else{
					if(!file.delete()){
						System.err.println("IO error : Can not delete existing files.");
						return false;
					}
				}
			}
		} else {
			try {
				if(!file.getCanonicalFile().getParentFile().exists()){
					if(!file.getCanonicalFile().getParentFile().mkdirs()){
						System.err.println("IO error : Can not create parent path.");
						return false;
					}
				}
				if(!file.createNewFile()){
					System.err.println("IO error : Can not create target file.");
					return false;
				}
				
			} catch (IOException e) {
				System.err.println("IO error : " + e.getMessage());
				return false;
			}
		}
		return true;
		
	}

	public static void main(String[] args) {
		System.out.println(helpMessage);
	}
	
	public static void premain(String agentArgs, Instrumentation inst){
		String[] Args = agentArgs.split(";");
		
		if(checkArgs(Args)){
			for(@SuppressWarnings("rawtypes") Class clazz :inst.getAllLoadedClasses()){
				if(clazz.getName().equals(Args[0])){
					ClassPool classPool = ClassPool.getDefault();
					try {
						CtClass getedClass = classPool.getCtClass(Args[0]);
						OnLoadTransformer.write2File(Args[1], getedClass.toBytecode());
						System.out.println("The class <" + Args[0] + "> already loaded, save it to <" + Args[1] + ">");
					} catch (NotFoundException | RuntimeException | IOException | CannotCompileException e) {
						e.printStackTrace();
					}
					return;
				}
				
			}
			System.out.println("Waiting for class <" + Args[0] + "> load ...");
			inst.addTransformer(new OnLoadTransformer(Args[0], Args[1]));
		} else {
			System.out.println(helpMessage);
		}
		
	}

}


