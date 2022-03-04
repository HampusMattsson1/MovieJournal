package com.moviejournal2

import android.app.Activity
import android.app.PendingIntent.getActivity
import androidx.core.content.MimeTypeFilter.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.EspressoException
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import kotlinx.coroutines.delay
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.not

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule
import java.util.regex.Pattern.matches
import androidx.test.rule.ActivityTestRule




/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class UITests {

//    @get:Rule val mActivityRule: ActivityTestRule<MainActivity> = ActivityTestRule<MainActivity>(MainActivity::class.java)
    @get:Rule val mActivityRule: ActivityTestRule<sign_in> = ActivityTestRule<sign_in>(sign_in::class.java)
//    var mActivityRule: ActivityTestRule<MainActivity> = ActivityTestRule<MainActivity>(MainActivity::class.java)

//    @get:Rule var a : ActivityScenarioRule<sign_in> = ActivityScenarioRule(sign_in::class.java)


    // Login tests
    @Test
    fun loginWithoutCredentials() {
        onView(withId(R.id.btn_login)).perform(click())

        Thread.sleep(500)

        onView(withText("Please enter email")).inRoot(withDecorView(not(
            CoreMatchers.`is`(
                mActivityRule.getActivity()!!.getWindow().getDecorView()
            )
        ))).check(matches(isDisplayed()))
    }

    @Test
    fun loginWithoutEmail() {
        onView(withId(R.id.login_textEditPassword)).perform(replaceText("hamt19"))
        onView(withId(R.id.btn_login)).perform(click())

        Thread.sleep(500)

        onView(withText("Please enter email")).inRoot(withDecorView(not(
            CoreMatchers.`is`(
                mActivityRule.getActivity()!!.getWindow().getDecorView()
            )
        ))).check(matches(isDisplayed()))
    }

    @Test
    fun loginWithoutPassword() {
        onView(withId(R.id.login_textEditEmail)).perform(replaceText("hjm@ownit.nu"))
        onView(withId(R.id.btn_login)).perform(click())

        Thread.sleep(500)

        onView(withText("Please enter password")).inRoot(withDecorView(not(
            CoreMatchers.`is`(
                mActivityRule.getActivity()!!.getWindow().getDecorView()
            )
        ))).check(matches(isDisplayed()))
    }

    @Test
    fun loginSuccess() {
        onView(withId(R.id.login_textEditEmail)).perform(replaceText("hjm@ownit.nu"))
        onView(withId(R.id.login_textEditPassword)).perform(replaceText("hamt19"))
        onView(withId(R.id.btn_login)).perform(click())

        Thread.sleep(500)

        onView(withText("You are logged in successfully")).inRoot(withDecorView(not(
            CoreMatchers.`is`(
                mActivityRule.getActivity()!!.getWindow().getDecorView()
            )
        ))).check(matches(isDisplayed()))
    }

    @Test
    fun movieSearch() {
        onView(withId(R.id.login_textEditEmail)).perform(replaceText("hjm@ownit.nu"))
        onView(withId(R.id.login_textEditPassword)).perform(replaceText("hamt19"))
        onView(withId(R.id.btn_login)).perform(click())
        Thread.sleep(3000)

        onView(withId(R.id.ic_search)).perform(click())
        onView(withId(R.id.textEditSearch)).perform(replaceText("spider-man"))
        onView(withId(R.id.btn_search)).perform(click())
    }

    @Test
    fun searchUser() {
        onView(withId(R.id.login_textEditEmail)).perform(replaceText("hjm@ownit.nu"))
        onView(withId(R.id.login_textEditPassword)).perform(replaceText("hamt19"))
        onView(withId(R.id.btn_login)).perform(click())
        Thread.sleep(3000)

        onView(withId(R.id.ic_people)).perform(click())
        onView(withId(R.id.userSearch)).perform(replaceText("batman"))
        onView(withId(R.id.searchButton)).perform(click())
        Thread.sleep(2000)
        onView(withText("batman")).check(matches(isDisplayed()))
    }

    @Test
    fun editProfile() {
        onView(withId(R.id.login_textEditEmail)).perform(replaceText("hjm@ownit.nu"))
        onView(withId(R.id.login_textEditPassword)).perform(replaceText("hamt19"))
        onView(withId(R.id.btn_login)).perform(click())
        Thread.sleep(3000)

        onView(withId(R.id.ic_profile)).perform(click())
        onView(withId(R.id.settings)).perform(click())
        onView(withId(R.id.username)).perform(replaceText("UITest"))
        onView(withId(R.id.username)).perform(swipeUp())
        Thread.sleep(3000)
        onView(withId(R.id.save)).perform(click())
        Thread.sleep(5000)

        Espresso.pressBack()
        Thread.sleep(2000)
        onView(withText("UITest")).check(matches(isDisplayed()))
    }

}