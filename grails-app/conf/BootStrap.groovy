import grails.plugins.crm.task.CrmTaskAlarmJob

class BootStrap {

    def crmCoreService
    def crmAccountService
    def crmSecurityService
    def crmContactService
    def crmContentService
    def crmPluginService
    def navigationService
    def crmTaskService
    def gr8ConfService

    def init = { servletContext ->

        navigationService.registerItem('main', [controller: 'crmCalendar', action: 'index', title: 'crmCalendar.index.label', order: 81])
        navigationService.updated()

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

        crmPluginService.registerView('crmContact', 'show', 'tabs',
                [id: "tasks", index: 300, permission: "crmTask:show", label: "crmTask.index.label", template: '/crmTask/list', plugin: "crm-task-ui", model: {
                    def result = crmTaskService.list([reference: crmContact], [sort: 'startTime', order: 'asc'])
                    def rid = crmCoreService.getReferenceIdentifier(crmContact)
                    return [bean: crmContact, reference: rid, result: result, totalCount: result.totalCount]
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
                def gr8conf = crmContactService.createCompany(name: "GR8Conf", email: "info@gr8conf.org",
                        address: [address1: "IT-University", city: 'Copenhagen', country: "DK"], true)
                crmContactService.createPerson(firstName: "Søren", lastName: "Berg Glasius",
                        email: "sbglasius@gr8conf.org", parent: gr8conf, title: "Awesome Organizer", true)
                def technipelago = crmContactService.createCompany(name: "Technipelago AB", email: "info@technipelago.se",
                        website: 'www.technipelago.se', address: [city: 'Djurhamn', country: "SE"], true)
                crmContactService.createPerson(firstName: "Göran", lastName: "Ehrsson",
                        email: "goran@technipelago.se", parent: technipelago, title: "Developer", true)

                gr8ConfService.importGr8ConfSchedule()
            }
        }
    }

    def destroy = {
    }
}
