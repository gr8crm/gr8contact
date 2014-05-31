/*
 * Copyright (c) 2013. Avtala Sverige AB. All rights reserved.
 *
 *     http://www.avtala.se
 *
 * NOTICE: All information contained herein is, and remains the property of
 * Avtala Sverige AB. The intellectual and technical concepts contained herein are
 * proprietary to Avtala Sverige AB and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly
 * forbidden unless prior written permission is obtained from Avtala Sverige AB.
 */

import grails.plugins.crm.core.CrmTheme

import javax.servlet.http.Cookie

class CrmThemeFilters {

    def crmThemeService
    def grailsApplication

    def filters = {
        setTheme(controller: '*', action: '*') {
            before = {
                def cookieName = grailsApplication.config.grails.layout.cookie.name
                if (params.theme) {
                    def themeName = params.theme
                    def tenant = crmThemeService.getTenantForTheme(themeName) ?: 1L
                    def theme = new CrmTheme(themeName, tenant)
                    if (themeName == 'eu') {
                        request.removeAttribute('crmTheme')
                        request.removeAttribute(cookieName)
                    } else {
                        request.setAttribute('crmTheme', theme)
                        request.setAttribute(cookieName, themeName)
                    }
                    if (cookieName) {
                        def themeConfig = grailsApplication.config.crm.theme
                        def cookie = new Cookie(cookieName, themeName)
                        def domain = themeConfig."$themeName".cookie.domain
                        def path = themeConfig."$themeName".cookie.path
                        if (!domain) {
                            domain = themeConfig.cookie.domain ?: 'localhost'
                        }
                        if (!path) {
                            path = themeConfig.cookie.path ?: "/"
                        }
                        cookie.setDomain(domain)
                        cookie.setPath(path)
                        if (themeName != 'eu') {
                            cookie.setMaxAge(themeConfig.cookie.age ?: (60 * 60 * 24 * 365)) // Store cookie for 1 year
                        } else {
                            cookie.setMaxAge(0) // This removes the cookie.
                        }
                        response.addCookie(cookie)
                    }
                } else {
                    def host = request.getServerName()
                    def themeName = grailsApplication.config.grails.layout.domain[host]
                    if (!themeName && cookieName) {
                        def cookie = request.getCookies().find { it.name == cookieName }
                        if (cookie) {
                            themeName = cookie.getValue()
                        }
                    }
                    if(themeName) {
                        def tenant = crmThemeService.getTenantForTheme(themeName) ?: 1L
                        def theme = new CrmTheme(themeName, tenant)
                        request.setAttribute('crmTheme', theme)
                        request.setAttribute(cookieName, themeName)
                    }
                }
            }
        }
    }
}
