<%@ page import="org.apache.commons.lang.StringUtils; grails.plugins.crm.core.TenantUtils; grails.util.GrailsNameUtils;" %><!DOCTYPE html>
<html lang="en" class="no-js">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title><g:layoutTitle default="${meta(name: 'app.name')}"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <r:require module="gr8conf-us"/>

    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">

    <g:each in="${grailsApplication.config.crm.ui.apple.icons ?: ['': 'apple-touch-icon.png']}" var="icon">
        <link rel="apple-touch-icon" sizes="${icon.key}" href="${resource(dir: 'images', file: icon.value)}">
    </g:each>

    <r:script>

        $(document).ajaxError(function(e, xhr, settings, exception) {
            if (xhr.status == 403) {
                window.location.href = "${createLink(mapping: 'start', absolute: true)}";
            } else if (xhr.status == 404) {
                alert('Requested URL not found.');
            } else if (xhr.status == 500) {
                alert('Error.\nInternal server error.');
            } else if (errStatus == 'parsererror') {
                alert('Error.\nParsing JSON Request failed.');
            } else if (errStatus == 'timeout') {
                alert('Request timed out.\nPlease try later');
            } else {
                alert('Unknown Error.');
            }
        });

        $(document).ready(function() {
            $("#navigation_notifications .notification-delete a").click(function(ev) {
                var item = $(this).closest(".notification-item");
                $.post("${createLink(controller:'crmNotification', action:'delete')}", {id:item.data('crm-id')}, function(data) {
                    item.remove();
                    var count = data.count;
                    if(count > 0) {
                        $("#notifictions-unread-count").text(count);
                    } else {
                        $("#navigation_notifications").remove();
                    }
                });
            });

            $(".recent-clear").click(function(ev) {
                ev.stopImmediatePropagation();
                $.post($(this).attr('href'), function(data) {
                    location.reload();
                });
                return false;
            });

<% if(flash.alert) { %>
            $('#alertModal').modal({show:true});
<% } %>
        });
    </r:script>
    <r:layoutResources/>
    <g:layoutHead/>
    <!-- Le HTML5 shim, for IE6-8 support of HTML elements -->
    <!--[if lt IE 9]>
          <script src="${resource(dir:'js', file: 'html5.js')}"></script>
    <![endif]-->
</head>

