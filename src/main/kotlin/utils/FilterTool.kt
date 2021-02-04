package utils

import org.springframework.data.domain.PageRequest
import utils.FilterTool
import java.util.stream.Collectors
import utils.StringTool
import org.hibernate.usertype.UserType
import kotlin.Throws
import org.hibernate.HibernateException
import java.sql.SQLException
import java.sql.ResultSet
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.sql.PreparedStatement
import java.util.*
import java.util.function.Consumer

/**
 * @author Stig Norland
 */
object FilterTool {
    fun defaultSort(pageable: Pageable?, vararg args: String?): PageRequest {
        assert(pageable != null)
        val sort: Sort
        sort = if (pageable!!.sort == null) defaultSort(*args) else {
            filterSort(pageable.sort, "responseDomain.name")
        }
        return PageRequest.of(
            pageable.pageNumber, pageable.pageSize, sort
        )
    }

    private fun filterSort(source: Sort, vararg args: String): Sort {
        val filterwords = Arrays.asList(*args)
        val orders: MutableList<Sort.Order> = ArrayList(0)
        source.iterator().forEachRemaining { o: Sort.Order -> if (!filterwords.contains(o.property)) orders.add(o) }
        return Sort.by(orders)
    }

    private fun defaultSort(vararg args: String): Sort {
        return Sort.by(
            Arrays.stream(args).map { s: String ->
                val par = s.split(" ").toTypedArray()
                if (par.size > 1) return@map Sort.Order(
                    Sort.Direction.fromString(
                        par[1]
                    ), par[0]
                ) else return@map Sort.Order(Sort.Direction.ASC, par[0])
            }.collect(Collectors.toList())
        )
    }

    fun referencedSort(pageable: Pageable?): PageRequest {
        assert(pageable != null)
        val orders: MutableList<Sort.Order> = LinkedList()
        pageable!!.sort.forEach(Consumer { o: Sort.Order ->
            if (o.property != "modified") {
                orders.add(o)
            }
        })
        if (orders.size == 0) orders.add(Sort.Order(Sort.Direction.ASC, "kind"))
        orders.add(Sort.Order(Sort.Direction.ASC, "antall"))
        return PageRequest.of(pageable.pageNumber, pageable.pageSize, Sort.by(orders))
    }

    fun defaultOrModifiedSort(pageable: Pageable?, vararg args: String?): PageRequest {
        assert(pageable != null)
        val sort: Sort
        sort = if (pageable!!.sort == null) defaultSort(*args) else modifiedSort(
            pageable.sort
        )
        return PageRequest.of(pageable.pageNumber, pageable.pageSize, sort)
    }

    private fun modifiedSort(sort: Sort): Sort {
        val orders: MutableList<Sort.Order> = LinkedList()
        sort.forEach(Consumer { o: Sort.Order ->
            if (o.property == "modified") {
                orders.add(Sort.Order(o.direction, "updated"))
            } else if (o.property == "responseDomainName") {
                orders.add(Sort.Order(o.direction, "responsedomain_name"))
            } else if (o.property == "questionName") {
                orders.add(Sort.Order(o.direction, "question_name"))
            } else if (o.property == "questionText") {
                orders.add(Sort.Order(o.direction, "question_text"))
            } else if (o.property == "status.label") {
                orders.add(Sort.Order(o.direction, "statuslabel"))
            } else if (o.property == "categoryType") {
                orders.add(Sort.Order(o.direction, "category_kind"))
            } else orders.add(o)
        })
        return Sort.by(orders)
    }
}
