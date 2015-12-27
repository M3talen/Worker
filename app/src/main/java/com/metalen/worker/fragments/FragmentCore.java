package com.metalen.worker.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;

/**
 * Created by Metalen on 24.1.2015..
 */
public class FragmentCore extends Fragment {


    protected static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void revealShow(View rootView, boolean reveal, final AlertDialog dialog, int dialogViewID, int FABViewID, boolean a) {
        final View view = rootView.findViewById(dialogViewID);
        int w = view.getWidth();
        int h = view.getHeight();
        int tx = (int) view.getX();
        int ty = (int) view.getY();
        int ix = (int) view.findViewById(FABViewID).getX();
        int iy = (int) view.findViewById(FABViewID).getY();
        int ih = view.findViewById(FABViewID).getHeight();
        int iw = view.findViewById(FABViewID).getWidth();

        // float maxRadius = (float) Math.sqrt(w * 0.8*w + h * 0.8*h);
        float maxRadius = (float) Math.sqrt(w * w + h * h) + 10;

        if (reveal) {
            //Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, w-(w/2+ix), h-(h/2+iy), 0, maxRadius);
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, w / 2, h / 2, 0, maxRadius);

            view.setVisibility(View.VISIBLE);
            revealAnimator.start();

        } else {
            Animator anim = ViewAnimationUtils.createCircularReveal(view, ix + (ih / 2), iy + (iw / 2), maxRadius, ih).setDuration(300);
            //Animator anim = ViewAnimationUtils.createCircularReveal(view, w / 2, h / 2, maxRadius, 0).setDuration(300);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);
                }
            });

            anim.start();
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void revealShow(View rootView, boolean reveal, final AlertDialog dialog, int dialogViewID, int FABViewID) {
        final View view = rootView.findViewById(dialogViewID);
        int w = view.getWidth();
        int h = view.getHeight();
        int tx = (int) view.getX();
        int ty = (int) view.getY();
        int ix = (int) view.findViewById(FABViewID).getX();
        int iy = (int) view.findViewById(FABViewID).getY();
        int ih = view.findViewById(FABViewID).getHeight();
        int iw = view.findViewById(FABViewID).getWidth();

        // float maxRadius = (float) Math.sqrt(w * 0.8*w + h * 0.8*h);
        float maxRadius = (float) Math.sqrt(w * w + h * h) + 10;

        if (reveal) {
            //Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, w-(w/2+ix), h-(h/2+iy), 0, maxRadius);
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, w / 2, h / 2, 0, maxRadius);

            view.setVisibility(View.VISIBLE);
            revealAnimator.start();

        } else {
            Animator anim = ViewAnimationUtils.createCircularReveal(view, ix + (ih / 2), iy + (iw / 2), maxRadius, 0).setDuration(300);
            //Animator anim = ViewAnimationUtils.createCircularReveal(view, w / 2, h / 2, maxRadius, 0).setDuration(300);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);
                }
            });

            anim.start();
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void revealShow(View rootView, boolean reveal, final AlertDialog dialog, int dialogViewID, int FABViewID, int x, int y) {
        final View view = rootView.findViewById(dialogViewID);
        int w = view.getWidth();
        int h = view.getHeight();
        int dx = (int) view.getX();
        int dy = (int) view.getY();
        int tx = x;
        int ty = y;
        int ix = (int) view.findViewById(FABViewID).getX();
        int iy = (int) view.findViewById(FABViewID).getY();
        int ih = view.findViewById(FABViewID).getHeight();
        int iw = view.findViewById(FABViewID).getWidth();

        float maxRadius = (float) Math.sqrt(w * 0.8 * w + h * 0.8 * h);

        if (reveal) {
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, x, y, 0, maxRadius).setDuration(500);

            view.setVisibility(View.VISIBLE);
            revealAnimator.start();

        } else {
            Animator anim = ViewAnimationUtils.createCircularReveal(view, ix + (ih / 2), iy + (iw / 2), maxRadius, 0).setDuration(500);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);
                }
            });

            anim.start();
        }

    }

    protected int getPixels(int dipValue) {
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
        return px;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}
