package seu.io

import org.junit.Test

import org.junit.Assert.*
import org.junit.BeforeClass
import org.junit.Ignore
import seu.lr.LR

class CodeFileTest {

    companion object {
        lateinit var codeFile: CodeFile
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            val yaccFile = YaccFile("resource/example.y")
            val lr = LR(yaccFile.rules.toList(), yaccFile.start)
            codeFile = CodeFile(yaccFile, lr)
        }
    }

    @Test
    @Ignore
    fun generateTest() {
        print(codeFile.generate())
    }

    @Test
    @Ignore
    fun includeTest() {
        print(codeFile.include())
    }

    @Test
    @Ignore
    fun productionsTest() {
        print(codeFile.productions())
    }

    @Test
    @Ignore
    fun actionsTest() {
        print(codeFile.actions())
    }

    @Test
    @Ignore
    fun parsingTableTest() {
        print(codeFile.parsingTable())
    }

    @Test
    @Ignore
    fun gotoTableTest() {
        print(codeFile.gotoTable())
    }
}