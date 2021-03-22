//package no.nsd.qddt.model.embedded
//
//import com.fasterxml.jackson.databind.JavaType
//import com.fasterxml.jackson.databind.type.TypeFactory
//import com.fasterxml.jackson.databind.util.Converter
//import kotlin.reflect.typeOf
//
//class KotlinVersionConverter : Converter<String,Version> {
//
//    override fun convert(value: String?): Version {
//        val del = value?.split(", ")?: listOf<String>()
//        return when (del.size) {
//            4 ->
//                Version(del[0].toInt(), del[1].toInt(), revision = del[2].toInt(), del[3])
//            3 ->
//                    try {
//                        Version(del[0].toInt(), del[1].toInt(),revision = del[2].toInt())
//                    } catch (ex: NumberFormatException ){
//                        Version(del[0].toInt(), del[1].toInt(),revision = null,del[2])
//                    }
//            2 ->
//                Version(del[0].toInt(), del[1].toInt())
//            else ->
//                Version()
//        }
//    }
//
//    override fun getInputType(typeFactory: TypeFactory?): JavaType {
//        return typeFactory!!.constructType(String::class.java)
//    }
//
//    override fun getOutputType(typeFactory: TypeFactory?): JavaType {
//        return typeFactory!!.constructType(Version::class.java)
//    }
//}