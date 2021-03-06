package com.bentonian.framework.ui;

import static com.bentonian.framework.io.FileUtil.readFile;
import static com.bentonian.framework.io.FileUtil.readResource;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glValidateProgram;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.Util;

import com.google.common.base.Strings;


public class ShaderUtil {
  
  private static final int BUFFER_SIZE = 4096;

  public static int compileProgram(int vShader, int fShader) {
    int shaderProgram = glCreateProgram();
    
    glAttachShader(shaderProgram, vShader);
    testGlError();
    glAttachShader(shaderProgram, fShader);
    testGlError();
    glLinkProgram(shaderProgram);
    testGlError();

    glValidateProgram(shaderProgram);
    checkProgram(shaderProgram);

    return shaderProgram;
  }

  public static int loadShader(int shaderType, String file) {
    return loadShader(shaderType, file, readFile(file));
  }

  public static int loadShader(int shaderType, Class<?> clazz, String resourceName) {
    return loadShader(shaderType, resourceName, readResource(clazz, resourceName));
  }
  
  public static void checkShader(int shader, String description) {
    testGlError();
    String infolog = glGetShaderInfoLog(shader, BUFFER_SIZE);
    if (!Strings.isNullOrEmpty(infolog) 
        && !infolog.trim().equals("No errors.")
        && !infolog.contains("WARNING:")) {
      infolog = infolog.trim();
      System.out.println("Info log for shader '" + description + "' (ID " + shader + "):");
      System.out.println(infolog);
      new RuntimeException().printStackTrace();
      System.exit(-1);
    }
    if (!Strings.isNullOrEmpty(infolog)) {
      System.out.println(infolog.trim());
    }
  }

  public static void checkProgram(int program) {
    testGlError();
    String infolog = glGetProgramInfoLog(program, BUFFER_SIZE);
    if (!Strings.isNullOrEmpty(infolog) 
        && !infolog.trim().equals("No errors.")
        && !infolog.contains("WARNING:")) {
      infolog = infolog.trim();
      System.out.println("Info Log of Program Object ID: " + program);
      System.out.println(infolog);
      new RuntimeException().printStackTrace();
      System.exit(-1);
    }
    if (!Strings.isNullOrEmpty(infolog)) {
      System.out.println(infolog.trim());
    }
  }
  
  public static void testGlError() {
    try {
      Util.checkGLError();
    } catch (OpenGLException e) {
      e.setStackTrace(Arrays.copyOfRange(e.getStackTrace(), 2, e.getStackTrace().length));
      e.printStackTrace();
      System.exit(-1);
    }
  }

  public static void clearGlError() {
    try {
      Util.checkGLError();
    } catch (OpenGLException e) {
      System.out.println("Clearing OpenGL error " + e.getMessage());
    }
  }

  public static int validateLocation(int location, String name) {
    if (location < 0) {
      RuntimeException e = new RuntimeException("Whoops!  Couldn't find " + name + ".");
      e.printStackTrace();
      System.exit(-1);
    }
    return location;
  }

  public static int loadShader(int shaderType, String name, List<String> lines) {
    int shader = glCreateShader(shaderType);
    glShaderSource(shader, lines.toArray(new String[]{}));
    testGlError();
    glCompileShader(shader);
    checkShader(shader, name);
    return shader;
  }
  
  public static void printGlVersion() {
    System.out.println("GL version: " + GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
  }
}
