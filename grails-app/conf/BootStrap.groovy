class BootStrap {

    def crmCoreService
    def crmAccountService
    def crmSecurityService
    def crmContactService
    def crmContentService
    def crmPluginService

    def init = { servletContext ->

        // crmContact:show << documents
        crmPluginService.registerView('crmContact', 'show', 'tabs',
                [id: "documents",
                        index: 500,
                        permission: "crmContact:show",
                        label: "crmContact.tab.documents.label",
                        template: '/crmContent/embedded',
                        plugin: "crm-content-ui",
                        model: {
                            def result = crmContentService.findResourcesByReference(crmContact)
                            return [bean: crmContact, list: result, totalCount: result.size(),
                                    reference: crmCoreService.getReferenceIdentifier(crmContact), openAction: 'show']
                        }]
        )

        def admin = crmSecurityService.createUser([username: "admin", password: "admin",
                email: "firstname.lastname@email.com", name: "Admin", enabled: true])

        crmSecurityService.addPermissionAlias("permission.all", ["*:*"])

        crmSecurityService.runAs(admin.username) {
            def account = crmAccountService.createAccount([status: "active"])
            def tenant = crmSecurityService.createTenant(account, "GR8 Contacts", [locale: Locale.ENGLISH])
            crmSecurityService.runAs(admin.username, tenant.id) {
                crmSecurityService.addPermissionToUser("permission.all")

                // Create some demo data.
                def type1 = crmContactService.createRelationType(name: "Organizer", param: "organizer", true)
                def type2 = crmContactService.createRelationType(name: "Owner", param: "owner", true)

                def gr8conf = crmContactService.createCompany(name: "GR8Conf", email: "info@gr8conf.org",
                        address: [address1: "IT-University", city: 'Copenhagen', country: "DK"], true)
                def soren = crmContactService.createPerson(firstName: "Søren", lastName: "Berg Glasius",
                        email: "sbglasius@gr8conf.org", title: "Awesome Organizer", true)
                crmContactService.addRelation(soren, gr8conf, type1, true, 'Awesome organizer of GR8Conf Europe')
                def technipelago = crmContactService.createCompany(name: "Technipelago AB", email: "info@technipelago.se",
                        url: 'www.technipelago.se', address: [city: 'Djurhamn', country: "SE"], true)
                def goran = crmContactService.createPerson(firstName: "Göran", lastName: "Ehrsson",
                        email: "goran@technipelago.se", title: "Developer", true)
                crmContactService.addRelation(goran, technipelago, type2, true)
            }
        }
    }

    def destroy = {
    }
}
