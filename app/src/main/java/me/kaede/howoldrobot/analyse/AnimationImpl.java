/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot.analyse;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

import me.kaede.howoldrobot.analyse.presenter.IAnimation;

class AnimationImpl implements IAnimation {

    @Override
    public void doLogoAnimation(View view) {
        AnimationSet animationSet = new AnimationSet(false);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);
        // alphaAnimation.setInterpolator(new LinearInterpolator());

        TranslateAnimation translateAnimation = new TranslateAnimation(0f, 0f, 100f, 0f);
        translateAnimation.setDuration(500);
        translateAnimation.setFillAfter(true);
        // translateAnimation.setInterpolator(new LinearInterpolator());

        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(translateAnimation);
        view.startAnimation(animationSet);
    }

    @Override
    public void doIntroduceAnimation(View view) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(500);
        alphaAnimation.setStartOffset(500);
        alphaAnimation.setFillAfter(true);
        view.startAnimation(alphaAnimation);
    }
}
