<#--
Web content templates are used to lay out the fields defined in a web
content structure.

Please use the left panel to quickly add commonly used variables.
Autocomplete is also available and can be invoked by typing "${".
-->

<#assign themeId = serviceLocator.findService("com.liferal.kernel.layout.ThemeId") />

<#assign browserSniffer = serviceLocator.findService("com.liferay.portal.kernel.servlet.BrowserSnifferUtil") />

<#assign browserSniffer = serviceLocator.findService("com.liferay.portal.kernel.servlet.BrowserSnifferUtil") >

<#assign
    browserSniffer = serviceLocator.findService("com.liferay.portal.kernel.servlet.BrowserSnifferUtil")
    variableA = "valirable A"
    themeId = serviceLocator.findService("com.liferal.kernel.layout.ThemeId")
/>

<#assign
    browserSniffer = serviceLocator.findService("com.liferay.portal.kernel.servlet.BrowserSnifferUtil")
    variableAB = "valirable B"
    themeId = serviceLocator.findService("com.liferal.kernel.layout.ThemeId")
>