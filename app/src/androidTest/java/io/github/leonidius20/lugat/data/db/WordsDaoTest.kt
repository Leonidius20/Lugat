package io.github.leonidius20.lugat.data.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import junit.framework.TestCase.assertTrue
import java.io.File

@RunWith(AndroidJUnit4::class)
@SmallTest
class WordsDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: DictionaryDatabase



    @Before
    fun initDb() {
        db = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            DictionaryDatabase::class.java
        ).build()
    }

    // so that we only depend on the constructor in one place
    private fun createWord(): WordInDb {
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