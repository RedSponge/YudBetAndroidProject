package com.redsponge.carddeck.gl;

import android.graphics.Color;

import com.redsponge.carddeck.R;
import com.redsponge.carddeck.gl.texture.Texture;
import com.redsponge.carddeck.gl.texture.TextureRegion;
import com.redsponge.carddeck.gl.utils.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static android.opengl.GLES30.*;
public class TextureBatch implements Disposable {

    private static final int FLOAT_SIZE = 4;

    private static final int MAX_RENDERS_PER_BATCH = 128;
    private static final int NUM_VERTICES = MAX_RENDERS_PER_BATCH * 4;
    private static final int NUM_INDICES = MAX_RENDERS_PER_BATCH * 6;

    private int vbo, ebo, vao;

    private ShaderProgram shader;

    private float[] projectionMatrix;

    private int posAttribLocation;
    private int texCoordsAttribLocation;
    private int colorAttribLocation;


    private int numDrawnVerts;
    private boolean isDrawing;
    private FloatBuffer vertices;

    private Texture currentRenderedTexture;
    private TextureRegion tmpRegion;

    private TexBatchVertex tmpVertex;

    private Color color;

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

        vertices = BufferUtils.allocateFloatBuffer(NUM_VERTICES * TexBatchVertex.getSize() / FLOAT_SIZE);
        tmpVertex = new TexBatchVertex();

        tmpRegion = new TextureRegion();

        color = Color.valueOf(Color.WHITE);
    }

    public void begin() {
        if(isDrawing) {
            throw new RuntimeException("Tried to begin a started batch!");
        }
        numDrawnVerts = 0;
        isDrawing = true;
        currentRenderedTexture = null;
    }

    public void draw(Texture texture, float x, float y) {
        draw(texture, x, y, texture.getWidth(), texture.getHeight());
    }

    public void draw(Texture texture, float x, float y, float width, float height) {
        tmpRegion.setTexture(texture);
        tmpRegion.setX(0);
        tmpRegion.setY(0);
        tmpRegion.setWidth(texture.getWidth());
        tmpRegion.setHeight(texture.getHeight());

        draw(tmpRegion, x, y, width, height);
    }

    public void draw(TextureRegion region, float x, float y, float width, float height) {
        if(!isDrawing) {
            throw new RuntimeException("Tried to draw using batch that hasn't begun!");
        }

        if(numDrawnVerts >= NUM_VERTICES) {
            midFlush();
        }

        if(currentRenderedTexture == null) currentRenderedTexture = region.getTexture();
        if(region.getTexture() != currentRenderedTexture) {
            midFlush();
            flushToScreen();
            currentRenderedTexture = region.getTexture();
        }

        addVertex(x, y, region.getX1(), region.getY1());
        addVertex(x + width, y, region.getX2(), region.getY1());
        addVertex(x + width, y + height, region.getX2(), region.getY2());
        addVertex(x, y + height, region.getX1(), region.getY2());
    }

    private void midFlush() {
        flushToScreen();
        numDrawnVerts = 0;
        currentRenderedTexture = null;
    }

    private void flushToScreen() {
        if(numDrawnVerts == 0) return;

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, numDrawnVerts * TexBatchVertex.getSize(), vertices);


        shader.bind();
        glBindVertexArray(vao);
        shader.setUniformMat4("u_projection", projectionMatrix);

        currentRenderedTexture.bind();
        int count = 6 * numDrawnVerts / 4;

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);

        glDisable(GL_BLEND);
    }

    private void addVertex(float x, float y, float texX, float texY) {
        tmpVertex.x = x;
        tmpVertex.y = y;
        tmpVertex.r = color.red();
        tmpVertex.g = color.green();
        tmpVertex.b = color.blue();
        tmpVertex.a = color.alpha();
        tmpVertex.texX = texX;
        tmpVertex.texY = texY;

        tmpVertex.toFloatArray(vertices, numDrawnVerts * TexBatchVertex.getSize() / FLOAT_SIZE);
        numDrawnVerts++;
    }

    public void end() {
        if(!isDrawing) {
            throw new RuntimeException("Tried to end batch that hasn't started!");
        }
        flushToScreen();
        isDrawing = false;
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

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private static class TexBatchVertex {
        private float x, y;
        private float r, g, b, a;
        private float texX, texY;

        public static int getSize() {
            return 32;
        }

        public void toFloatArray(FloatBuffer dst, int offset) {
            dst.position(0);
            dst.put(offset + 0, x);
            dst.put(offset + 1, y);
            dst.put(offset + 2, r);
            dst.put(offset + 3, g);
            dst.put(offset + 4, b);
            dst.put(offset + 5, a);
            dst.put(offset + 6, texX);
            dst.put(offset + 7, texY);
        }
    }

    public float[] getProjectionMatrix() {
        return projectionMatrix;
    }

    public void setProjectionMatrix(float[] projectionMatrix) {
        System.arraycopy(projectionMatrix, 0, this.projectionMatrix, 0, 16);
    }

    @Override
    public void dispose() {
        shader.dispose();
        int[] exposer = new int[] {vbo, ebo, vao};
        glDeleteBuffers(2, exposer, 0);

        glDeleteVertexArrays(1, exposer, 2);
    }
}
