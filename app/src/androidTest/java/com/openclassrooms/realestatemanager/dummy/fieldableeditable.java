package com.openclassrooms.realestatemanager.dummy;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import com.openclassrooms.realestatemanager.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class fieldableeditable {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.WRITE_EXTERNAL_STORAGE");

    @Test
    public void fieldableeditable() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.buttonadd),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        floatingActionButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textInputEditText = onView(
                childAtPosition(
                        childAtPosition(
                                withId(R.id.edit_adresse),
                                0),
                        0));
        textInputEditText.perform(scrollTo(), replaceText("re"), closeSoftKeyboard());

        ViewInteraction textInputEditText2 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.edit_codepostal),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText2.perform(replaceText("6"), closeSoftKeyboard());

        ViewInteraction textInputEditText3 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.edit_prix),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText3.perform(replaceText("0"), closeSoftKeyboard());

        ViewInteraction textInputEditText4 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.edit_chambre),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText4.perform(replaceText("0"), closeSoftKeyboard());

        ViewInteraction textInputEditText5 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.edit_sdb),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText5.perform(click());

        ViewInteraction textInputEditText6 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.edit_sdb),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText6.perform(replaceText("5"), closeSoftKeyboard());

        ViewInteraction textInputEditText7 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.edit_surface),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText7.perform(replaceText("3"), closeSoftKeyboard());

        ViewInteraction textInputEditText8 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.edit_piece),
                                0),
                        0),
                        isDisplayed()));
        textInputEditText8.perform(replaceText("5"), closeSoftKeyboard());

        pressBack();

        ViewInteraction chip = onView(
                allOf(withId(R.id.check_magasin), withText("Shops"),
                        childAtPosition(
                                allOf(withId(R.id.relativeCBox),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                17)),
                                1)));
        chip.perform(scrollTo(), click());

        ViewInteraction chip2 = onView(
                allOf(withId(R.id.check_magasin), withText("Shops"),
                        childAtPosition(
                                allOf(withId(R.id.relativeCBox),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                17)),
                                1)));
        chip2.perform(scrollTo(), click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
