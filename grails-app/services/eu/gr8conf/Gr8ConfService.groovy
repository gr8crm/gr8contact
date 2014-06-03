package eu.gr8conf

import grails.events.Listener
import grails.plugins.crm.contact.CrmContact
import grails.plugins.crm.task.CrmTask
import groovy.json.JsonSlurper

import java.text.SimpleDateFormat

/**
 * Demo Service.
 */
class Gr8ConfService {

    def crmContactService
    def crmTaskService

    /**
     * Listen for alarms and send email reminders to subscribing users.
     *
     * @param data event payload
     */
    @Listener(namespace = "crmTask", topic = "alarm")
    void alarm(data) {
        def task = crmTaskService.getTask(data.id)

        println "Alarm [$task] was triggered! $data"

        // Send email to data.recipients
    }

    void importGr8ConfSchedule() {
        createSpeakers(1)
        createTalks(1)
        updateAgenda(1)
    }

    private void createSpeakers(int id) {
        def slurper = new JsonSlurper()
        def json = new URL("http://cfp.gr8conf.org/api2/speakers/$id").withReader { r -> slurper.parse(r) }
        json.each { speaker ->
            def company
            if (speaker.company) {
                company = crmContactService.findByName(speaker.company) ?: crmContactService.createCompany(name: speaker.company, username: 'admin', true)
            }
            def person = crmContactService.createPerson(firstName: speaker.name, parent: company, description: speaker.bio, username: 'admin', true)
        }
    }

    private void createTalks(int id) {
        def slurper = new JsonSlurper()
        def json = new URL("http://cfp.gr8conf.org/api2/talks/$id").withReader { r -> slurper.parse(r) }
        def timeFormat = new SimpleDateFormat("HH:mm:ss")
        def sessionType = crmTaskService.createTaskType(name: "Conference session", param: "session", true)
        json.each { talk ->
            def startTime = timeFormat.parse(talk.slot.start)
            def speaker = talk.speakers?.find { it }?.name
            if (speaker) {
                speaker = CrmContact.findByName(speaker)
            }
            def crmTask = crmTaskService.createTask(number: talk.id, startTime: startTime, duration: talk.duration,
                    name: talk.title, type: sessionType, description: talk.summary,
                    username: 'admin', reference: speaker, true)
            if (crmTask.hasErrors()) {
                throw new IllegalStateException(crmTask.errors.allErrors.toString())
            } else {
                for (tag in talk.tags) {
                    for (t in tag.split(/\s+/)) {
                        crmTask.setTagValue(t)
                    }
                }
            }
        }
    }


    private void updateAgenda(int id) {
        def slurper = new JsonSlurper()
        def days = new URL("http://cfp.gr8conf.org/api2/agenda/$id").withReader { r -> slurper.parse(r) }
        def timeFormat = new SimpleDateFormat("HH:mm:ss")
        days.each { agenda ->
            def cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Copenhagen"), new Locale("sv_SE"))
            cal.setTime(Date.parse("yyyy-MM-dd", agenda.day).clearTime())
            agenda.tracks.each { track ->
                track.slots.each { slot ->
                    def startTime = timeFormat.parse(slot.start)
                    cal.set(Calendar.HOUR_OF_DAY, startTime.hours)
                    cal.set(Calendar.MINUTE, startTime.minutes)
                    def talk = slot.talk
                    if (talk) {
                        def crmTask = CrmTask.findByNumber(talk.id) //crmTaskService.findByNumber(talk.id)
                        if (crmTask) {
                            crmTask.setStartTime(cal.getTime())
                            crmTask.setDuration(Integer.valueOf(slot.duration))
                        }
                    }
                }
            }
        }
    }
}
