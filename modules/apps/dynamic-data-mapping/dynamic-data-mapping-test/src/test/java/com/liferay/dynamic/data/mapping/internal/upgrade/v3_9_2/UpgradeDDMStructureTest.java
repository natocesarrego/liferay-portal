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

package com.liferay.dynamic.data.mapping.internal.upgrade.v3_9_2;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesTracker;
import com.liferay.dynamic.data.mapping.form.field.type.internal.fieldset.FieldSetDDMFormFieldType;
import com.liferay.dynamic.data.mapping.form.field.type.internal.select.SelectDDMFormFieldType;
import com.liferay.dynamic.data.mapping.form.field.type.internal.text.TextDDMFormFieldType;
import com.liferay.dynamic.data.mapping.internal.io.DDMFormJSONDeserializer;
import com.liferay.dynamic.data.mapping.internal.io.DDMFormLayoutJSONDeserializer;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidation;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutColumn;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutPage;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutRow;
import com.liferay.dynamic.data.mapping.model.DDMFormRule;
import com.liferay.dynamic.data.mapping.util.DDMFormDeserializeUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormLayoutDeserializeUtil;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Renato Rego
 */
@PrepareForTest({LocaleUtil.class, ResourceBundleUtil.class})
@RunWith(PowerMockRunner.class)
public class UpgradeDDMStructureTest extends PowerMockito {

	@Before
	public void setUp() throws Exception {
		_setUpDDMFormFieldTypeServicesTracker();
		_setUpJSONFactory();
		_setUpJSONFactoryUtil();
		_setUpLanguageUtil();
		_setUpLocaleUtil();
		_setUpPortalUtil();
		_setUpResourceBundleUtil();
	}

	@Test
	public void testUpgradeDDMForm() throws Exception, IOException {
		String definition = _getDDMStructureLayoutDefinition(
			"dynamic-data-mapping-structure-version-definition.json");

		DDMForm ddmForm = DDMFormDeserializeUtil.deserialize(
			_ddmFormJSONDeserializer, definition);

		_upgradeDDMStructure.upgradeDDMForm(ddmForm);

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		List<DDMFormFieldValidation> expectedDDMFormFieldsValidations =
			new ArrayList<>();

		Set<String> expectedOptionsValues = new HashSet<>();

		List<Object> expectedRows = new ArrayList<>();

		for (DDMFormField ddmFormField : ddmFormFieldsMap.values()) {
			if (ddmFormField.getDDMFormFieldValidation() != null) {
				expectedDDMFormFieldsValidations.add(
					ddmFormField.getDDMFormFieldValidation());
			}

			DDMFormFieldOptions ddmFormFieldOptions =
				ddmFormField.getDDMFormFieldOptions();

			Set<String> optionsValues = ddmFormFieldOptions.getOptionsValues();

			if (!optionsValues.isEmpty()) {
				expectedOptionsValues.addAll(optionsValues);
			}

			String ddmFormFieldType = ddmFormField.getType();

			Object rows = ddmFormField.getProperty("rows");

			if (ddmFormFieldType.equals("fieldset") && (rows != null)) {
				expectedRows.add(rows);
			}
		}

		_assertNormalizedDDMFormFieldsNames(ddmFormFieldsMap.keySet());

		_assertNormalizedDDMFormFieldsReferences(ddmForm);

		_assertNormalizedDDMFormFieldsValidations(
			expectedDDMFormFieldsValidations);

		_assertNormalizedDDMFormRules(ddmForm.getDDMFormRules());

		_assertNormalizedOptionsValues(expectedOptionsValues);

		_assertNormalizedRows(expectedRows);
	}

