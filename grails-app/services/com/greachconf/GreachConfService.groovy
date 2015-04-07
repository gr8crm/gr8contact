package com.greachconf

import org.apache.commons.lang.StringUtils

/**
 * Parse CSV file and add speakers and talks as contacts and tasks in GR8 CRM.
 */
class GreachConfService {

    def crmContactService
    def crmTaskService

    private List splitName(String name) {
        def tmp = name.split(' ')
        def firstname = (tmp.size() > 1 ? tmp[0..-2].join(' ') : tmp[0]).trim()
        def lastname = tmp.size() > 1 ? tmp[-1].trim() : null
        [firstname, lastname]
    }

    def importTalks(File file) {
        def cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, 2015)
        cal.set(Calendar.MONTH, Calendar.APRIL)
        cal.set(Calendar.DAY_OF_MONTH, 10)
        cal.set(Calendar.HOUR_OF_DAY, 8)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        def type = crmTaskService.createTaskType(name: "Talk", true)
        def talks = parseFile(file)
        for (info in talks) {
            def speaker = crmContactService.findByName(info.name)
            if (!speaker) {
                def (firstname, lastname) = splitName(info.name)
                speaker = crmContactService.createPerson(firstName: firstname, lastName: lastname, title: info.position,
                        number2: info.twitter, description: StringUtils.abbreviate(info.bio, 2000), true)
            }
            if (!speaker.hasErrors()) {
                def number = Math.abs(info.title.hashCode()).toString()
                def talk = crmTaskService.findByNumber(number)
                if (!talk) {
                    talk = crmTaskService.createTask(type: type, number: number, name: StringUtils.abbreviate(info.title, 20), startTime: cal.getTime(), duration: 30, reference: speaker, true)
                }
            }
            cal.add(Calendar.MINUTE, 30)
            if (cal.get(Calendar.HOUR_OF_DAY) > 18) {
                // Advance to next day
                cal.add(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 8)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
            }
        }
    }

    List<Map<String, String>> parseFile(File file) {
        def quote = false
        def lines = []
        def cols = [:]
        def s = new StringBuilder()
        def i = 0
        def header = []
        def line = 0

        file.text.each { character ->
            if (character == '\n') {
                if (quote) {
                    s << character
                } else {
                    def text = s.toString()
                    if (line) {
                        def key = header[i]
                        cols[key] = text

                        lines << cols
                        cols = [:]
                    } else {
                        header << text
                    }
                    line++
                    s = new StringBuilder()
                    i = 0
                }
            } else if (character == '"') {
                if (quote) {
                    quote = false
                } else {
                    quote = true
                }
            } else if (character == ',') {
                if (quote) {
                    s << character
                } else {
                    def text = s.toString()
                    if (line) {
                        def key = header[i]
                        cols[key] = text
                    } else {
                        header << text
                    }
                    i++
                    s = new StringBuilder()
                }
            } else {
                s << character
            }
        }
        if (s.length()) {
            def key = header[i]
            cols[key] = s.toString()
            lines << cols
        }
        lines
    }
}
