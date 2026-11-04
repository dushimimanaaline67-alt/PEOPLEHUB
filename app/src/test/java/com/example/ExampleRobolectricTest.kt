package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.util.IdGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("PeopleHub", appName)
  }

  @Test
  fun `verify automated ID generator sequence`() {
    val firstId = IdGenerator.generateNextId(null, "PCD")
    assertTrue(firstId.startsWith("PCD-"))
    assertTrue(firstId.endsWith("-000001"))

    val nextId = IdGenerator.generateNextId(firstId, "PCD")
    assertTrue(nextId.endsWith("-000002"))
  }
}
