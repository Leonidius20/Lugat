package io.github.leonidius20.lugat.data.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WordsDaoUnitTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: DictionaryDatabase



    @Before
    fun initDb() {
        db = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            DictionaryDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    // so that we only depend on the constructor in one place
    private fun createWord(latin: String, cyrillic: String): WordInDb {
        TODO()
    }

    @Test
    fun findsAnExactMatchInLatinWord() = runTest {
        db.testDao().insertWord(WordInDb(0, "кьаве", "qave", "кофе", WordInDb.Language.CRIMEAN_TATAR))

        val query = "qave"
        val expectedWordLatin = "qave"

        val results = db.wordsDao().search(query)

        assertTrue(results.find { it.wordLatin == expectedWordLatin } != null)
    }

    @Test
    fun findsAnExactMatchInCyrillicWord() {

    }

    @Test
    fun findsAnExactMatchInDefinition() {

    }

    @Test
    fun findsAWordWhereLatinVersionStartsWithQuery() {

    }

    @Test
    fun findsAWordWhereCyrillicVersionStartsWithQuery() {

    }

    @After
    fun closeDb() {
        db.close()
    }

}