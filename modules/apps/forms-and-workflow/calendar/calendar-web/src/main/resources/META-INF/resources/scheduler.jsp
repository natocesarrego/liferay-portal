<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/asset" prefix="liferay-asset" %><%@
taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/comment" prefix="liferay-comment" %><%@
taglib uri="http://liferay.com/tld/expando" prefix="liferay-expando" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/security" prefix="liferay-security" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.asset.kernel.model.AssetEntry" %><%@
page import="com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil" %><%@
page import="com.liferay.calendar.constants.CalendarActionKeys" %><%@
page import="com.liferay.calendar.exception.CalendarBookingDurationException" %><%@
page import="com.liferay.calendar.exception.CalendarBookingRecurrenceException" %><%@
page import="com.liferay.calendar.exception.CalendarNameException" %><%@
page import="com.liferay.calendar.exception.CalendarResourceCodeException" %><%@
page import="com.liferay.calendar.exception.CalendarResourceNameException" %><%@
page import="com.liferay.calendar.exception.DuplicateCalendarResourceException" %><%@
page import="com.liferay.calendar.exception.NoSuchResourceException" %><%@
page import="com.liferay.calendar.model.Calendar" %><%@
page import="com.liferay.calendar.model.CalendarBooking" %><%@
page import="com.liferay.calendar.model.CalendarNotificationTemplate" %><%@
page import="com.liferay.calendar.model.CalendarNotificationTemplateConstants" %><%@
page import="com.liferay.calendar.model.CalendarResource" %><%@
page import="com.liferay.calendar.notification.NotificationField" %><%@
page import="com.liferay.calendar.notification.NotificationTemplateType" %><%@
page import="com.liferay.calendar.notification.NotificationType" %><%@
page import="com.liferay.calendar.notification.impl.NotificationUtil" %><%@
page import="com.liferay.calendar.recurrence.Frequency" %><%@
page import="com.liferay.calendar.recurrence.PositionalWeekday" %><%@
page import="com.liferay.calendar.recurrence.Recurrence" %><%@
page import="com.liferay.calendar.recurrence.Weekday" %><%@
page import="com.liferay.calendar.service.CalendarBookingLocalServiceUtil" %><%@
page import="com.liferay.calendar.service.CalendarBookingServiceUtil" %><%@
page import="com.liferay.calendar.service.CalendarNotificationTemplateLocalServiceUtil" %><%@
page import="com.liferay.calendar.service.CalendarResourceServiceUtil" %><%@
page import="com.liferay.calendar.service.CalendarServiceUtil" %><%@
page import="com.liferay.calendar.service.configuration.CalendarServiceConfigurationValues" %><%@
page import="com.liferay.calendar.service.permission.CalendarPermission" %><%@
page import="com.liferay.calendar.service.permission.CalendarPortletPermission" %><%@
page import="com.liferay.calendar.service.permission.CalendarResourcePermission" %><%@
page import="com.liferay.calendar.util.CalendarResourceUtil" %><%@
page import="com.liferay.calendar.util.CalendarUtil" %><%@
page import="com.liferay.calendar.util.ColorUtil" %><%@
page import="com.liferay.calendar.util.JCalendarUtil" %><%@
page import="com.liferay.calendar.util.RecurrenceUtil" %><%@
page import="com.liferay.calendar.util.comparator.CalendarNameComparator" %><%@
page import="com.liferay.calendar.web.internal.constants.CalendarWebKeys" %><%@
page import="com.liferay.calendar.web.internal.display.context.CalendarDisplayContext" %><%@
page import="com.liferay.calendar.web.internal.search.CalendarResourceDisplayTerms" %><%@
page import="com.liferay.calendar.web.internal.search.CalendarResourceSearch" %><%@
page import="com.liferay.calendar.workflow.CalendarBookingWorkflowConstants" %><%@
page import="com.liferay.portal.kernel.bean.BeanParamUtil" %><%@
page import="com.liferay.portal.kernel.bean.BeanPropertiesUtil" %><%@
page import="com.liferay.portal.kernel.dao.orm.QueryUtil" %><%@
page import="com.liferay.portal.kernel.dao.search.SearchContainer" %><%@
page import="com.liferay.portal.kernel.json.JSONArray" %><%@
page import="com.liferay.portal.kernel.json.JSONFactoryUtil" %><%@
page import="com.liferay.portal.kernel.json.JSONObject" %><%@
page import="com.liferay.portal.kernel.json.JSONSerializer" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.model.Group" %><%@
page import="com.liferay.portal.kernel.model.User" %><%@
page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %><%@
page import="com.liferay.portal.kernel.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.kernel.security.permission.ResourceActionsUtil" %><%@
page import="com.liferay.portal.kernel.service.GroupServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.UserLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalServiceUtil" %><%@
page import="com.liferay.portal.kernel.servlet.BrowserSnifferUtil" %><%@
page import="com.liferay.portal.kernel.util.ArrayUtil" %><%@
page import="com.liferay.portal.kernel.util.CalendarFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.DateUtil" %><%@
page import="com.liferay.portal.kernel.util.FastDateFormatConstants" %><%@
page import="com.liferay.portal.kernel.util.FastDateFormatFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ListUtil" %><%@
page import="com.liferay.portal.kernel.util.OrderByComparator" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.PrefsPropsUtil" %><%@
page import="com.liferay.portal.kernel.util.PropsKeys" %><%@
page import="com.liferay.portal.kernel.util.SessionClicks" %><%@
page import="com.liferay.portal.kernel.util.StringBundler" %><%@
page import="com.liferay.portal.kernel.util.StringPool" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.Time" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.kernel.util.comparator.UserScreenNameComparator" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %><%@
page import="com.liferay.rss.util.RSSUtil" %><%@
page import="com.liferay.taglib.search.ResultRow" %>

