package com.moviejournal2

import android.app.Activity
import android.widget.Button
import com.google.common.truth.Truth.assertThat
import android.widget.EditText
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

// Login
class UnitTest1 {
//    var activity: Activity = MainActivity()
    var a: Activity = MainActivity()

    @Test
    fun validateEmail() {
        val email = a.findViewById<EditText>(R.id.login_textEditEmail)
        val pass = a.findViewById<EditText>(R.id.login_textEditPassword)
        email.setText("")
        pass.setText("")
        assertThat(a.findViewById<Button>(R.id.btn_login).performClick()).isTrue()
    }
//    val u = activity.findViewById<EditText>(R.id.login_textEditEmail)
//    val p = activity.findViewById<EditText>(R.id.login_textEditPassword)


}






class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}