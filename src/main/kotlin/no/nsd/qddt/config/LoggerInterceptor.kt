package no.nsd.qddt.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest
import java.util.Enumeration



class LoggerInterceptor : HandlerInterceptor {

    private val log: Logger = LoggerFactory.getLogger(LoggerInterceptor::class.java)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        log.info("[preHandle][" + request + "]" + "[" + request.method+ "]" + request.requestURI + getParameters(request))
        return super.preHandle(request, response, handler)
    }


    private fun getParameters(request: HttpServletRequest): String? {
        val posted = StringBuffer()
        val e: Enumeration<*>? = request.parameterNames
        if (e != null) {
            posted.append("?")
        }
        while (e!!.hasMoreElements()) {
            if (posted.length > 1) {
                posted.append("&")
            }
            val curr = e.nextElement() as String
            posted.append("$curr=")
            if (curr.contains("password")
                || curr.contains("pass")
                || curr.contains("pwd")
            ) {
                posted.append("*****")
            } else {
                posted.append(request.getParameter(curr))
            }
        }
        val ip = request.getHeader("X-FORWARDED-FOR")
        val ipAddr: String = ip ?: getRemoteAddr(request)
        if (ipAddr != null && ipAddr != "") {
            posted.append("&_psip=$ipAddr")
        }
        return posted.toString()
    }

    private fun getRemoteAddr(request: HttpServletRequest): String {
        val ipFromHeader = request.getHeader("X-FORWARDED-FOR")
        if (ipFromHeader != null && ipFromHeader.isNotEmpty()) {
            log.debug("ip from proxy - X-FORWARDED-FOR : $ipFromHeader")
            return ipFromHeader
        }
        return request.remoteAddr
    }
}

