package com.redsponge.gltest.gl;

import com.redsponge.gltest.R;
import com.redsponge.gltest.gl.texture.Texture;
import com.redsponge.gltest.gl.utils.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import static android.opengl.GLES30.*;
public class TextureBatch {

    private static final int MAX_RENDERS_PER_BATCH = 128;
    private static final int NUM_VERTICES = MAX_RENDERS_PER_BATCH * 4;
    private static final int NUM_INDICES = MAX_RENDERS_PER_BATCH * 6;

    private int vbo, ebo, vao;

    private ShaderProgram shader;

    private float[] projectionMatrix;

    private int posAttribLocation;
    private int texCoordsAttribLocation;
    private int colorAttribLocation;

    public TextureBatch() {
        projectionMatrix = new float[16];

        shader = new ShaderProgram(RawReader.readRawFile(R.raw.batch_vertex), RawReader.readRawFile(R.raw.batch_fragment));

        int[] fetchers = new int[2];
        glGenBuffers(fetchers.length, fetchers, 0);

        vbo = fetchers[0];
        ebo = fetchers[1];

        glGenVertexArrays(1, fetchers, 0);
        vao = fetchers[0];

        glBindVertexArray(vao);

        generateIndices();
        generateVertexBuffer();
        loadShaderAttributes();

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }


    /**
     * VAO and VBO should be bound beforehand
     */
    private void loadShaderAttributes() {
        posAttribLocation = shader.getAttributeLocation("a_position");
        texCoordsAttribLocation = shader.getAttributeLocation("a_texCoords");
        colorAttribLocation = shader.getAttributeLocation("a_color");

        System.out.println(posAttribLocation + " " + texCoordsAttribLocation + " " + colorAttribLocation);

        glVertexAttribPointer(posAttribLocation, 2, GL_FLOAT, false, TexBatchVertex.getSize(), 0);
        glEnableVertexAttribArray(posAttribLocation);

        glVertexAttribPointer(colorAttribLocation, 4, GL_FLOAT, false, TexBatchVertex.getSize(), 2*4);
        glEnableVertexAttribArray(colorAttribLocation);

        glVertexAttribPointer(texCoordsAttribLocation, 2, GL_FLOAT, false, TexBatchVertex.getSize(), 6*4);
        glEnableVertexAttribArray(texCoordsAttribLocation);
    }

    private void generateVertexBuffer() {
        FloatBuffer fb = BufferUtils.allocateFloatBuffer(NUM_VERTICES * TexBatchVertex.getSize() / 4);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, NUM_VERTICES * TexBatchVertex.getSize(), fb, GL_DYNAMIC_DRAW); // TODO: Maybe an error is here! try changing to empty buffer instead of null!
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private void generateIndices() {
        final int[] indices = new int[NUM_INDICES];
        for(int i = 0; i < MAX_RENDERS_PER_BATCH; i++) {
            indices[6 * i + 0] = 0 + i * 4;
            indices[6 * i + 1] = 1 + i * 4;
            indices[6 * i + 2] = 2 + i * 4;
            indices[6 * i + 3] = 2 + i * 4;
            indices[6 * i + 4] = 3 + i * 4;
            indices[6 * i + 5] = 0 + i * 4;
        }
        IntBuffer iBuf = BufferUtils.allocateIntBuffer(indices);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, NUM_INDICES*4, iBuf, GL_STATIC_DRAW);
    }

    public void render(Texture tex, float x, float y, float width, float height) {
        float[] verts = new float[] {
                x, y, 1, 1, 1, 1, 0, 0,
                x + width, y, 1, 1, 1, 1, 1, 0,
                x + width, y + height, 1, 1, 1, 1, 1, 1,
                x, y + height, 1, 1, 1, 1, 0, 1
        };
        FloatBuffer fBuf = BufferUtils.allocateFloatBuffer(verts);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, verts.length * 4, fBuf);


        shader.bind();
        glBindVertexArray(vao);
//        System.out.println(Arrays.toString(projectionMatrix));
        shader.setUniformMat4("u_projection", projectionMatrix);

        tex.bind();
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
//        glDrawArrays(GL_TRIANGLES, 0, 3);
        glBindVertexArray(0);
    }

    private static class TexBatchVertex {
        private float x, y;
        private float r, g, b, a;
        private float texX, texY;

        public static int getSize() {
            return 32;
        }
    }

    public float[] getProjectionMatrix() {
        return projectionMatrix;
    }

    public void setProjectionMatrix(float[] projectionMatrix) {
        System.arraycopy(projectionMatrix, 0, this.projectionMatrix, 0, 16);
    }
}
