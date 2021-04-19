package com.thoughtworks.sea.ssointegrationdemo.controller

import com.thoughtworks.sea.ssointegrationdemo.service.URLService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView

@RestController
@RequestMapping("/login")
class LoginController(
    private val urlService: URLService
) {

    // OIDC: Step 1: Provide an interface to do authentication checks
    @GetMapping("/oidc")
    fun redirectToOIDCLoginURL() = RedirectView(urlService.generateOIDCLoginURL())
}
