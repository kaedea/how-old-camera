/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot.analyse;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

import me.kaede.howoldrobot.analyse.presenter.IAnimation;

class AnimationImpl implements IAnimation {

    @Override
    public void doLogoAnimation(View view) {
        AnimationSet animationSet = new AnimationSet(false);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setFillAfter(true);

        TranslateAnimation translateAnimation = new TranslateAnimation(0f, 0f, 100f, 0f);
        translateAnimation.setDuration(1000);
        translateAnimation.setFillAfter(true);

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
