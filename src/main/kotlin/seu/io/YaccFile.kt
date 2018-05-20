package seu.io

import java.io.BufferedReader
import java.io.FileReader

class YaccFile(filePath: String) {

    private var reader: BufferedReader = BufferedReader(FileReader(filePath))

    var headers: StringBuffer = StringBuffer()
    var instructions: HashMap<String, HashSet<String>> = HashMap()
    var rules: HashMap<String, ArrayList<String>> = HashMap()
    var userSeg: StringBuffer = StringBuffer()

}