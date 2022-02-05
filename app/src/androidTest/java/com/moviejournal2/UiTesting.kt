package com.moviejournal2

import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import kotlinx.coroutines.delay

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class UITests {

    @get:Rule var a : ActivityScenarioRule<sign_in> = ActivityScenarioRule(sign_in::class.java)


    // Login tests
    @Test
    fun loginWithoutCredentials() {
        onView(withId(R.id.btn_login)).perform(click())
    }

    @Test
    fun loginTest1() {
        onView(withId(R.id.login_textEditEmail)).perform(replaceText("hjm@ownit.nu"))
        onView(withId(R.id.login_textEditPassword)).perform(replaceText("hamt19"))
        onView(withId(R.id.btn_login)).perform(click())
    }



}