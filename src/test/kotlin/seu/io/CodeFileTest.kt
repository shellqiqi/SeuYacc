package seu.io

import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import seu.lr.LR

class CodeFileTest {
    companion object {
        private lateinit var yaccFile: YaccFile
        private lateinit var lr: LR
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            yaccFile = YaccFile("resource/example4.y")
            lr = LR(yaccFile.rules.keys.toList(), yaccFile.start)

        }
    }
    @Test
    @Ignore
    fun writeHeadersTest() {
        print(yaccFile.headers)
    }

    @Test
    @Ignore
    fun writeUserSegTest() {
        print(yaccFile.userSeg)
    }
}