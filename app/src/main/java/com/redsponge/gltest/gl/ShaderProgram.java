package com.redsponge.gltest.gl;

import android.util.Log;

import static android.opengl.GLES30.*;

public class ShaderProgram {

    private final int shaderProgramId;

    public ShaderProgram(String vertexCode, String fragmentCode) {
        shaderProgramId = glCreateProgram();

        int vertexShader = createShader(GL_VERTEX_SHADER, vertexCode);
        int fragmentShader = createShader(GL_FRAGMENT_SHADER, fragmentCode);

        glAttachShader(shaderProgramId, vertexShader);
        glAttachShader(shaderProgramId, fragmentShader);

        glLinkProgram(shaderProgramId);
    }

    private int createShader(int type, String code) {
        int shader = glCreateShader(type);
        glShaderSource(shader, code);
        glCompileShader(shader);

        int[] compileStatus = new int[1];
        glGetShaderiv(shader, GL_COMPILE_STATUS, compileStatus, 0);
        if(compileStatus[0] != GL_TRUE) {
            String err = glGetShaderInfoLog(shader);
            Log.e("ShaderProgram", "Failed to compile shader:\n------------\n" + code + "\n-----------\nError:\n" + err);
        }
        return shader;
    }

    public void bind() {
        glUseProgram(shaderProgramId);
    }

    public void setUniformFloat(String uniformName, int value) {
        glUniform1f(glGetUniformLocation(shaderProgramId, uniformName), value);
    }

    public void setUniformMat4(String uniformName, float[] matrix) {
        glUniformMatrix4fv(glGetUniformLocation(shaderProgramId, uniformName), 1, false, matrix, 0);
    }

    public int getAttributeLocation(String attribute) {
        return glGetAttribLocation(shaderProgramId, attribute);
    }


}