<%@ page import="java.text.Format" %>

<%@ page import="java.util.ArrayList" %><%@
page import="java.util.Arrays" %><%@
page import="java.util.Collections" %><%@
page import="java.util.Date" %><%@
page import="java.util.Iterator" %><%@
page import="java.util.List" %><%@
page import="java.util.Objects" %><%@
page import="java.util.TimeZone" %>

<%@ page import="javax.portlet.PortletURL" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
int defaultDuration = GetterUtil.getInteger(portletPreferences.getValue("defaultDuration", null), 60);
String defaultView = portletPreferences.getValue("defaultView", "week");
String timeFormat = GetterUtil.getString(portletPreferences.getValue("timeFormat", "locale"));
String timeZoneId = portletPreferences.getValue("timeZoneId", user.getTimeZoneId());
boolean usePortalTimeZone = GetterUtil.getBoolean(portletPreferences.getValue("usePortalTimeZone", Boolean.TRUE.toString()));
int weekStartsOn = GetterUtil.getInteger(portletPreferences.getValue("weekStartsOn", null));

String sessionClicksDefaultView = SessionClicks.get(request, "com.liferay.calendar.web_defaultView", defaultView);

if (usePortalTimeZone) {
	timeZoneId = user.getTimeZoneId();
}

boolean displaySchedulerHeader = GetterUtil.getBoolean(portletPreferences.getValue("displaySchedulerHeader", null), true);
boolean displaySchedulerOnly = GetterUtil.getBoolean(portletPreferences.getValue("displaySchedulerOnly", null));
boolean showUserEvents = GetterUtil.getBoolean(portletPreferences.getValue("showUserEvents", null), true);

boolean showAgendaView = GetterUtil.getBoolean(portletPreferences.getValue("showAgendaView", null), true);
boolean showDayView = GetterUtil.getBoolean(portletPreferences.getValue("showDayView", null), true);
boolean showMonthView = GetterUtil.getBoolean(portletPreferences.getValue("showMonthView", null), true);
boolean showWeekView = GetterUtil.getBoolean(portletPreferences.getValue("showWeekView", null), true);

int eventsPerPage = GetterUtil.getInteger(portletPreferences.getValue("eventsPerPage", null), 10);
int maxDaysDisplayed = GetterUtil.getInteger(portletPreferences.getValue("maxDaysDisplayed", null), 1);

boolean enableRSS = !PortalUtil.isRSSFeedsEnabled() ? false : GetterUtil.getBoolean(portletPreferences.getValue("enableRss", null), true);
int rssDelta = GetterUtil.getInteger(portletPreferences.getValue("rssDelta", StringPool.BLANK), SearchContainer.DEFAULT_DELTA);
String rssDisplayStyle = portletPreferences.getValue("rssDisplayStyle", RSSUtil.DISPLAY_STYLE_DEFAULT);
String rssFeedType = portletPreferences.getValue("rssFeedType", RSSUtil.FEED_TYPE_DEFAULT);
long rssTimeInterval = GetterUtil.getLong(portletPreferences.getValue("rssTimeInterval", StringPool.BLANK), Time.WEEK);

CalendarBooking calendarBooking = (CalendarBooking)request.getAttribute(CalendarWebKeys.CALENDAR_BOOKING);

CalendarResource groupCalendarResource = CalendarResourceUtil.getScopeGroupCalendarResource(liferayPortletRequest, scopeGroupId);
CalendarResource userCalendarResource = null;

