package com.mlins.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.mlins.views.RotateImageAnimation;
import com.mlins.views.TouchImageView;

import java.util.LinkedList;
import java.util.Queue;

public class AnimationsHolder implements Cleanable, AnimationListener {

    private Queue<Animation> queue = new LinkedList<Animation>();
    private View mView = null;
    private Object lock = new Object();

    public static AnimationsHolder getInstance() {
        return Lookup.getInstance().get(AnimationsHolder.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(AnimationsHolder.class);
    }

    public void clean() {
        queue.clear();
    }

    public void addAnimation(Animation animation, View v) {
        synchronized (lock) {
            if (v != null && mView == null || mView != v) {
                mView = v;
            }
            if (animation != null && !queueContainsType(animation)) {
                queue.add(animation);
                if (queue.size() == 1 && mView != null) {
                    play();
                }
            }
        }
    }

    private boolean queueContainsType(Animation animation) {
        boolean result = false;
        for (Animation o : queue) {
            if (o instanceof TouchImageView.CenterImageAnimation && animation instanceof TouchImageView.CenterImageAnimation) {
                result = true;
                break;
            } else if (o instanceof RotateImageAnimation && animation instanceof RotateImageAnimation) {
                result = true;
                break;
            } else if (!(animation instanceof RotateImageAnimation) && !(animation instanceof TouchImageView.CenterImageAnimation)) {
                result = true;
                break;
            }
        }
        return result;
    }

    private void play() {
        Animation animation = queue.peek();
        if (animation != null && mView != null && !animation.hasStarted()) {
            mView.clearAnimation();
            mView.startAnimation(animation);
            animation.setAnimationListener(this);
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        synchronized (lock) {
            queue.remove(animation);
            play();
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub

    }

    public void reset() {
        if (mView != null) {
            mView.clearAnimation();
        }
        queue.clear();
    }
}
