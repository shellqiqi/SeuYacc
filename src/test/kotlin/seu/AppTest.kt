package seu

import org.junit.BeforeClass
import org.junit.Test
import seu.io.YaccFile
import kotlin.test.assertEquals

class AppTest {

    companion object {
        private lateinit var yaccFile: YaccFile
        @BeforeClass @JvmStatic fun constructor() {
            yaccFile = YaccFile("resource/example.y")
        }
    }

    @Test fun readHeaders() {
        assertEquals("#include <ctype.h>\n", yaccFile.headers.toString())
    }

    @Test fun readInstructions() {
        val instructions: HashMap<String, String> = HashMap()
        instructions["DIGIT"] = "%token"
        assertEquals(instructions, yaccFile.instructions)
    }

    @Test fun readRules() {
        // TODO
    }

    @Test fun readUserSeg() {
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
