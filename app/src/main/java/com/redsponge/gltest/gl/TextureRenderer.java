package com.redsponge.gltest.gl;

import android.opengl.Matrix;

import com.redsponge.gltest.R;
import com.redsponge.gltest.gl.utils.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static android.opengl.GLES30.*;
public class TextureRenderer {

    private int vbo;
    private int vao;
    private int ebo;

    private float[] projectionMatrix;

    private static final float[] VERTS = new float[] {
            0, 0, 1, 1, 1, 1, 0, 0,
            20, 0, 1, 1, 1, 1, 1, 0,
            20, 20, 1, 1, 1, 1, 1, 1,
            0, 20, 1, 1, 1, 1, 0, 1
    };

    private static final int[] INDICES = new int[] {
            0, 1, 2,
            2, 3, 0
    };

    private ShaderProgram shader;


    public TextureRenderer() {
        projectionMatrix = new float[16];
        Matrix.orthoM(projectionMatrix, 0, 0, 320, 0, 180, -1, 1);

        shader = new ShaderProgram(RawReader.readRawFile(R.raw.texvertex), RawReader.readRawFile(R.raw.texfragment));

        int[] receivers = new int[2];
        glGenBuffers(receivers.length, receivers, 0);

        vbo = receivers[0];
        ebo = receivers[1];

        glGenVertexArrays(1, receivers, 0);
        vao = receivers[0];

        glBindVertexArray(vao);

        FloatBuffer vertBuffer = BufferUtils.allocateFloatBuffer(VERTS);
        IntBuffer indicesBuffer = BufferUtils.allocateIntBuffer(INDICES);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

        glBufferData(GL_ARRAY_BUFFER, VERTS.length * 4, vertBuffer, GL_STATIC_DRAW);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, INDICES.length * 4, indicesBuffer, GL_STATIC_DRAW);

        int posLoc = shader.getAttributeLocation("a_position");
        int texLoc = shader.getAttributeLocation("a_texCoords");
        int colLoc = shader.getAttributeLocation("a_color");

        glVertexAttribPointer(posLoc, 2, GL_FLOAT, false, 8 * 4, 0 * 4);
        glVertexAttribPointer(colLoc, 4, GL_FLOAT, false, 8 * 4, 2 * 4);
        glVertexAttribPointer(texLoc, 2, GL_FLOAT, false, 8 * 4, 6 * 4);

        glEnableVertexAttribArray(posLoc);
        glEnableVertexAttribArray(colLoc);
        glEnableVertexAttribArray(texLoc);

        glBindVertexArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void render(Texture texture) {
        shader.bind();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

        shader.setUniformMat4("u_projection", projectionMatrix);

        texture.bind();
//        glDrawArrays(GL_TRIANGLES, 0, 3);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
    }

    public void setProjectionMatrix(float[] projectionMatrix) {
        System.arraycopy(projectionMatrix, 0, this.projectionMatrix, 0, projectionMatrix.length);
    }

}
