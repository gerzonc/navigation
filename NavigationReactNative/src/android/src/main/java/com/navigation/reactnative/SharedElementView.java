package com.navigation.reactnative;

import android.content.Context;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;

import com.google.android.material.transition.MaterialContainerTransform;

public class SharedElementView extends ViewGroup {
    final MaterialContainerTransform transition;
    final long defaultDuration;
    final int defaultFadeMode;

    public SharedElementView(Context context) {
        super(context);
        transition = new MaterialContainerTransform(context, false);
        defaultDuration = transition.getDuration();
        defaultFadeMode = transition.getFadeMode();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewParent ancestor = getParent();
        while (ancestor != null && !(ancestor instanceof SceneView))
            ancestor = ancestor.getParent();
        if (ancestor == null)
            return;
        final SceneView scene = (SceneView) ancestor;
        scene.sharedElements.add(this);
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                if (scene.transitioner != null)
                    scene.transitioner.load(SharedElementView.this);
                return true;
            }
        });
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
    }
}
