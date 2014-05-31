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

class CrmThemeFilters {

    def crmThemeService
    def grailsApplication

    def filters = {
        setTheme(controller: '*', action: '*') {
            before = {
                def themeName = grailsApplication.config.grails.layout.domain[request.getServerName()]
                if (themeName) {
                    def tenant = crmThemeService.getTenantForTheme(themeName) ?: 1L
                    def theme = new CrmTheme(themeName, tenant)
                    def cookieName = grailsApplication.config.grails.layout.cookie.name
                    request.setAttribute('crmTheme', theme)
                    request.setAttribute(cookieName, themeName)
                }
            }
        }
    }
}