<body class="${controllerName ?: 'home'}-body">

    <div id="head-wrapper" class="clearfix">
        <div class="row-fluid">
            <div class="span4">
                <div id="brand" class="visible-desktop">
                    <g:link uri="/" class="brand"><g:img dir="images" file="gr8conf-us-logo.png"/></g:link>
                </div>
            </div>
            <div class="span8">
                <recent:hasHistory>
                    <div class="recent-list pull-right clearfix">
                        <recent:each var="m" max="5" reverse="true">
                            <span class="${m.tags ? 'label' : ''}">
                                <g:link class="${m.controller}"
                                        controller="${m.controller}" action="${m.action}" id="${m.id}"
                                        title="${message(code:m.controller + '.click.to.show.label', default:'Click to show {0}', args:[m])}">
                                        <i class="${m.icon ?: 'icon-chevron-right'}"></i><g:decorate include="abbreviate" max="20">${m}</g:decorate>
                                </g:link>
                                <g:if test="${m.tags}">
                                    <g:link controller="recentDomain" action="clear" params="${[type: m.type, id: m.id]}" class="recent-clear">&times;</g:link>
                                </g:if>
                            </span>
                        </recent:each>
                    </div>
                </recent:hasHistory>

                <g:pageProperty name="page.top"/>

                <div id="global-message" class="hide">
                    <g:if test="${flash.info || flash.message}">
                        <div class="alert-info">
                            ${raw(flash.info ?: flash.message)}
                        </div>
                    </g:if>
                    <g:if test="${flash.success}">
                        <div class="alert-success">
                            ${raw(flash.success)}
                        </div>
                    </g:if>
                    <g:if test="${flash.warning}">
                        <div class="alert-warning">
                            ${raw(flash.warning)}
                        </div>
                    </g:if>
                    <g:if test="${flash.error}">
                        <div class="alert-error">
                            ${raw(flash.error)}
                        </div>
                    </g:if>
                </div>
            </div>
        </div>
    </div>

    <div class="navbar" id="navigation-wrapper">
        <div class="navbar-inner">
            <div class="container">

                <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>

                <g:link mapping="home" class="brand hidden-desktop"><g:message code="app.name" default="GR8 CRM"/></g:link>

                <div class="nav-collapse">

                    <nav:menu scope="main" custom="true" class="nav" id="navigation_main">
                        <g:set var="navController" value="${item.linkArgs.controller}"/>
                        <g:set var="navAction" value="${item.linkArgs.action}"/>
                        <g:set var="navData" value="${item.data}"/>
                        <crm:hasPermission permission="${navController + ':' + navAction + (navData.id ? ':' + navData.id : '')}">
                            <li class="${active || (navController == controllerName && navAction == actionName) ? 'active' : ''}">
                                <g:link controller="${navController ?: controllerName}" action="${navAction}" id="${navData.id}"
                                        title="${message(code:navController + '.' + navAction + '.help')}">
                                    ${message(code: item.titleMessageCode ?: (navController + '.' + navAction), default: message(code: navController, default: item.titleMessageCode ?: (navController + '.' + navAction)), args: [entityName])}
                                </g:link>
                            </li>
                        </crm:hasPermission>
                    </nav:menu>

                    <crm:tenant><g:set var="tenantName" value="${name}"/></crm:tenant>

                    <crm:user>
    <%--
                        <form class="navbar-search pull-right" action="${createLink(mapping:'logout')}">
                            <button id="logout-button" class="btn btn-small"><i class="icon-off"></i></button>
                        </form>
    --%>
                        <ul class="nav pull-right">
                            <li class="dropdown">
                                <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                    ${(tenantName ?: name).encodeAsHTML()}<b class="caret"></b>
                                </a>
                                <ul class="dropdown-menu">

                                    <li><g:link mapping="logout" title="${message(code: 'auth.logout.title', default: 'Logout {0}', args:[name])}">
                                        <g:message code="auth.logout.label" default="Logout" args="${[name]}"/>
                                    </g:link>
                                    </li>

                                    <crm:eachNavigationItem scope="settings">
                                        <g:set var="navController" value="${item.linkArgs.controller}"/>
                                        <g:set var="navAction" value="${item.linkArgs.action}"/>
                                        <g:set var="navData" value="${item.data}"/>
                                        <crm:hasPermission permission="${navController + ':' + navAction + (navData.id ? ':' + navData.id : '')}">
                                            <li class="${active || (navController == controllerName && navAction == actionName) ? 'active' : ''}">
                                                <g:link controller="${navController ?: controllerName}" action="${navAction}" id="${navData.id}"
                                                        title="${message(code:navController + '.' + navAction + '.help')}">
                                                    ${message(code: item.titleMessageCode ?: (navController + '.' + navAction), default: message(code: navController, default: item.titleMessageCode ?: (navController + '.' + navAction)), args: [entityName])}
                                                </g:link>
                                            </li>
                                        </crm:hasPermission>
                                    </crm:eachNavigationItem>

                                    <li class="divider"></li>

                                    <crm:eachTenant var="a">
                                        <li class="${a.current ? 'current' : ''}">
                                            <g:link mapping="crm-tenant-activate" id="${a.id}">
                                                ${a.name.encodeAsHTML()}
                                            </g:link>
                                        </li>
                                    </crm:eachTenant>
                                </ul>
                            </li>
                        </ul>

                        <ul class="nav pull-right" id="navigation_favorites">
                            <li class="dropdown">
                                <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                    <g:message code="default.favorites.menu.label"/><b class="caret"></b>
                                </a>
                                <ul class="dropdown-menu">
                                    <g:set var="prevFav" value=""/>
                                    <usertag:eachTagged tag="favorite" tenant="${TenantUtils.tenant}" username="${username}"
                                                        var="fav">
                                        <g:set var="thisFav"
                                               value="${GrailsNameUtils.getPropertyName(fav.class)}"/>
                                        <g:if test="${thisFav != prevFav}">
                                            <g:set var="prevFav" value="${thisFav}"/>
                                            <li style="color:#999;padding-left:16px;font-size:10px;text-transform:uppercase;">${message(code: thisFav + '.label', default: GrailsNameUtils.getNaturalName(thisFav))}</li>
                                        </g:if>
                                        <li>
                                            <g:link controller="${grails.util.GrailsNameUtils.getPropertyName(fav.class)}"
                                                    action="show" id="${fav.id}">${fav.encodeAsHTML()}</g:link>
                                        </li>
                                    </usertag:eachTagged>

                                    <crm:eachNavigationItem scope="public">
                                        <g:set var="navController" value="${item.linkArgs.controller}"/>
                                        <g:set var="navAction" value="${item.linkArgs.action}"/>
                                        <g:set var="navData" value="${item.data}"/>
                                        <crm:hasPermission permission="${navController + ':' + navAction + (navData.id ? ':' + navData.id : '')}">
                                            <li class="${active || (navController == controllerName && navAction == actionName) ? 'active' : ''}">
                                                <g:link controller="${navController ?: controllerName}" action="${navAction}" id="${navData.id}"
                                                        title="${message(code:navController + '.' + navAction + '.help')}">
                                                    ${message(code: item.titleMessageCode ?: (navController + '.' + navAction), default: message(code: navController, default: item.titleMessageCode ?: (navController + '.' + navAction)), args: [entityName])}
                                                </g:link>
                                            </li>
                                        </crm:hasPermission>
                                    </crm:eachNavigationItem>
                                </ul>
                            </li>
                        </ul>

                        <crm:tenant>
                            <ul class="nav pull-right" id="navigation_admin">
                                <li class="dropdown">
                                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                        <g:message code="default.admin.menu.label"/><b class="caret"></b>
                                    </a>
                                    <nav:menu scope="admin" custom="true" class="dropdown-menu">
                                        <g:set var="navController" value="${item.linkArgs.controller}"/>
                                        <g:set var="navAction" value="${item.linkArgs.action}"/>
                                        <g:set var="navData" value="${item.data}"/>
                                        <crm:hasPermission permission="${navController + ':' + navAction + (navData.id ? ':' + navData.id : '')}">
                                            <li class="${active || (navController == controllerName && navAction == actionName) ? 'active' : ''}">
                                                <g:link controller="${navController ?: controllerName}" action="${navAction}" id="${navData.id}"
                                                        title="${message(code:navController + '.' + navAction + '.help')}">
                                                    ${message(code: item.titleMessageCode ?: (navController + '.' + navAction), default: message(code: navController, default: item.titleMessageCode ?: (navController + '.' + navAction)), args: [entityName])}
                                                </g:link>
                                            </li>
                                        </crm:hasPermission>
                                    </nav:menu>
                                </li>
                            </ul>
                        </crm:tenant>

                        <plugin:isAvailable name="crm-notification">
                            <crm:hasUnreadNotifications username="${username}" tenant="${TenantUtils.tenant}">
                                <ul class="nav pull-right" id="navigation_notifications" role="menu">
                                    <li class="dropdown">
                                        <a href="javascript:void(0);" class="dropdown-toggle" data-toggle="dropdown">
                                            <span class="badge badge-important" id="notifictions-unread-count">${count}</span>
                                        </a>
                                        <ul class="dropdown-menu" role="menu">
                                            <crm:eachNotification username="${username}" tenant="${TenantUtils.tenant}" var="n">
                                                <li class="dropdown-submenu notification-item" data-crm-id="${n.id}">
                                                    <a href="javascript:void(0);" tabindex="-1">${n.dateCreated.format('d MMM HH:mm')} - ${StringUtils.abbreviate(n.subject, 30)}</a>
                                                    <ul class="dropdown-menu">
                                                        <g:each in="${n.payload?.links}" var="l">
                                                            <li>
                                                                <g:link controller="${l.controller ?: controllerName}" action="${l.action ?: ''}" title="${l.title ?: ''}">
                                                                    <i class="${l.icon ?: 'icon-chevron-right'}"></i>
                                                                    ${l.label}
                                                                </g:link>
                                                            </li>
                                                        </g:each>
                                                        <li class="notification-delete">
                                                            <a href="javascript:void(0);">
                                                                <i class="icon-trash"></i>
                                                                <g:message code="crmNotification.button.delete.label" default="Delete"/>
                                                            </a>
                                                        </li>
                                                    </ul>
                                                </li>
                                            </crm:eachNotification>
                                        </ul>
                                    </li>
                                </ul>
                            </crm:hasUnreadNotifications>
                        </plugin:isAvailable>
                    </crm:user>


                    <crm:noUser>

                        <g:form controller="auth" action="signIn" name="loginBar" class="navbar-search pull-right">
                            <input type="hidden" name="targetUri" value="${targetUri}"/>
                            <!--[if lt IE 10]>
                            <span><g:message code="auth.login.username"/></span>
                            <![endif]-->
                            <g:textField id="login-username" name="username" value="${username}" autocapitalize="off"
                                         placeholder="${message(code:'auth.login.username', default:'Username...')}"
                                         class="search-query input-small"/>
                            <!--[if lt IE 10]>
                            <span><g:message code="auth.login.password"/></span>
                            <![endif]-->
                            <g:passwordField id="login-password" name="password" value=""
                                             placeholder="${message(code:'auth.login.password', default:'Password...')}"
                                             class="search-query input-small"/>
                            <button id="login-button" type="submit" class="btn btn-small" style="margin-top:0px;"><i class="icon-play"></i></button>
                        </g:form>

                        <nav:menu scope="public" custom="true" class="nav" id="navigation_settings">
                            <g:set var="navController" value="${item.linkArgs.controller}"/>
                            <g:set var="navAction" value="${item.linkArgs.action}"/>
                            <g:set var="navData" value="${item.data}"/>
                            <crm:hasPermission permission="${navController + ':' + navAction + (navData.id ? ':' + navData.id : '')}">
                                <li class="${active || (navController == controllerName && navAction == actionName) ? 'active' : ''}">
                                    <g:link controller="${navController ?: controllerName}" action="${navAction}" id="${navData.id}"
                                            title="${message(code:navController + '.' + navAction + '.help')}">
                                        ${message(code: item.titleMessageCode ?: (navController + '.' + navAction), default: message(code: navController, default: item.titleMessageCode ?: (navController + '.' + navAction)), args: [entityName])}
                                    </g:link>
                                </li>
                            </crm:hasPermission>
                        </nav:menu>

                    </crm:noUser>

                    <g:pageProperty name="page.navbar"/>

                </div>
            </div>
        </div>
    </div>

    <div class="${grailsApplication.config.crm.ui.bootstrap.fluid ? 'container-fluid' : 'container'}">

        <g:pageProperty name="page.hero"/>

        <div class="controller-${controllerName ?: 'home'} action-${actionName ?: 'index'}" id="content-wrapper"
             role="main">
            <g:layoutBody/>
        </div>

        <div id="footer-wrapper">
            <g:render template="/footer"/>
        </div>
    </div>

<g:if test="${flash.alert}">
    <div class="modal hide fade" id="alertModal">

        <div class="modal-header">
            <a class="close" data-dismiss="modal">×</a>

            <h3><g:message code="alert.title" default="Message"/></h3>
        </div>

        <div class="modal-body">
            <p>${flash.alert.encodeAsHTML()}</p>
        </div>

        <div class="modal-footer">
            <a href="#" class="btn btn-primary" data-dismiss="modal"><g:message
                    code="default.button.ok.label" default="Ok"/></a>
        </div>

    </div>
</g:if>

<div id="spinner" class="spinner" style="display:none;"><g:img dir="images" file="spinner.gif" alt="${message(code: 'spinner.alt', default: 'Loading&hellip;')}"/></div>

<r:layoutResources/>

</body>
</html>