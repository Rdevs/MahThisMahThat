package rdevs.mtmt.models;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * Text overlay model
 */
public class TextOverlay implements Parcelable {
    public static final String DEFAULT_TEXT = "Some text";
    public static final int DEFAULT_SCALE = 1;
    public static final float DEFAULT_LOCATION_X = 0.5f;
    public static final float DEFAULT_LOCATION_Y = 0.5f;
    public static final float DEFAULT_ROTATION = 0f;
    public static final int DEFAULT_TEXT_SIZE = 50;
    public static final Creator<TextOverlay> CREATOR = new Creator<TextOverlay>() {
        @Override
        public TextOverlay createFromParcel(Parcel source) {
            String text = source.readString();
            float scale = source.readFloat();
            float[] location = {0f, 0f};
            source.readFloatArray(location);
            float rotation = source.readFloat();
            int color = source.readInt();
            String font = source.readString();
            return new TextOverlay(text, scale, location, rotation, color, font);
        }

        @Override
        public TextOverlay[] newArray(int size) {
            return new TextOverlay[size];
        }
    };
    private static final int DEFAULT_COLOR = -16711681;
    private static final String DEFAULT_FONT = "";
    private String text_;
    private float scale_;
    private float[] location_ = {0f, 0f};
    private float rotation_;
    private int color_ = DEFAULT_COLOR;
    private String font_;

    public TextOverlay(String text, float scale, float[] location, float rotation, int color, String font) {
        this.text_ = text;
        this.scale_ = scale;
        this.location_ = location;
        this.rotation_ = rotation;
        this.color_ = color;
        this.font_ = font;
    }

    public TextOverlay() {
        this(DEFAULT_TEXT, DEFAULT_SCALE, new float[]{DEFAULT_LOCATION_X, DEFAULT_LOCATION_Y}, DEFAULT_ROTATION, DEFAULT_COLOR, DEFAULT_FONT);
    }

    public static boolean isDefault(TextOverlay textOverlay) {
        return DEFAULT_TEXT.equals(textOverlay.getText()) &&
                textOverlay.getScale() == DEFAULT_SCALE &&
                textOverlay.getLocation()[0] == DEFAULT_LOCATION_X &&
                textOverlay.getLocation()[1] == DEFAULT_LOCATION_Y &&
                textOverlay.getRotation() == DEFAULT_ROTATION &&
                textOverlay.getColor() == DEFAULT_COLOR &&
                DEFAULT_FONT.equals(textOverlay.getFont());
    }

    /**
     * Updates EditText with {@link TextOverlay} model and place it relative to view with content
     */
    public static void restoreTextOverlayFromModel(@NonNull EditText overlayTextView, @NonNull TextOverlay model, @NonNull View contentView) {
        overlayTextView.setText(model.getText());
        overlayTextView.setScaleX(model.getScale());
        overlayTextView.setScaleY(model.getScale());
        overlayTextView.setRotation(model.getRotation());
        overlayTextView.setTextColor(model.getColor());

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.leftMargin = (int) (contentView.getWidth() * model.getLocation()[0]);
        layoutParams.rightMargin = Integer.MIN_VALUE / 4;

        layoutParams.topMargin = (int) (contentView.getHeight() * model.getLocation()[1]);
        layoutParams.bottomMargin = Integer.MIN_VALUE / 4;

        overlayTextView.setLayoutParams(layoutParams);
    }

    /**
     * Creates EditText and applies {@link TextOverlay} parameters relative to content view
     */
    public static EditText createOverlayEditText(Context context, TextOverlay model, View contentView) {
        EditText overlayEditText = new EditText(context);
        overlayEditText.setTextSize(DEFAULT_TEXT_SIZE);
        overlayEditText.setBackgroundColor(Color.TRANSPARENT);
        overlayEditText.setGravity(Gravity.CENTER);
        overlayEditText.setSingleLine(true);
        overlayEditText.setEllipsize(TextUtils.TruncateAt.END);
        TextOverlay.restoreTextOverlayFromModel(overlayEditText, model, contentView);
        return overlayEditText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text_);
        dest.writeFloat(scale_);
        dest.writeFloatArray(location_);
        dest.writeFloat(rotation_);
        dest.writeInt(color_);
        dest.writeString(font_);
    }

    public String getText() {
        return text_;
    }

    public void setText(String text) {
        this.text_ = text;
    }

    public float getScale() {
        return scale_;
    }

    public void setScale(float scale) {
        this.scale_ = scale;
    }

    public float[] getLocation() {
        return location_;
    }

    public void setLocation(float[] location) {
        this.location_ = location;
    }

    public float getRotation() {
        return rotation_;
    }

    public void setRotation(float rotation) {
        if (rotation > 360) {
            rotation = (rotation % 360);
        } else if (rotation < 0) {
            rotation = 360 + (rotation % 360);
        }
        this.rotation_ = rotation;
    }

    public int getColor() {
        return color_;
    }

    public void setColor(int color) {
        this.color_ = color;
    }

    public String getFont() {
        return font_;
    }

    public void setFont(String font) {
        this.font_ = font;
    }
}
