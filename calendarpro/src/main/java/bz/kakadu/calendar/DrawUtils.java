/*
 * Copyright (c) 2019 Roman Tsarou
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package bz.kakadu.calendar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created on 15.11.2016
 *
 * @author Roman Tsarou
 */
public final class DrawUtils {
    public static final int START = 0, MIDDLE = 1, END = 2, SINGLE = 3;
    private static final Paint PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Path PATH = new Path();
    private static final Path PATH2 = new Path();
    private static final RectF RECT_F = new RectF();

    private DrawUtils() {
    }

    public static void drawText(String text, Paint paint, Canvas canvas, RectF bounds) {
        final float textHeightHalf = (paint.descent() - paint.ascent()) / 2f - paint.descent();
        canvas.drawText(text, bounds.centerX(),
                bounds.centerY() + textHeightHalf, paint);
    }

    public static void drawCircle(@ColorInt int color, Canvas canvas, RectF bounds, float scale) {
        PAINT.setColor(color);
        drawCircle(PAINT, canvas, bounds, scale);
    }

    public static void drawCircle(Paint paint, Canvas canvas, RectF bounds, float scale) {
        final float size = Math.min(bounds.height(), bounds.width()) * scale;
        canvas.drawCircle(bounds.centerX(), bounds.centerY(), size / 2f, paint);
    }

//    private static float centerY(RectF bounds) {
//        return bounds.height() / 2f + bounds.top;
//    }
//
//    private static float centerX(RectF bounds) {
//        return bounds.width() / 2f + bounds.left;
//    }

    public static void drawDrawable(Drawable drawable, Canvas canvas, RectF bounds, float scale) {
        final float size = Math.min(bounds.height(), bounds.width()) * scale;
        drawable.setBounds(
                0,
                0,
                (int) size,
                (int) size
        );

        final float offsetX = bounds.left + (bounds.width() - (int) size) / 2f;
        final float offsetY = bounds.top + (bounds.height() - (int) size) / 2f;
        canvas.save();
        canvas.translate(offsetX, offsetY);
        drawable.draw(canvas);
        canvas.restore();
    }

    public static void drawCenterDrawable(Drawable drawable, Canvas canvas, RectF bounds) {
        drawable.setBounds(
                0,
                0,
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight()
        );

        final float offsetX = bounds.left + (bounds.width() - drawable.getIntrinsicWidth()) / 2f;
        final float offsetY = bounds.top + (bounds.height() - drawable.getIntrinsicHeight()) / 2f;
        canvas.save();
        canvas.translate(offsetX, offsetY);
        drawable.draw(canvas);
        canvas.restore();
    }

    public static void drawPeriod(@PeriodType int periodType, @ColorInt int color, Canvas canvas, RectF bounds, float scale) {
        PAINT.setColor(color);
        drawPeriod(periodType, PAINT, canvas, bounds, scale);
    }

    public static void drawPeriodArc(@PeriodType int periodType, @ColorInt int color, Canvas canvas, RectF bounds, float scale, float arcRadius) {
        PAINT.setColor(color);
        drawPeriodArc(periodType, PAINT, canvas, bounds, scale, arcRadius);
    }

    public static void drawPeriod(@PeriodType int periodType, Paint paint, Canvas canvas, RectF bounds, float scale) {
        if (periodType == SINGLE) {
            drawCircle(paint, canvas, bounds, scale);
            return;
        }
        final float size = Math.min(bounds.height(), bounds.width()) * scale;
        final float paddingHor = (1f * bounds.width() - size) / 2f;
        final float paddingVer = (1f * bounds.height() - size) / 2f;
        final float radius = size / 2;
        RECT_F.set(0, 0, 3 * bounds.width() - paddingHor * 2, size);
        float offsetX;
        switch (periodType) {
            case START:
                offsetX = bounds.left + paddingHor;
                break;
            case MIDDLE:
                offsetX = bounds.left - paddingHor - size;
                break;
            case END:
                offsetX = bounds.left - RECT_F.width() + paddingHor + size;
                break;
            default:
                throw new IllegalArgumentException();
        }
        RECT_F.offsetTo(offsetX, bounds.top + paddingVer);
        PATH.reset();
        PATH.addRoundRect(
                RECT_F, radius, radius, Path.Direction.CW
        );
        canvas.save();
        canvas.clipRect(bounds);
        canvas.drawPath(PATH, paint);
        canvas.restore();
    }

    public static void drawPeriodArc(@PeriodType int periodType, Paint paint, Canvas canvas, RectF bounds, float scale, float arcRadius) {
        final float size = Math.min(bounds.height(), bounds.width()) * scale;
        final float paddingHor = (1f * bounds.width() - size) / 2f;
        final float paddingVer = (1f * bounds.height() - size) / 2f;
        RECT_F.set(0, 0, 3 * bounds.width() - paddingHor * 2, size);
        RECT_F.offsetTo(0, bounds.top + paddingVer);
        PATH.reset();
        PATH2.reset();
        PATH.addCircle(arcRadius, arcRadius, arcRadius - RECT_F.top, Path.Direction.CW);
        PATH.addCircle(arcRadius, arcRadius, arcRadius - RECT_F.bottom, Path.Direction.CCW);
        PATH2.moveTo(arcRadius, arcRadius);
        switch (periodType) {
            case START:
                PATH2.lineTo(bounds.centerX(), bounds.top);
                PATH2.lineTo(bounds.right, bounds.top);
//                drawCircle(paint, canvas, bounds, scale);
                break;
            case MIDDLE:
                PATH2.lineTo(bounds.right, bounds.top);
                PATH2.lineTo(bounds.left, bounds.top);
                break;
            case END:
                PATH2.lineTo(bounds.centerX(), bounds.top);
                PATH2.lineTo(bounds.left, bounds.top);
//                drawCircle(paint, canvas, bounds, scale);
                break;
            case SINGLE:
                break;
            default:
                throw new IllegalArgumentException();
        }
        if (periodType != SINGLE) {
            PATH.op(PATH2, Path.Op.INTERSECT);
        }
        canvas.drawPath(PATH, paint);
    }

    @ColorInt
    public static int getPrimaryTextColor(Context context, boolean enabled) {
        return getAttrColor(context, android.R.attr.textColorPrimary, enabled);
    }

    @ColorInt
    public static int getSecondaryTextColor(Context context, boolean enabled) {
        return getAttrColor(context, android.R.attr.textColorSecondary, enabled);
    }

    @ColorInt
    public static int getAttrColor(Context context, @AttrRes int attr, boolean enabled) {
        TypedArray themeArray = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            int index = 0;
            int defaultColourValue = Color.MAGENTA;
            ColorStateList stateList = themeArray.getColorStateList(index);
            if (stateList != null) {
                return stateList.getColorForState(new int[]{android.R.attr.state_enabled * (enabled ? 1 : -1)}, defaultColourValue);
            } else {
                return themeArray.getColor(index, defaultColourValue);
            }
        } finally {
            themeArray.recycle();
        }
    }

    @ColorInt
    public static int getAttrColor(Context context, @AttrRes int attr) {
        return getAttrColor(context, attr, true);
    }

    @IntDef({START, MIDDLE, END, SINGLE})
    @Retention(RetentionPolicy.SOURCE)
    @interface PeriodType {
    }
}
