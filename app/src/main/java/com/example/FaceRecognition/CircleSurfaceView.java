package com.example.FaceRecognition;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * Created by Kenshin on 2017/7/6.
 */

public class CircleSurfaceView extends SurfaceView {

    public CircleSurfaceView(Context context) {
        super(context);
    }

    public CircleSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CircleSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.lightblue));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15);
        Path path = new Path();
        path.addCircle(this.getWidth() / 2, this.getHeight() / 2, this.getWidth() / 2 - 15, Path.Direction.CCW );
        //描边
        canvas.drawPath(path, paint);
        //裁剪画布
        canvas.clipPath(path, Region.Op.REPLACE);
        super.draw(canvas);
    }
}
