package com.mario22gmail.vasile.studentist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.View;

import com.matthewtamlin.android_utilities_library.helpers.BitmapEfficiencyHelper;
import com.matthewtamlin.android_utilities_library.helpers.ScreenSizeHelper;
import com.matthewtamlin.sliding_intro_screen_library.background.BackgroundManager;
import com.matthewtamlin.sliding_intro_screen_library.background.ColorBlender;
import com.matthewtamlin.sliding_intro_screen_library.buttons.IntroButton;
import com.matthewtamlin.sliding_intro_screen_library.core.IntroActivity;
import com.matthewtamlin.sliding_intro_screen_library.pages.ParallaxPage;
import com.matthewtamlin.sliding_intro_screen_library.transformers.MultiViewParallaxTransformer;

import java.util.ArrayList;
import java.util.Collection;

public class HotToUseActivity extends IntroActivity {

    private static final String DISPLAY_ONCE_THIS = "display_only_once";
    private static final String DISPLAY_ONCE_THIS_KEY = "display_only_once_key";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(IntroductionCompletedBefore())
        {
            Intent startActivity = new Intent(getApplicationContext(), com.mario22gmail.vasile.studentist.StartActivity.class);
            startActivity(startActivity);
        }

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        configureTransformer();
        configureBackground();

    }

    private static final int[] BACKGROUND_COLORS = {0xff304FFE, 0xffcc0066, 0xff9900ff};

    @Override
    protected Collection<Fragment> generatePages(Bundle savedInstanceState) {
// This variable holds the pages while they are being created
        final ArrayList<Fragment> pages = new ArrayList<>();

        // Get the screen dimensions so that Bitmaps can be loaded efficiently
        final int screenWidth = ScreenSizeHelper.getScreenWidthPx(this);
        final int screenHeight = ScreenSizeHelper.getScreenHeightPx(this);


        final Bitmap dentalImg = BitmapEfficiencyHelper.decodeResource(this, R.drawable.imgprotetica, screenWidth,screenHeight);

        // Create as many pages as there are background colors
        for (int i = 0; i < BACKGROUND_COLORS.length; i++) {
            final ParallaxPage newPage = ParallaxPage.newInstance();
            newPage.setFrontImage(dentalImg);
            pages.add(newPage);
        }

        return pages;

    }



    @Override
    protected IntroButton.Behaviour generateFinalButtonBehaviour() {
		/* The pending changes to the shared preferences editor will be applied when the
		 * introduction is successfully completed. By setting a flag in the pending edits and
		 * checking the status of the flag when the activity starts, the introduction screen can
		 * be skipped if it has previously been completed.
		 */
        final SharedPreferences sp = getSharedPreferences(DISPLAY_ONCE_THIS, MODE_PRIVATE);
        final SharedPreferences.Editor pendingEdits = sp.edit().putBoolean(DISPLAY_ONCE_THIS_KEY, false);

        // Define the next activity intent and create the Behaviour to use for the final button
        final Intent nextActivity = new Intent(this, StartActivity.class);
        return new IntroButton.ProgressToNextActivity(nextActivity, pendingEdits);
    }



    public boolean IntroductionCompletedBefore(){
        final SharedPreferences sp = getSharedPreferences(DISPLAY_ONCE_THIS, MODE_PRIVATE);
        return sp.getBoolean(DISPLAY_ONCE_THIS_KEY,false);
    }

    /**
     * Sets this IntroActivity to use a MultiViewParallaxTransformer page transformer.
     */
    private void configureTransformer() {
        final MultiViewParallaxTransformer transformer = new MultiViewParallaxTransformer();
        transformer.withParallaxView(R.id.page_fragment_imageHolderFront, 1.2f);
        setPageTransformer(false, transformer);
    }

    /**
     * Sets this IntroActivity to use a ColorBlender background manager.
     */
    private void configureBackground() {
        final BackgroundManager backgroundManager = new ColorBlender(BACKGROUND_COLORS);
        setBackgroundManager(backgroundManager);
    }
}
