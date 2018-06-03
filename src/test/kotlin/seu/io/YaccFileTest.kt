package seu.io

import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test

class YaccFileTest {

    companion object {
        private lateinit var yaccFile: YaccFile
        @BeforeClass
        @JvmStatic fun constructor() {
            yaccFile = YaccFile("resource/example.y")
        }
    }

    @Test
    fun readHeaders() {
        kotlin.test.assertEquals("#include <ctype.h>\n", yaccFile.headers.toString())
    }

    @Test
    fun readInstructions() {
        val instructions: HashMap<String, String> = HashMap()
        instructions["DIGIT"] = "%token"
        instructions["HELLO"] = "%token"
        kotlin.test.assertEquals(instructions, yaccFile.instructions)
    }

    @Test
    @Ignore
    fun readRules() {
        println("rules:")
        yaccFile.rules.forEach { (t, u) -> println(t.toString() + "\t" + (u ?: "")) }
    }

    @Test
    fun readUserSeg() {
        kotlin.test.assertEquals("""
        yylex() {
            int c;
            c = getchar();
            if (isdigit(c)) {
                yylval = c-'0';
                return DIGIT;
            }
            return c;
        }

        """.trimIndent(), yaccFile.userSeg.toString())
    }
}