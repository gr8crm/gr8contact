class BootStrap {

    def crmAccountService
    def crmSecurityService
    def crmContactService

    def init = { servletContext ->

        def admin = crmSecurityService.createUser([username: "admin", password: "admin",
                email: "firstname.lastname@email.com", name: "Admin", enabled: true])

        crmSecurityService.addPermissionAlias("permission.all", ["*:*"])

        crmSecurityService.runAs(admin.username) {
            def account = crmAccountService.createAccount([status: "active"])
            def tenant = crmSecurityService.createTenant(account, "GR8 Contacts", [locale: Locale.ENGLISH])
            crmSecurityService.runAs(admin.username, tenant.id) {
                crmSecurityService.addPermissionToUser("permission.all")

                // Create some demo data.
                def gr8conf = crmContactService.createCompany(name: "GR8Conf", email: "info@gr8conf.org",
                        address: [address1: "IT-University", city: 'Copenhagen', country: "DK"], true)
                crmContactService.createPerson(firstName: "Søren", lastName: "Berg Glasius",
                        email: "sbglasius@gr8conf.org", parent: gr8conf, title: "Awesome Organizer", true)
                def technipelago = crmContactService.createCompany(name: "Technipelago AB", email: "info@technipelago.se",
                        website: 'www.technipelago.se', address: [city: 'Djurhamn', country: "SE"], true)
                crmContactService.createPerson(firstName: "Göran", lastName: "Ehrsson",
                        email: "goran@technipelago.se", parent: technipelago, title: "Developer", true)
            }
        }
    }

    def destroy = {
    }
}
