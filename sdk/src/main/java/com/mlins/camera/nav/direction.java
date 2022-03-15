package com.mlins.camera.nav;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.mlins.utils.PropertyHolder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class direction implements Renderer {

    //	private final String[] cardinals = {"N","E","S","W"};
    public static final int TEX_INDEX_ARROW = 0;
    public static final int TEX_INDEX_ELEVATOR = 1;
    public static final int TEX_INDEX_DESTINATION = 2;
    final ShortBuffer mIndexBuffer;
    protected float[] vertices = {
            -0.5f, -0.5f, 0.0f,    //bottom left (White)
            0.5f, -0.5f, 0.0f,    //bottom right (Red)
            -0.5f, 0.5f, 0.0f,    //top left (Green)
            0.5f, 0.5f, 0.0f, //top right (Yellow)

            0.0f, 0.0f, 0.0f, // origin
    };
    // Colors respective to each vertex.
    protected float[] colors = {
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f,
    };
    protected float[] textureCoordinates = {
            1.0f, 1.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
    };
    // The order to draw facets from vertices.
    protected short[] indices = {
            0, 1, 2, 3, // Sign as triangle strip 0->1->2->0 + 2->1->3->2 [+ 2->3->4->2...]
    };
    FloatBuffer mVertexBuffer;
    FloatBuffer mColorBuffer;
    FloatBuffer mTextureBuffer;
    private Bitmap mBitmap;

    private float[] mRotationM = null; // float[16];
    private float[] mLocationV = null; // float[3]; // may be replaced by three floats?
    private int mTexID = -1;
    private int[] mTextures = new int[3];


//	private float mFovy = 45f;

    public direction() {
        mVertexBuffer = makeFloatBufferDirect(vertices, vertices.length);
        mColorBuffer = makeFloatBufferDirect(colors, colors.length);
        mTextureBuffer = makeFloatBufferDirect(textureCoordinates, textureCoordinates.length);
        mIndexBuffer = makeShortBufferDirect(indices, indices.length);
    }

    /**
     * Creates a direct float buffer based on the content of the given float array.
     * <p> The capacity of the new buffer will be the greater of the given
     * capacity and the given array length.
     *
     * @param far         content for the new buffer. may be null.
     * @param minCapacity a minimum capacity for the new buffer.
     * @return the created direct float buffer.
     */
    public static FloatBuffer makeFloatBufferDirect(float[] far, int minCapacity) {
        ByteBuffer bb = ByteBuffer.allocateDirect(4 * Math.max(minCapacity,
                (far == null ? 0 : far.length)));
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        if (far != null) {
            fb.put(far);
            fb.position(0);
        }
        return fb;
    }

    /**
     * Creates a direct short buffer based on the content of the given short array.
     * <p> The new buffer's capacity will be the greater of the given minimum
     * capacity and the given array length.
     *
     * @param sar         content for the new buffer. may be null.
     * @param minCapacity a minimum capacity for the new buffer.
     * @return the created direct short buffer.
     */
    public static ShortBuffer makeShortBufferDirect(short[] sar, int minCapacity) {
        ByteBuffer bb = ByteBuffer.allocateDirect(2 * Math.max(minCapacity,
                (sar == null ? 0 : sar.length)));
        bb.order(ByteOrder.nativeOrder());
        ShortBuffer sb = bb.asShortBuffer();
        if (sar != null) {
            sb.put(sar);
            sb.position(0);
        }
        return sb;
    }

    public static ByteBuffer makeByteBuffer(byte[] bar) {
        ByteBuffer bb = ByteBuffer.allocateDirect(bar.length);
        bb.order(ByteOrder.nativeOrder());
        bb.put(bar);
        bb.position(0);
        return bb;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

//		Prepare:
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);

//		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
//		gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
        gl.glColor4f(1, 1, 1, 1);

//		gl.glEnable(GL10.GL_POINT_SMOOTH);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
        gl.glShadeModel(GL10.GL_FLAT);

        gl.glPushMatrix();
        gl.glLoadIdentity();
//		GLU.gluLookAt(gl, 0f, -10f, 1.7f, 0.5f, 0f, 0.5f, 0f, 0f, 1f);
        if (mRotationM != null) gl.glMultMatrixf(mRotationM, 0);
//		if (mLocationV != null) gl.glTranslatef(mLocationV[0], mLocationV[1], mLocationV[2]);

//		Draw:
        if (mTexID != -1) {
            gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexID);
        }
        gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4,
                GL10.GL_UNSIGNED_SHORT, mIndexBuffer);

