grails.servlet.version = "3.0"
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

// uncomment (and adjust settings) to fork the JVM to isolate classpaths
//grails.project.fork = [
//   run: [maxMemory:1024, minMemory:64, debug:false, maxPerm:256]
//]
// Increase heap and permgen in run-war mode
grails.tomcat.jvmArgs = ["-Djava.awt.headless=true", "-Xms512m", "-Xmx768m", "-XX:MaxPermSize=320m"]
grails.tomcat.nio = true

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {}
    log "error"
    checksums true
    legacyResolve false

    repositories {
        inherits true

        grailsCentral()
        mavenCentral()
    }

    dependencies {
    }

    plugins {
        build ":tomcat:$grailsVersion"

        compile ':cache:1.1.1'

        runtime ":hibernate:$grailsVersion"
        compile ":resources:1.2.7"
        compile ":jquery:1.10.2"
        compile ":twitter-bootstrap:2.3.2"
        compile ":less-resources:1.3.3.2"
        compile ":database-migration:1.3.6"

        compile ":crm-contact-ui:2.0.0"
        compile ":crm-content-ui:2.0.0"
        compile ":crm-task-ui:2.0.0"
        compile ":crm-security-shiro:2.0.0"
        compile ":crm-i18n:2.0.0"
        compile ":crm-ui-bootstrap:2.0.0"
        compile ":cookie-layout:0.6"
    }
}
