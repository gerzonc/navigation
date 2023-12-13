package com.navigation.reactnative;

import android.os.Build;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.views.text.ReactTypefaceUtils;
import com.facebook.react.views.text.ReactFontManager;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class ExtendedFloatingActionButtonView extends ExtendedFloatingActionButton {
    final int defaultBackgroundColor;
    final int defaultColor;
    final int defaultRippleColor;
    final CoordinatorLayout.LayoutParams params;
    int marginTop, marginRight, marginBottom, marginLeft, marginStart, marginEnd, margin;
    private String text;
    private String fontFamily;
    private String fontWeight;
    private String fontStyle;
    private Integer fontSize;
    private boolean textChanged = false;
    private final IconResolver.IconResolverListener iconResolverListener;
    private String anchor;

    public ExtendedFloatingActionButtonView(@NonNull Context context) {
        super(context);
        defaultBackgroundColor = getBackgroundTintList() != null ? getBackgroundTintList().getColorForState(new int[]{ android.R.attr.state_enabled }, Color.BLACK) : Color.BLACK;
        defaultRippleColor = getRippleColor() != null ? getRippleColor().getColorForState(new int[]{ android.R.attr.state_pressed }, Color.WHITE) : Color.WHITE;
        defaultColor = getCurrentTextColor();
        params = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setBehavior(getBehavior());
        setLayoutParams(params);
        iconResolverListener = this::setIcon;
        setOnClickListener(view -> {
            EventDispatcher eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag((ReactContext) context, getId());
            eventDispatcher.dispatchEvent(new PressEvent(getId()));
        });
    }

    void setAnchor(String anchor) {
        this.anchor = anchor;
        if (this.anchor != null && getParent() instanceof CoordinatorLayoutView) {
            CoordinatorLayoutView coordinatorLayoutView = (CoordinatorLayoutView) getParent();
            for(int i = 0; i < coordinatorLayoutView.getChildCount(); i++) {
                View child = coordinatorLayoutView.getChildAt(i);
                if ((this.anchor.equals("navigationBar") && child instanceof NavigationBarView)
                    || (this.anchor.equals("bottomNavigationBar") && child instanceof BottomAppBarView)
                    || (this.anchor.equals("bottomSheet") && child instanceof BottomSheetView)) {
                    this.params.setAnchorId(child.getId());
                    coordinatorLayoutView.requestLayout();
                }
            }
        }
    }

    void setIconSource(@Nullable ReadableMap source) {
        IconResolver.setIconSource(source, iconResolverListener, getContext());
    }

    void setActionText(String text) {
        this.text = text;
        textChanged = true;
    }

    void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
        textChanged = true;
    }

    void setFontWeight(String fontWeight) {
        this.fontWeight = fontWeight;
        textChanged = true;
    }

    void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
        textChanged = true;
    }

    void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
        textChanged = true;
    }

    void styleText() {
        if (textChanged) {
            SpannableString textSpannable = null;
            if (text != null) {
                textSpannable = new SpannableString(text);
                 if (fontFamily != null){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                        textSpannable.setSpan(new TypefaceSpan(ReactFontManager.getInstance().getTypeface(fontFamily, 0, getContext().getAssets())), 0, text.length(), 0);
                    else
                        textSpannable.setSpan(new TypefaceSpan(fontFamily), 0, text.length(), 0);
                }
                if (fontWeight != null)
                    textSpannable.setSpan(new StyleSpan(ReactTypefaceUtils.parseFontWeight(fontWeight)), 0, text.length(), 0);
                if (fontStyle != null)
                    textSpannable.setSpan(new StyleSpan(ReactTypefaceUtils.parseFontStyle(fontStyle)), 0, text.length(), 0);
                if (fontSize != null)
                    textSpannable.setSpan(new AbsoluteSizeSpan(fontSize, true), 0, text.length(), 0);
            }
            setText(textSpannable);
            textChanged = false;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.setAnchor(this.anchor);
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        if (getParent() != null) {
            measure(
                MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
            layout(getLeft(), getTop(), getRight(), getBottom());
        }
    }

    static class PressEvent extends Event<ExtendedFloatingActionButtonView.PressEvent> {
        public PressEvent(int viewId) {
            super(viewId);
        }

        @Override
        public String getEventName() {
            return "topPress";
        }

        @Override
        public void dispatch(RCTEventEmitter rctEventEmitter) {
            rctEventEmitter.receiveEvent(getViewTag(), getEventName(), Arguments.createMap());
        }
    }
}
