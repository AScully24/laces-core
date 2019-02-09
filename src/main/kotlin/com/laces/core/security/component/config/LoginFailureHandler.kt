package com.laces.core.security.component.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Service
import java.io.IOException
import java.util.*
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class LoginFailureHandler: AuthenticationFailureHandler {

    private val objectMapper = ObjectMapper()

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationFailure(
            request: HttpServletRequest,
            response: HttpServletResponse,
            exception: AuthenticationException) {

//        when (exception) {
//            is BadCredentialsException -> response.status = HttpStatus.UNAUTHORIZED.value()
//            is CredentialsExpiredException -> response.status = HttpStatus.FORBIDDEN.value()
//            is AccountLockedException -> response.status = HttpStatus.PAYMENT_REQUIRED.value()
//            is AccountExpiredException -> response.status = HttpStatus.PAYMENT_REQUIRED.value()
//            else -> response.status = HttpStatus.UNAUTHORIZED.value()
//        }
        response.status = HttpStatus.UNAUTHORIZED.value()
        val data = HashMap<String, Any?>()
        data["timestamp"] = Calendar.getInstance().time
        data["exception"] = exception.javaClass.simpleName
        data["message"] = exception.message


        response.outputStream
                .println(objectMapper.writeValueAsString(data))
    }
}