if (showUserEvents || !themeDisplay.isSignedIn()) {
	userCalendarResource = CalendarResourceUtil.getUserCalendarResource(liferayPortletRequest, themeDisplay.getUserId());
}

Calendar userDefaultCalendar = null;

if (userCalendarResource != null) {
	long defaultCalendarId = userCalendarResource.getDefaultCalendarId();

	if (defaultCalendarId > 0) {
		userDefaultCalendar = CalendarServiceUtil.getCalendar(defaultCalendarId);
	}
}

List<Calendar> groupCalendars = Collections.emptyList();

boolean showSiteCalendars = false;

if (groupCalendarResource != null) {
	showSiteCalendars = (userCalendarResource == null) || (groupCalendarResource.getCalendarResourceId() != userCalendarResource.getCalendarResourceId());
}

if (showSiteCalendars) {
	groupCalendars = CalendarServiceUtil.search(themeDisplay.getCompanyId(), null, new long[] {groupCalendarResource.getCalendarResourceId()}, null, true, QueryUtil.ALL_POS, QueryUtil.ALL_POS, (OrderByComparator)null);
}

List<Calendar> userCalendars = Collections.emptyList();

if (userCalendarResource != null) {
	userCalendars = CalendarServiceUtil.search(themeDisplay.getCompanyId(), null, new long[] {userCalendarResource.getCalendarResourceId()}, null, true, QueryUtil.ALL_POS, QueryUtil.ALL_POS, (OrderByComparator)null);
}

List<Calendar> otherCalendars = new ArrayList<Calendar>();

long[] calendarIds = StringUtil.split(SessionClicks.get(request, "com.liferay.calendar.web_otherCalendars", StringPool.BLANK), 0L);

Calendar defaultCalendar = null;

CalendarDisplayContext calendarDisplayContext = (CalendarDisplayContext)renderRequest.getAttribute(CalendarWebKeys.CALENDAR_DISPLAY_CONTEXT);

if (calendarDisplayContext != null) {
	otherCalendars = calendarDisplayContext.getOtherCalendars(calendarIds);
}

for (Calendar groupCalendar : groupCalendars) {
	if (groupCalendar.isDefaultCalendar() && CalendarPermission.contains(themeDisplay.getPermissionChecker(), groupCalendar, CalendarActionKeys.MANAGE_BOOKINGS)) {
		defaultCalendar = groupCalendar;
	}
}

if (defaultCalendar == null) {
	for (Calendar userCalendar : userCalendars) {
		if (userCalendar.isDefaultCalendar()) {
			defaultCalendar = userCalendar;
		}
	}
}

TimeZone userTimeZone = CalendarUtil.getCalendarBookingDisplayTimeZone(calendarBooking, TimeZone.getTimeZone(timeZoneId));
TimeZone utcTimeZone = TimeZone.getTimeZone(StringPool.UTC);

Format dateFormatLongDate = FastDateFormatFactoryUtil.getDate(FastDateFormatConstants.LONG, locale, userTimeZone);

Format dateFormatTime = null;

boolean useIsoTimeFormat = timeFormat.equals("24-hour") || (timeFormat.equals("locale") && !DateUtil.isFormatAmPm(locale));

if (useIsoTimeFormat) {
	dateFormatTime = FastDateFormatFactoryUtil.getSimpleDateFormat("HH:mm", locale, userTimeZone);
}
else {
	dateFormatTime = FastDateFormatFactoryUtil.getSimpleDateFormat("hh:mm a", locale, userTimeZone);
}

String activeView = ParamUtil.getString(request, "activeView", sessionClicksDefaultView);
long date = ParamUtil.getLong(request, "date", System.currentTimeMillis());
String editCalendarBookingURL = ParamUtil.getString(request, "editCalendarBookingURL");
String filterCalendarBookings = ParamUtil.getString(request, "filterCalendarBookings", null);
boolean hideAgendaView = ParamUtil.getBoolean(request, "hideAgendaView");
boolean hideDayView = ParamUtil.getBoolean(request, "hideDayView");
boolean hideMonthView = ParamUtil.getBoolean(request, "hideMonthView");
boolean hideWeekView = ParamUtil.getBoolean(request, "hideWeekView");
String permissionsCalendarBookingURL = ParamUtil.getString(request, "permissionsCalendarBookingURL");
boolean preventPersistence = ParamUtil.getBoolean(request, "preventPersistence");
boolean readOnly = ParamUtil.getBoolean(request, "readOnly");
boolean showAddEventBtn = ParamUtil.getBoolean(request, "showAddEventBtn");
boolean showSchedulerHeader = ParamUtil.getBoolean(request, "showSchedulerHeader", true);
String viewCalendarBookingURL = ParamUtil.getString(request, "viewCalendarBookingURL");
%>