//		draw leading line
//		gl.glColor4f(1, 1, 0, 1); // Yellow
//		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
//		gl.glDisable(GL10.GL_TEXTURE_2D);
//		gl.glPointSize(16);
//		gl.glDrawArrays(GL10.GL_POINTS, 0, 5);
//		 Cleanup:
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glPopMatrix();

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        float ratio = ((float) height) / width;
        GLU.gluOrtho2D(gl, -0.5f, 0.5f, -0.2f * ratio, -0.8f * ratio);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef(0, 0, -5);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
//		gl.glColor4f(0.2f, 0.2f, 0.0f, 0.5f);
        gl.glClearDepthf(1.0f);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

//		// debug code:
//		String glerrs;
//		if ((glerr = gl.glGetError()) != GL10.GL_NO_ERROR) {
//			glerrs = GLU.gluErrorString(glerr)+" ("+glerr+").";
//			int[] s = new int[1];
//			gl.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, s, 0); // [2048] > [765(H)| 599(W)]
//			int f = GLUtils.getInternalFormat(mFloorBitmap); // RGBA = 0x1908
//			int t = GLUtils.getType(mFloorBitmap); // UNSIGNED_BYTE = 0x1401
//		}

//		load Texture
//		int glerr; // for debuging
//		while ((glerr = gl.glGetError()) != GL10.GL_NO_ERROR); // reset gl errors if any.
        gl.glGenTextures(mTextures.length, mTextures, 0);
        mTexID = mTextures[0];
        Resources res = PropertyHolder.getInstance().getMlinsContext().getResources();
        Bitmap deco;
        for (int i = 0; i < mTextures.length; i++) {
            switch (i) {
                case 0:
                    setBitmap(null);
                    break;
                case 1:
                    deco = null;//XXX CAMERA BitmapFactory.decodeResource(res , R.drawable.elevator);
                    setBitmap(deco);
                    break;
                case 2:
                    deco = null;//XXX CAMERA BitmapFactory.decodeResource(res , R.drawable.destination);
                    setBitmap(deco);
                    break;
            }
            gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[i]);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                    GL10.GL_LINEAR);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                    GL10.GL_NEAREST);
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);
        }
    }

    public void setBitmap(Bitmap bm) {
//		XXX: programmatically create POT bitmaps of minimal size when required.
//		hint: GL_IMG_texture_npot - http://www.khronos.org/registry/gles/extensions/OES/OES_texture_npot.txt
//		int h = 64;
//		int w = 64;
//		if (w < 1024) w = 1024;
//		else w = 2048;
        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
        }
        mBitmap.eraseColor(Color.TRANSPARENT);
        Canvas can = new Canvas(mBitmap);
        drawCompass(can, bm);
    }

    private void drawCompass(Canvas canvas, Bitmap bitmap) {
        int radius = Math.min(canvas.getHeight(), canvas.getWidth()) / 2;
        Paint paint = new Paint();
        paint.setColor(0x80808080);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(radius, radius, radius, paint);
        paint.setAntiAlias(true);
        paint.setTextSize(12);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(0xffffffff);
        paint.setStrokeWidth(2);
        Paint headPaint = new Paint(paint);
        headPaint.setColor(0xA0FF0000);
        float u = 0.02f * radius;

        canvas.rotate(180, radius, radius);
        if (bitmap == null) { // draw big red arrow
            Path ahead = new Path();
            ahead.lineTo(5 * u, 12 * u);
            ahead.lineTo(0, 10 * u);
            ahead.lineTo(-5 * u, 12 * u);
            ahead.close();
            ahead.offset(radius, 0);
            canvas.drawPath(ahead, headPaint);
        } else {
//			canvas.drawBitmap(bitmap, radius - bitmap.getWidth()/2, 0, null);
            Rect dst = new Rect((int) (radius - 10 * u), 0, (int) (radius + 10 * u), (int) (20 * u));
            canvas.drawBitmap(bitmap, null, dst, null);
        }

//		headPaint.setStyle(Paint.Style.STROKE);
//		RectF oval = new RectF(-40 * u, -40 * u, 40 * u, 40 * u);
        headPaint.setColor(Color.GREEN);
        Path left = new Path();
        left.rLineTo(6 * u, 2 * u);
        left.rLineTo(-2 * u, -2 * u);
        left.rLineTo(2 * u, -2 * u);
        left.close();

        android.graphics.Matrix m = new android.graphics.Matrix();
        m.setRotate(180);
        Path right = new Path();
        left.transform(m, right);

//		left.addArc(oval , 270, 45);
        left.offset(radius, 10 * u);
        canvas.save();
        canvas.rotate(60, radius, radius);
        for (int i = 0; i < 7; i++) {
            canvas.drawPath(left, headPaint);
            canvas.rotate(15, radius, radius);
        }
        canvas.restore();

//		Path right = new Path();
//		right.moveTo(0, -40 * u);
//		right.rLineTo(-6 * u, -2 *u);
//		right.rLineTo(2 * u, 2 * u);
//		right.rLineTo(-2 * u, 2 * u);
//		right.close();
////		right.addArc(oval , 270, -45);
        right.offset(radius, 10 * u);
        canvas.save();
        canvas.rotate(-60, radius, radius);
        for (int i = 0; i < 7; i++) {
            canvas.drawPath(right, headPaint);
            canvas.rotate(-15, radius, radius);
        }
        canvas.restore();


////		cardinalPaint.setTextSize(24);
//		for(int i=0; i<360; i++) {
//			//				canvas.save();
//			if (i%10 == 0){
//				canvas.drawLine(radius, 1.95f*radius, radius, 2*radius, paint);
//				canvas.drawText(Integer.toString(i), radius, 1.8f*radius, paint);
//				canvas.drawText(Integer.toString(i), radius, 0.15f*radius, paint);
//				if (i%90 == 0){
//					canvas.drawText(cardinals[i/90], radius, 0.4f*radius, cardinalPaint);
//				}
//			}
//			else{
//				canvas.drawLine(radius, 1.98f*radius, radius, 2*radius, paint);
//			}
//			//				canvas.restore();
//			canvas.rotate(1, radius, radius);
//		}

    }

    public void setTexture(int index) {
        if (index < 0 || index >= mTextures.length)
            mTexID = -1;
        else {
            mTexID = mTextures[index];
        }
    }

    public void setRotation(float[] rotationM) {
        if (mRotationM == null) mRotationM = new float[16];
        System.arraycopy(rotationM, 0, mRotationM, 0, 16);
    }

    public void setRotation(float angle, float x, float y, float z) {
        if (mRotationM == null) mRotationM = new float[16];
        Matrix.setIdentityM(mRotationM, 0);
        Matrix.rotateM(mRotationM, 0, angle, x, y, z);
    }

    public void rotate(float angle, float x, float y, float z) {
        if (mRotationM == null) {
            mRotationM = new float[16];
            Matrix.setIdentityM(mRotationM, 0);
        }
        Matrix.rotateM(mRotationM, 0, angle, x, y, z);
    }

    public void setTranslation(float x, float y, float z) {
        if (mLocationV == null) mLocationV = new float[3];
        mLocationV[0] = x;
        mLocationV[1] = y;
        mLocationV[2] = z;
//		for drawing leading line:
        mVertexBuffer.put(12, -x).put(13, -y).put(14, 0);
    }

    public void translate(float x, float y, float z) {
        if (mLocationV == null) mLocationV = new float[3];
        mLocationV[0] += x;
        mLocationV[1] += y;
        mLocationV[2] += z;
    }
}
