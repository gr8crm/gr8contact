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

        grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenLocal()
        mavenCentral()

        mavenRepo "http://labs.technipelago.se/repo/crm-releases-local/"
        mavenRepo "http://labs.technipelago.se/repo/plugins-releases-local/"
    }

    dependencies {
    }

    plugins {
        build ":tomcat:$grailsVersion"

        compile ':cache:1.1.1'

        runtime ":hibernate:$grailsVersion"
        runtime ":resources:1.2.7"
        runtime ":jquery:1.10.2"
        runtime ":twitter-bootstrap:2.3.2"
        runtime ":less-resources:1.3.3.2"
        runtime ":database-migration:1.3.6"

        runtime "grails.crm:crm-security-shiro:1.2.6"
        runtime "grails.crm:crm-i18n:1.2.2"
        compile "grails.crm:crm-contact-lite:1.2.5"
        runtime "grails.crm:crm-ui-bootstrap:1.2.12"
    }
}