<div class="calendar-portlet-wrapper" id="<portlet:namespace />scheduler"></div>

<%@ include file="/event_recorder.jspf" %>

<aui:script use="aui-toggler,liferay-calendar-list,liferay-scheduler,liferay-store,json">
	var calendarContainer = Liferay.component('<portlet:namespace />calendarContainer');

	var remoteServices = Liferay.component('<portlet:namespace />remoteServices');

	var showMoreStrings = {
		close: '<liferay-ui:message key="close" />',
		showMore: '<liferay-ui:message key="show-x-more" />'
	};

	<c:if test="<%= !hideDayView %>">
		window.<portlet:namespace />dayView = new Liferay.SchedulerDayView(
			{
				headerViewConfig: {
					eventsOverlayConstrain: '#p_p_id<portlet:namespace />',
					strings: showMoreStrings
				},
				height: 700,
				isoTime: <%= useIsoTimeFormat %>,
				readOnly: <%= readOnly %>,
				strings: {
					allDay: '<liferay-ui:message key="all-day" />'
				}
			}
		);
	</c:if>

	<c:if test="<%= !hideWeekView %>">
		window.<portlet:namespace />weekView = new Liferay.SchedulerWeekView(
			{
				headerViewConfig: {
					displayDaysInterval: A.DataType.DateMath.WEEK_LENGTH,
					eventsOverlayConstrain: '#p_p_id<portlet:namespace />',
					strings: showMoreStrings
				},
				height: 700,
				isoTime: <%= useIsoTimeFormat %>,
				readOnly: <%= readOnly %>,
				strings: {
					allDay: '<liferay-ui:message key="all-day" />'
				}
			}
		);
	</c:if>

	<c:if test="<%= !hideMonthView %>">
		window.<portlet:namespace />monthView = new Liferay.SchedulerMonthView(
			{
				eventsOverlayConstrain: '#p_p_id<portlet:namespace />',
				height: 'auto',
				isoTime: <%= useIsoTimeFormat %>,
				readOnly: <%= readOnly %>,
				strings: showMoreStrings
			}
		);
	</c:if>

	<c:if test="<%= !hideAgendaView %>">
		window.<portlet:namespace />agendaView = new Liferay.SchedulerAgendaView(
			{
				daysCount: 31,
				height: 700,
				isoTime: <%= useIsoTimeFormat %>,
				readOnly: <%= readOnly %>,
				strings: {
					noEvents: '<liferay-ui:message key="no-events" />'
				}
			}
		);
	</c:if>

	<c:choose>
		<c:when test="<%= !readOnly && (defaultCalendar != null) %>">
			var width = Math.min(Liferay.Util.getWindowWidth(), 550);

			window.<portlet:namespace />eventRecorder = new Liferay.SchedulerEventRecorder(
				{
					bodyTemplate: new A.Template(A.one('#<portlet:namespace />eventRecorderBodyTpl').html()),
					calendarContainer: calendarContainer,
					calendarId: <%= defaultCalendar.getCalendarId() %>,
					color: '<%= ColorUtil.toHexString(defaultCalendar.getColor()) %>',
					duration: <%= defaultDuration %>,
					editCalendarBookingURL: '<%= HtmlUtil.escapeJS(editCalendarBookingURL) %>',
					headerTemplate: new A.Template(A.one('#<portlet:namespace />eventRecorderHeaderTpl').html()),
					permissionsCalendarBookingURL: '<%= HtmlUtil.escapeJS(permissionsCalendarBookingURL) %>',
					popover: {
						width: width
					},
					portletNamespace: '<portlet:namespace />',
					remoteServices: remoteServices,
					showHeader: <%= showSchedulerHeader %>,
					strings: {
						'description-hint': '<liferay-ui:message key="description-hint" />'
					},
					viewCalendarBookingURL: '<%= HtmlUtil.escapeJS(viewCalendarBookingURL) %>'
				}
			);
		</c:when>
		<c:when test="<%= !readOnly && !groupCalendars.isEmpty() %>">

			<%
			Calendar groupDefaultCalendar = null;

			for (Calendar groupCalendar : groupCalendars) {
				if (groupCalendar.isDefaultCalendar() && CalendarPermission.contains(themeDisplay.getPermissionChecker(), groupCalendar, CalendarActionKeys.VIEW_BOOKING_DETAILS)) {
					groupDefaultCalendar = groupCalendar;
				}
			}
			%>

			<c:if test="<%= groupDefaultCalendar != null %>">
				var width = Math.min(Liferay.Util.getWindowWidth(), 550);

				window.<portlet:namespace />eventRecorder = new Liferay.SchedulerEventRecorder(
					{
						bodyTemplate: new A.Template(A.one('#<portlet:namespace />eventRecorderBodyTpl').html()),
						calendarContainer: calendarContainer,
						calendarId: <%= groupDefaultCalendar.getCalendarId() %>,
						color: '<%= ColorUtil.toHexString(groupDefaultCalendar.getColor()) %>',
						duration: <%= defaultDuration %>,
						editCalendarBookingURL: '<%= HtmlUtil.escapeJS(editCalendarBookingURL) %>',
						headerTemplate: new A.Template(A.one('#<portlet:namespace />eventRecorderHeaderTpl').html()),
						permissionsCalendarBookingURL: '<%= HtmlUtil.escapeJS(permissionsCalendarBookingURL) %>',
						popover: {
							width: width
						},
						portletNamespace: '<portlet:namespace />',
						remoteServices: remoteServices,
						showHeader: <%= showSchedulerHeader %>,
						strings: {
							'description-hint': '<liferay-ui:message key="description-hint" />'
						},
						viewCalendarBookingURL: '<%= HtmlUtil.escapeJS(viewCalendarBookingURL) %>'
					}
				);
			</c:if>

		</c:when>
	</c:choose>

	var views = [];

	<c:if test="<%= !hideDayView %>">
		views.push(window.<portlet:namespace />dayView);
	</c:if>

	<c:if test="<%= !hideWeekView %>">
		views.push(window.<portlet:namespace />weekView);
	</c:if>

	<c:if test="<%= !hideMonthView %>">
		views.push(window.<portlet:namespace />monthView);
	</c:if>

	<c:if test="<%= !hideAgendaView %>">
		views.push(window.<portlet:namespace />agendaView);
	</c:if>

	window.<portlet:namespace />scheduler = new Liferay.Scheduler(
		{
			activeView: window['<portlet:namespace /><%= HtmlUtil.escapeJS(activeView) %>View'],
			boundingBox: '#<portlet:namespace />scheduler',
			calendarContainer: calendarContainer,

			<%
			java.util.Calendar dateJCalendar = CalendarFactoryUtil.getCalendar(userTimeZone);

			dateJCalendar.setTimeInMillis(date);

			int dateYear = dateJCalendar.get(java.util.Calendar.YEAR);
			int dateMonth = dateJCalendar.get(java.util.Calendar.MONTH);
			int dateDay = dateJCalendar.get(java.util.Calendar.DAY_OF_MONTH);
			%>

			currentTimeFn: A.bind(remoteServices.getCurrentTime, remoteServices),
			date: new Date(<%= dateYear %>, <%= dateMonth %>, <%= dateDay %>),

			<c:if test="<%= !themeDisplay.isSignedIn() %>">
				disabled: true,
			</c:if>

			eventRecorder: window.<portlet:namespace />eventRecorder,
			eventsPerPage: <%= eventsPerPage %>,
			filterCalendarBookings: window['<%= HtmlUtil.escapeJS(filterCalendarBookings) %>'],
			firstDayOfWeek: <%= weekStartsOn %>,
			items: A.Object.values(calendarContainer.get('availableCalendars')),
			maxDaysDisplayed: <%= maxDaysDisplayed %>,
			portletNamespace: '<portlet:namespace />',
			preventPersistence: <%= preventPersistence %>,
			remoteServices: remoteServices,
			render: true,
			showAddEventBtn: <%= showAddEventBtn %>,
			showHeader: <%= showSchedulerHeader %>,
			strings: {
				agenda: '<liferay-ui:message key="agenda" />',
				day: '<liferay-ui:message key="day" />',
				month: '<liferay-ui:message key="month" />',
				today: '<liferay-ui:message key="today" />',
				week: '<liferay-ui:message key="week" />',
				year: '<liferay-ui:message key="year" />'
			},

			<%
			java.util.Calendar todayJCalendar = CalendarFactoryUtil.getCalendar(userTimeZone);

			int todayYear = todayJCalendar.get(java.util.Calendar.YEAR);
			int todayMonth = todayJCalendar.get(java.util.Calendar.MONTH);
			int todayDay = todayJCalendar.get(java.util.Calendar.DAY_OF_MONTH);
			%>

			todayDate: new Date(<%= todayYear %>, <%= todayMonth %>, <%= todayDay %>),
			views: views
		}
	);
</aui:script>