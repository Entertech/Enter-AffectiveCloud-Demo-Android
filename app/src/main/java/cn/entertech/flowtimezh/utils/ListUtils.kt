package cn.entertech.flowtimezh.utils

fun convertList2String(list: List<Any>?): String {
    if (list == null){
        return ""
    }
    var str = ""
    for (i in list.indices) {
        str = if (i == list.size - 1) {
            "$str${list[i]}"
        } else {
            "$str${list[i]},"
        }
    }
    return str
}

fun convertString2List(string: String?,splitChart:String = ","):List<String>{
    if (string == null || string == ""){
        return listOf()
    }
    return string.split(splitChart)?.toList()
}