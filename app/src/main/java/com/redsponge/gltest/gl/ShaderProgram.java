package com.redsponge.gltest.gl;

import android.opengl.Matrix;

import static android.opengl.GLES20.*;
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
        return shader;
    }

    public void bind() {
        glUseProgram(shaderProgramId);
    }

    public void setUniformFloat(String uniformName, int value) {
        glUniform1f(glGetUniformLocation(shaderProgramId, uniformName), value);
    }

    public void setUniformMat4(String uniformName, float[] matrix) {
        glUniformMatrix4fv(glGetUniformLocation(shaderProgramId, uniformName), 16, false, matrix, 0);
    }

    public int getAttributeLocation(String attribute) {
        return glGetAttribLocation(shaderProgramId, attribute);
    }


}