	@Test
	public void testUpgradeDDMFormLayout() throws Exception, IOException {
		String definition = _getDDMStructureLayoutDefinition(
			"dynamic-data-mapping-structure-layout-definition.json");

		DDMFormLayout ddmFormLayout = DDMFormLayoutDeserializeUtil.deserialize(
			_ddmFormLayoutJSONDeserializer, definition);

		_upgradeDDMStructure.upgradeDDMFormLayout(ddmFormLayout);

		List<String> expectedDDMFormFieldNames = new ArrayList<>();

		for (DDMFormLayoutPage ddmFormLayoutPage :
				ddmFormLayout.getDDMFormLayoutPages()) {

			for (DDMFormLayoutRow ddmFormLayoutRow :
					ddmFormLayoutPage.getDDMFormLayoutRows()) {

				for (DDMFormLayoutColumn ddmFormLayoutColumn :
						ddmFormLayoutRow.getDDMFormLayoutColumns()) {

					List<String> ddmFormFieldNames =
						ddmFormLayoutColumn.getDDMFormFieldNames();

					if (!ddmFormFieldNames.isEmpty()) {
						expectedDDMFormFieldNames.addAll(ddmFormFieldNames);
					}
				}
			}
		}

		Stream<String> expectedDDMFormFieldNamesStream =
			expectedDDMFormFieldNames.stream();

		Assert.assertEquals(
			expectedDDMFormFieldNames.size(),
			_countNormalizedItems(expectedDDMFormFieldNamesStream));
	}

	private void _assertNormalizedDDMFormFieldsNames(
		Set<String> ddmFormFieldsNames) {

		Stream<String> ddmFormFieldsNamesStream = ddmFormFieldsNames.stream();

		Assert.assertEquals(5, _countNormalizedItems(ddmFormFieldsNamesStream));
	}

	private void _assertNormalizedDDMFormFieldsReferences(DDMForm ddmForm) {
		Map<String, DDMFormField> ddmFormFieldsReferencesMap =
			ddmForm.getDDMFormFieldsReferencesMap(true);

		Set<String> ddmFormFieldsReferences =
			ddmFormFieldsReferencesMap.keySet();

		Stream<String> ddmFormFieldsReferencesStream =
			ddmFormFieldsReferences.stream();

		Assert.assertEquals(
			5, _countNormalizedItems(ddmFormFieldsReferencesStream));
	}

	private void _assertNormalizedDDMFormFieldsValidations(
		List<DDMFormFieldValidation> expectedDDMFormFieldsValidations) {

		Stream<DDMFormFieldValidation> expectedDDMFormFieldsValidationsStream =
			expectedDDMFormFieldsValidations.stream();

		Assert.assertEquals(
			1,
			_sumNormalizedItems(
				expectedDDMFormFieldsValidationsStream.map(
					ddmFormFieldValidation ->
						ddmFormFieldValidation.
							getDDMFormFieldValidationExpression()
				).map(
					ddmFormFieldValidationExpression ->
						ddmFormFieldValidationExpression.getValue()
				)));
	}

	private void _assertNormalizedDDMFormRules(List<DDMFormRule> ddmFormRules) {
		List<String> expectedDDMFormRulesActions = new ArrayList<>();

		List<String> expectedDDMFormRulesConditions = new ArrayList<>();

		for (DDMFormRule ddmFormRule : ddmFormRules) {
			List<String> ddmFormRuleActions = ddmFormRule.getActions();

			expectedDDMFormRulesActions.addAll(ddmFormRuleActions);

			String ddmFormRuleCondition = ddmFormRule.getCondition();

			expectedDDMFormRulesConditions.add(ddmFormRuleCondition);
		}

		Stream<String> expectedDDMFormRulesActionsStream =
			expectedDDMFormRulesActions.stream();

		Assert.assertEquals(
			1, _sumNormalizedItems(expectedDDMFormRulesActionsStream));

		Stream<String> expectedDDMFormRulesConditionsStream =
			expectedDDMFormRulesConditions.stream();

		Assert.assertEquals(
			0, _sumNormalizedItems(expectedDDMFormRulesConditionsStream));
	}

	private void _assertNormalizedOptionsValues(
		Set<String> expectedOptionsValues) {

		Stream<String> expectedOptionsValuesStream =
			expectedOptionsValues.stream();

		Assert.assertEquals(
			3, _countNormalizedItems(expectedOptionsValuesStream));
	}

	private void _assertNormalizedRows(List<Object> expectedRows) {
		Stream<Object> expectedRowsStream = expectedRows.stream();

		Stream<String> expectedRowsStringStream = expectedRowsStream.map(
			row -> row.toString());

		Assert.assertEquals(2, _sumNormalizedItems(expectedRowsStringStream));
	}

