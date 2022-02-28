package no.nsd.qddt.config

import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


/**
 * @author Stig Norland
 */
//class CacheControlFilter : OncePerRequestFilter() {
//
//    @Throws(ServletException::class, IOException::class)
//    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
//        response.addHeader("Cache-Control", "private, max-age=600")
//        filterChain.doFilter(request, response)
//    }
//
//
//}
