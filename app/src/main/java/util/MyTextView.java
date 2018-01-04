package util;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextView extends TextView {

    private int mLineY = 0; //总行高

    private int mViewWidth; // TextView高度

    private TextPaint paint;

    public MyTextView(Context context) {
        super(context);
        init();
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        paint = getPaint();
        paint.setColor(getCurrentTextColor());
        paint.drawableState = getDrawableState();
    }





    @Override
    public void onDraw(Canvas canvas) {
        mLineY = 0;
        mViewWidth = getMeasuredWidth();//获取textview的实际宽度  
        mLineY += getTextSize();

        String text = getText().toString();
        Layout layout = getLayout();
        int lineCount = layout.getLineCount();
        for (int i = 0; i < lineCount; i++) {
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            String lineText = text.substring(lineStart, lineEnd);
            if (needSclae(lineText)) {
                if (i == lineCount - 1) {
                    canvas.drawText(lineText, 0, mLineY, paint);
                } else {
                    float width = StaticLayout.getDesiredWidth(text, lineStart, lineEnd, paint);
                    drawScaleText(canvas, lineText, width);
                }
            } else {
                canvas.drawText(lineText, 0, mLineY, paint);
            }
            mLineY += getLineHeight();
        }
    }

    /** 
      * 判断需不需要缩放. 
      * 
      * @param lineText 该行所有的文字 
      * @return true 该行最后一个字符不是换行符  false 该行最后一个字符是换行符 
      */
    private boolean needSclae(String lineText) {
        if(lineText.length()==0)
        {
            return false;
        } else {
            return lineText.charAt(lineText.length() -1 )!='\n';
        }
    }

    /** 
      * 重绘此行. 
      * 
      * @param canvas    画布 
      * @param lineText  该行所有的文字 
      * @param lineWidth 该行每个文字的宽度的总和 
      */
    private void drawScaleText(Canvas canvas , String lineText, float lineWidth) {
        float x = 0;
        if(isFisetLineOfParagraph(lineText)) {
            String blanks = "  ";
            canvas.drawText(blanks,x,mLineY,paint);
            float width = StaticLayout.getDesiredWidth(blanks,paint);
            x+=width;
            lineText=lineText.substring(3);
        }
        //比如说一共有5个字，中间有4个间隔，  
//那就用整个TextView的宽度 - 5个字的宽度，  
//然后除以4，填补到这4个空隙中  
        float interval =(mViewWidth-lineWidth) / (lineText.length() - 1) ;
        for (int i = 0; i < lineText.length(); i++) {
            String character = String.valueOf(lineText.charAt(i));
            float cw = StaticLayout.getDesiredWidth(character,paint);
            canvas.drawText(character,x,mLineY,paint);
            x+=(cw+interval);
        }
    }

      /** 
          * 判断是不是段落的第一行. 
          * 一个汉字相当于一个字符，此处判断是否为第一行的依据是： 
          * 字符长度大于3且前两个字符为空格 
          * 
          * @param lineText 该行所有的文字 
          */
    private boolean isFisetLineOfParagraph(String lineText) {
        return lineText.length()>3&&lineText.charAt(0)==' ' && lineText.charAt(1) == ' ';
    }

    public int getmLineY() {
        return mLineY;
    }

    public void setmLineY(int mLineY) {
        this.mLineY = mLineY;
    }

    public int getmViewWidth() {
        return mViewWidth;
    }

    public void setmViewWidth(int mViewWidth) {
        this.mViewWidth = mViewWidth;
    }

    @Override
    public TextPaint getPaint() {
        return paint;
    }

    public void setPaint(TextPaint paint) {
        this.paint = paint;
    }
}