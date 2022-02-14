package com.moviejournal2

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
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import kotlinx.coroutines.delay

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule
import java.util.regex.Pattern.matches

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
//        onView(withText("Please enter email").check(matches(isDisplayed())))
//        onView(withId(R.id.btn_login))
//            .perform(click())
//            .check(matches(isDisplayed()))
    }

    @Test
    fun loginWithoutEmail() {
        onView(withId(R.id.login_textEditPassword)).perform(replaceText("hamt19"))
        onView(withId(R.id.btn_login)).perform(click())
    }

    @Test
    fun loginWithoutPassword() {
        onView(withId(R.id.login_textEditEmail)).perform(replaceText("hjm@ownit.nu"))
        onView(withId(R.id.btn_login)).perform(click())
    }

    @Test
    fun loginSuccess() {
        onView(withId(R.id.login_textEditEmail)).perform(replaceText("hjm@ownit.nu"))
        onView(withId(R.id.login_textEditPassword)).perform(replaceText("hamt19"))
        onView(withId(R.id.btn_login)).perform(click())
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
    }

    @Test
    fun test() {
//        onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()))

//        onView(withText("Page Two Title")).check(isDisplayed())

//        onView(withId(R.id.login_textEditEmail)).perform(replaceText("hjm@ownit.nu"))
//        onView(withId(R.id.login_textEditPassword)).perform(replaceText("hamt19"))
//        onView(withId(R.id.btn_login)).perform(click())
//        Thread.sleep(2000)

//        onView(withId(R.id.ic_journal)).perform(click())

//        onView(withText("Your watchlist")).check(matches(isDisplayed()))



    }


}