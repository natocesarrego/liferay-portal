<#--
Web content templates are used to lay out the fields defined in a web
content structure.
Please use the left panel to quickly add commonly used variables.
Autocomplete is also available and can be invoked by typing "${".
-->

<#assign normalizer = serviceLocator.findService("com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil")/>

<#assign browserSniffer = serviceLocator.findService("com.liferay.portal.kernel.servlet.BrowserSnifferUtil") />

<#assign browserSniffer = serviceLocator.findService("com.liferay.portal.kernel.servlet.BrowserSnifferUtil") >

<#assign
browserSniffer = serviceLocator.findService("com.liferay.portal.kernel.servlet.BrowserSnifferUtil")
variableA = "variable A"
assetEntryLocalService = serviceLocator.findService("com.liferay.asset.kernel.service.AssetEntryLocalService")
/>

<#assign
browserSniffer = serviceLocator.findService("com.liferay.portal.kernel.servlet.BrowserSnifferUtil")
variableB = "variable B"
assetEntryLocalService = serviceLocator.findService("com.liferay.asset.kernel.service.AssetEntryLocalService")
>