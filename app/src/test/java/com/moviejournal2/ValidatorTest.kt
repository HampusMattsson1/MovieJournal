package com.moviejournal2

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import junit.framework.Assert.assertEquals
import junit.framework.TestCase.assertEquals
import org.assertj.core.api.Assertions
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class ValidatorTest {

//    @Test
//    fun whenValid() {
//        val email = "example@example.com"
//        val pass = "test"
////        val res = Validator.validateLogin(email,pass)
////        Assertions.assertEquals(res)
//    }

    var a: sign_in? = null

    @Before
    fun setup() {
        a = sign_in()
    }


    @Test
    fun testLogin() {
//        val a: sign_in = getActivity(this, 1, )
        val e = a!!.findViewById<EditText>(R.id.login_textEditEmail)
        e.setText("test")
        val p = a!!.findViewById<EditText>(R.id.login_textEditPassword)
        p.setText("test")
        val res = a!!.findViewById<Button>(R.id.btn_login).performClick()


        val test = a!!.findViewById<EditText>(R.id.login_textEditEmail)
//        Assertions.assertThat(res).isEqualTo(false)
        Assertions.assertThat(test).isNotNull
    }

}