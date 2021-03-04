package com.laces.core.security.component.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
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


        val data = HashMap<String, Any?>()
        data["timestamp"] = Calendar.getInstance().time
        data["exception"] = exception.javaClass.simpleName
        data["message"] = exception.message

        when (exception) {
            is BadCredentialsException -> {
                response.status = HttpStatus.UNAUTHORIZED.value()
                data["message"] = "${exception.message}. Username or password is incorrect. Please try again."
            }
            is DisabledException -> {
                response.status = HttpStatus.PRECONDITION_FAILED.value()
                data["message"] = "${exception.message}. Please activate your account"
            }
            is CredentialsExpiredException -> response.status = HttpStatus.FORBIDDEN.value()
            is AccountExpiredException -> response.status = HttpStatus.PAYMENT_REQUIRED.value()
            else -> response.status = HttpStatus.UNAUTHORIZED.value()
        }


        response.outputStream
                .println(objectMapper.writeValueAsString(data))
    }
}