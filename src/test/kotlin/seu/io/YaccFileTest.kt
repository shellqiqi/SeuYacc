package seu.io

import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import seu.lr.Symbol
import kotlin.test.assertEquals

class YaccFileTest {

    companion object {
        private lateinit var yaccFile: YaccFile
        @BeforeClass
        @JvmStatic
        fun constructor() {
            yaccFile = YaccFile("resource/example.y")
        }
    }

    @Test
    fun readHeaders() {
        assertEquals("#include <ctype.h>\n", yaccFile.headers.toString())
    }

    @Test
    fun readInstructions() {
        val tokens: HashSet<String> = HashSet()
        tokens.add("DIGIT")
        tokens.add("HELLO")
        assertEquals(tokens, yaccFile.tokens)
        assertEquals(Symbol(Symbol.NON_TERMINAL, "line"), yaccFile.start)
    }

    @Test
    @Ignore
    fun readRules() {
        println("rules:")
        yaccFile.rules.forEach { (t, u) -> println(t.toString() + "\t" + (u ?: "")) }
    }

    @Test
    fun readUserSeg() {
        assertEquals("""
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