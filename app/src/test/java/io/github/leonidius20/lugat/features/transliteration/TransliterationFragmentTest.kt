package io.github.leonidius20.lugat.features.transliteration

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TransliterationFragmentTest {

    //private lateinit var fragmentScenario: FragmentScenario<TransliterationFragment>

    @Before
    fun setup() {
       // fragmentScenario = launchFragmentInContainer()
    }

    @Test
    fun `buttons should be shown according to state from viewmodel`() {
    }

    @Test
    fun `transliteration should be triggered when source text changes`() {
        //onView(withId(R.id.transliterationSourceText)).perform(typeText("тест"))

    }

    @Test
    fun `target text should update when viewmodel state updates`() {

    }

}