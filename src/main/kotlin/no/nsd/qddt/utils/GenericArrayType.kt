package no.nsd.qddt.utils
//
//import org.hibernate.HibernateException
//import org.hibernate.engine.spi.SharedSessionContractImplementor
//import org.hibernate.usertype.UserType
//import java.io.Serializable
//import java.sql.PreparedStatement
//import java.sql.ResultSet
//import java.sql.SQLException
//import java.sql.Types
//
///**
// * @author Stig Norland
// */
//class GenericArrayType<T : Serializable?> : UserType {
//    private val typeParameterClass: Class<T>? = null
//    @Throws(HibernateException::class)
//    override fun assemble(cached: Serializable, owner: Any): Any {
//        return deepCopy(cached)
//    }
//
//    @Throws(HibernateException::class)
//    override fun deepCopy(value: Any): Any {
//        return value
//    }
//
//    @Throws(HibernateException::class)
//    override fun disassemble(value: Any): Serializable {
//        return deepCopy(value) as T
//    }
//
//    @Throws(HibernateException::class)
//    override fun equals(x: Any, y: Any): Boolean {
//        return x == y
//    }
//
//    @Throws(HibernateException::class)
//    override fun hashCode(x: Any): Int {
//        return x.hashCode()
//    }
//
//    @Throws(HibernateException::class, SQLException::class)
//    override fun nullSafeGet(
//        rs: ResultSet,
//        names: Array<String>,
//        session: SharedSessionContractImplementor,
//        owner: Any
//    ): Any? {
//        if (rs.wasNull()) {
//            return null
//        }
//        if (rs.getArray(names[0]) == null) {
//            return arrayOfNulls<Int>(0)
//        }
//        val array = rs.getArray(names[0])
//        return array.array as T
//    }
//
//    @Throws(HibernateException::class, SQLException::class)
//    override fun nullSafeSet(st: PreparedStatement, value: Any, index: Int, session: SharedSessionContractImplementor) {
//        val connection = st.connection
//        val castObject = value as T
//        val array = connection.createArrayOf("integer", castObject as Array<Any?>)
//        st.setArray(index, array)
//    }
//
//    override fun isMutable(): Boolean {
//        return true
//    }
//
//    @Throws(HibernateException::class)
//    override fun replace(original: Any, target: Any, owner: Any): Any {
//        return original
//    }
//
//    override fun returnedClass(): Class<T> {
//        return typeParameterClass!!
//    }
//
//    override fun sqlTypes(): IntArray {
//        return intArrayOf(Types.ARRAY)
//    }
//
//    companion object {
//        protected val SQL_TYPES = intArrayOf(Types.ARRAY)
//    }
//}
