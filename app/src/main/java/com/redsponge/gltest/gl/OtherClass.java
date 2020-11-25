package com.redsponge.gltest.gl;

import android.opengl.Matrix;

import com.redsponge.gltest.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static android.opengl.GLES31.*;

public class OtherClass {

    private ShaderProgram prog;
    private float[] projectionMatrix;
    private int vboId, eboId;
    private FloatBuffer vboBuffer;

    private float[] triangle = {
            10, 10, 1, 0, 0, 1,
            100, 10, 0, 1, 0, 1,
            100, 80, 0, 0, 1, 1,
            10, 80, 1, 1, 0, 1,
    };

    private int[] indices = {
            0, 1, 2,
            2, 3, 0
    };

    private int vaoId;

    public void create() {
        prog = new ShaderProgram(RawReader.readRawFile(R.raw.vertex), RawReader.readRawFile(R.raw.fragment));

        projectionMatrix = new float[16];

        Matrix.setIdentityM(projectionMatrix, 0);
        Matrix.orthoM(projectionMatrix, 0, 0, 160, 0, 90, -1, 1);

        int[] fetchers = new int[2];
        glGenBuffers(2, fetchers, 0);
        vboId = fetchers[0];
        eboId = fetchers[1];

        glGenVertexArrays(1, fetchers, 0);
        vaoId = fetchers[0];

        ByteBuffer buff = ByteBuffer.allocateDirect(triangle.length * 4);
        buff.order(ByteOrder.nativeOrder());
        vboBuffer = buff.asFloatBuffer();

        vboBuffer.put(triangle);
        vboBuffer.position(0);

        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, triangle.length * 4, vboBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        ByteBuffer eboBuff = ByteBuffer.allocateDirect(indices.length * 4);
        eboBuff.order(ByteOrder.nativeOrder());
        IntBuffer eboBuffer = eboBuff.asIntBuffer();

        eboBuffer.put(indices);
        eboBuffer.position(0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.length * 4, eboBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        glBindVertexArray(vaoId);

        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);

        int posLocation = prog.getAttributeLocation("a_position");
        glEnableVertexAttribArray(posLocation);
        glVertexAttribPointer(posLocation, 2, GL_FLOAT, false, 4 * 6, 0);

        int colorLocation = prog.getAttributeLocation("a_color");
        glEnableVertexAttribArray(colorLocation);
        glVertexAttribPointer(colorLocation, 4, GL_FLOAT, false, 4 * 6, 2 * 4);


        glBindVertexArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

    }

    public void render() {
        glBindVertexArray(vaoId);

//        glBindBuffer(GL_ARRAY_BUFFER, vboId);
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        prog.bind();
//        int posLocation = prog.getAttributeLocation("a_position");
//        glEnableVertexAttribArray(posLocation);
//        glVertexAttribPointer(posLocation, 2, GL_FLOAT, false, 4 * 6, 0);
//
//        int colorLocation = prog.getAttributeLocation("a_color");
//        glEnableVertexAttribArray(colorLocation);
//        glVertexAttribPointer(colorLocation, 4, GL_FLOAT, false, 4 * 6, 2 * 4);

        prog.setUniformMat4("u_projection", projectionMatrix);

        glDrawArrays(GL_TRIANGLES, 0, 3);
//        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);

//        glDisableVertexAttribArray(posLocation);
    }
}