	private long _countNormalizedItems(Stream<String> stream) {
		return stream.filter(
			item -> item.charAt(0) == '_'
		).count();
	}

	private String _getDDMStructureLayoutDefinition(String fileName)
		throws IOException {

		Class<?> clazz = getClass();

		InputStream inputStream = clazz.getResourceAsStream(
			"dependencies/" + fileName);

		return StringUtil.read(inputStream);
	}

	private void _setUpDDMFormFieldTypeServicesTracker() throws Exception {
		MemberMatcher.field(
			DDMFormJSONDeserializer.class, "_ddmFormFieldTypeServicesTracker"
		).set(
			_ddmFormJSONDeserializer, _ddmFormFieldTypeServicesTracker
		);

		PowerMockito.when(
			_ddmFormFieldTypeServicesTracker.getDDMFormFieldType("fieldset")
		).thenReturn(
			new FieldSetDDMFormFieldType()
		);

		PowerMockito.when(
			_ddmFormFieldTypeServicesTracker.getDDMFormFieldType("select")
		).thenReturn(
			new SelectDDMFormFieldType()
		);

		PowerMockito.when(
			_ddmFormFieldTypeServicesTracker.getDDMFormFieldType("text")
		).thenReturn(
			new TextDDMFormFieldType()
		);
	}

	private void _setUpJSONFactory() throws Exception {
		MemberMatcher.field(
			DDMFormJSONDeserializer.class, "_jsonFactory"
		).set(
			_ddmFormJSONDeserializer, _jsonFactory
		);

		MemberMatcher.field(
			DDMFormLayoutJSONDeserializer.class, "_jsonFactory"
		).set(
			_ddmFormLayoutJSONDeserializer, _jsonFactory
		);

		MemberMatcher.field(
			UpgradeDDMStructure.class, "_jsonFactory"
		).set(
			_upgradeDDMStructure, _jsonFactory
		);
	}

	private void _setUpJSONFactoryUtil() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(PowerMockito.mock(Language.class));
	}

	private void _setUpLocaleUtil() {
		mockStatic(LocaleUtil.class);

		when(
			LocaleUtil.fromLanguageId("en_US")
		).thenReturn(
			LocaleUtil.US
		);
	}

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		Portal portal = PowerMockito.mock(Portal.class);

		ResourceBundle resourceBundle = PowerMockito.mock(ResourceBundle.class);

		PowerMockito.when(
			portal.getResourceBundle(Matchers.any(Locale.class))
		).thenReturn(
			resourceBundle
		);

		portalUtil.setPortal(portal);
	}

	private void _setUpResourceBundleUtil() {
		PowerMockito.mockStatic(ResourceBundleUtil.class);

		Mockito.when(
			ResourceBundleUtil.getBundle(
				Matchers.anyString(), Matchers.any(Locale.class),
				Matchers.any(ClassLoader.class))
		).thenReturn(
			ResourceBundleUtil.EMPTY_RESOURCE_BUNDLE
		);
	}

	private int _sumNormalizedItems(Stream<String> stream) {
		return stream.mapToInt(
			item -> {
				String[] itemParts = item.split(
					_NORMALIZED_DDM_FORM_FIELD_NAME_REGEX);

				return itemParts.length - 1;
			}
		).sum();
	}

	private static final String _NORMALIZED_DDM_FORM_FIELD_NAME_REGEX =
		"\\_[0-9][a-zA-Z]+";

	@Mock
	private DDMFormFieldTypeServicesTracker _ddmFormFieldTypeServicesTracker;

	private final DDMFormJSONDeserializer _ddmFormJSONDeserializer =
		new DDMFormJSONDeserializer();
	private final DDMFormLayoutJSONDeserializer _ddmFormLayoutJSONDeserializer =
		new DDMFormLayoutJSONDeserializer();
	private final JSONFactory _jsonFactory = new JSONFactoryImpl();
	private final UpgradeDDMStructure _upgradeDDMStructure =
		new UpgradeDDMStructure(null, null, null, null, null);

}