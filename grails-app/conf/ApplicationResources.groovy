modules = {
    'bootstrap-responsive-less' {
        dependsOn 'bootstrap'
        resource id:'bootstrap-responsive-less', url:[plugin: 'twitter-bootstrap', dir: 'less', file: 'responsive.less'], attrs:[rel: "stylesheet/less", type:'css', order:150], disposition: 'head'
    }
    application {
        dependsOn 'bootstrap-responsive-less'
        resource url:[dir: 'less', file: 'application.less'], attrs:[rel: "stylesheet/less", type:'css', order:10], disposition: 'head'
        resource url:'js/application.js'
    }
    'gr8conf-us-crm' {
        dependsOn 'jquery, modernizr, bootstrap-js'
        resource url: [id: 'crm-ui-bootstrap-less', dir: 'less-us', file: 'crm-ui-bootstrap.less'], attrs: [rel: "stylesheet/less", type: 'css', order: 110], disposition: 'head'
        resource url: [plugin: 'crm-ui-bootstrap', dir: 'js', file: 'crm-ui-bootstrap.js']
        resource url: [plugin: 'crm-ui-bootstrap', dir: 'js', file: 'jquery.dropdownPlain.js']
        resource url: [plugin: 'crm-ui-bootstrap', dir: 'js', file: 'jquery.hoverIntent-min.js']
        resource url: [plugin: 'crm-ui-bootstrap', dir: 'js', file: 'jquery.notifier.js']
    }
    'gr8conf-us' {
        dependsOn 'gr8conf-us-crm'
        resource id:'bootstrap-less', url:[dir: 'less-us', file: 'bootstrap.less'], attrs:[rel: "stylesheet/less", type:'css', order:100], disposition: 'head'
        resource id:'bootstrap-responsive-less', url:[dir: 'less-us', file: 'responsive.less'], attrs:[rel: "stylesheet/less", type:'css', order:150], disposition: 'head'
        resource url:[dir: 'less-us', file: 'gr8conf-us.less'], attrs:[rel: "stylesheet/less", type:'css', order:10], disposition: 'head'
        resource url:'js/application.js'
    }
}