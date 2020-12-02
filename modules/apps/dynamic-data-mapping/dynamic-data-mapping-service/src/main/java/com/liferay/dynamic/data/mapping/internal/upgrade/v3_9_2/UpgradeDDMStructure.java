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

import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutSerializer;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutSerializerSerializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormLayoutSerializerSerializeResponse;
import com.liferay.dynamic.data.mapping.io.DDMFormSerializer;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidation;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidationExpression;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutColumn;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutPage;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutRow;
import com.liferay.dynamic.data.mapping.model.DDMFormRule;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.util.DDMFormDeserializeUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormLayoutDeserializeUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormSerializeUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Renato Rego
 */
public class UpgradeDDMStructure extends UpgradeProcess {

	public UpgradeDDMStructure(
		DDMFormDeserializer ddmFormDeserializer,
		DDMFormLayoutDeserializer ddmFormLayoutDeserializer,
		DDMFormLayoutSerializer ddmFormLayoutSerializer,
		DDMFormSerializer ddmFormSerializer, JSONFactory jsonFactory) {

		_ddmFormDeserializer = ddmFormDeserializer;
		_ddmFormLayoutDeserializer = ddmFormLayoutDeserializer;
		_ddmFormLayoutSerializer = ddmFormLayoutSerializer;
		_ddmFormSerializer = ddmFormSerializer;
		_jsonFactory = jsonFactory;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_upgradeDDMStructureLayout();

		_upgradeDDMStructureVersion();

		_upgradeDDMStructure();
	}

	protected void upgradeDDMForm(DDMForm ddmForm) {
		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		List<DDMFormField> ddmFormFields = new ArrayList<>(
			ddmFormFieldsMap.values());

		if (!ddmFormFields.isEmpty()) {
			ddmFormFields = _getNormalizedDDMFormFields(ddmFormFields);

			ddmForm.setDDMFormFields(ddmFormFields);
		}
	}

	protected void upgradeDDMFormLayout(DDMFormLayout ddmFormLayout) {
		for (DDMFormLayoutPage ddmFormLayoutPage :
				ddmFormLayout.getDDMFormLayoutPages()) {

			for (DDMFormLayoutRow ddmFormLayoutRow :
					ddmFormLayoutPage.getDDMFormLayoutRows()) {

				for (DDMFormLayoutColumn ddmFormLayoutColumn :
						ddmFormLayoutRow.getDDMFormLayoutColumns()) {

					List<String> ddmFormFieldNames =
						ddmFormLayoutColumn.getDDMFormFieldNames();

					if (!ddmFormFieldNames.isEmpty()) {
						ddmFormLayoutColumn.setDDMFormFieldNames(
							_getNormalizedDDMFormFieldNames(ddmFormFieldNames));
					}
				}
			}
		}
	}

	private List<String> _getNormalizedDDMFormFieldNames(
		List<String> ddmFormFieldNames) {

		Stream<String> ddmFormFieldNamesStream = ddmFormFieldNames.stream();

		return ddmFormFieldNamesStream.map(
			ddmFormFieldName -> {
				if (_isFirstLetterDigit(ddmFormFieldName)) {
					return StringPool.UNDERLINE + ddmFormFieldName;
				}

				return ddmFormFieldName;
			}
		).collect(
			Collectors.toList()
		);
	}

	private List<DDMFormField> _getNormalizedDDMFormFields(
		List<DDMFormField> ddmFormFields) {

		Stream<DDMFormField> ddmFormFieldsStream = ddmFormFields.stream();

		return ddmFormFieldsStream.map(
			ddmFormField -> {
				_normalizeDDMFormFieldFieldset(ddmFormField);

				_normalizeDDMFormFieldOptions(ddmFormField);

				_normalizeDDMFormFieldReference(ddmFormField);

				_normalizeDDMFormFieldValidationExpression(ddmFormField);

				_normalizeDDMFormRules(ddmFormField);

				_normalizeDDMFormFieldName(ddmFormField);

				return ddmFormField;
			}
		).collect(
			Collectors.toList()
		);
	}

	private List<DDMFormRule> _getNormalizedDDMFormRules(
		List<DDMFormRule> ddmFormRules, DDMFormField ddmFormField) {

		Stream<DDMFormRule> ddmFormRulesStream = ddmFormRules.stream();

		return ddmFormRulesStream.map(
			ddmFormRule -> {
				_normalizeDDMFormRuleActions(ddmFormRule, ddmFormField);

				_normalizeDDMFormRuleCondition(ddmFormRule, ddmFormField);

				return ddmFormRule;
			}
		).collect(
			Collectors.toList()
		);
	}

	private boolean _isFirstLetterDigit(String name) {
		return Character.isDigit(name.charAt(0));
	}

	private void _normalizeDDMFormFieldFieldset(DDMFormField ddmFormField) {
		String ddmFormFieldType = ddmFormField.getType();

		Object rows = ddmFormField.getProperty("rows");

		if (ddmFormFieldType.equals("fieldset") && (rows != null)) {
			try {
				JSONArray rowsJSONArray = _jsonFactory.createJSONArray(
					rows.toString());

				JSONArray newRowsJSONArray = _jsonFactory.createJSONArray();

				for (int i = 0; i < rowsJSONArray.length(); i++) {
					JSONObject rowJSONObject = rowsJSONArray.getJSONObject(i);

					JSONArray columnsJSONArray = rowJSONObject.getJSONArray(
						"columns");

					JSONArray newColumnsJSONArray =
						_jsonFactory.createJSONArray();

					for (int j = 0; j < columnsJSONArray.length(); j++) {
						JSONObject columnJSONObject =
							columnsJSONArray.getJSONObject(j);

						JSONArray fieldsJSONArray =
							columnJSONObject.getJSONArray("fields");

						JSONArray newFieldsJSONArray =
							_jsonFactory.createJSONArray();

						for (int k = 0; k < fieldsJSONArray.length(); k++) {
							String fieldName = fieldsJSONArray.getString(k);

							if (_isFirstLetterDigit(fieldName)) {
								newFieldsJSONArray.put(
									StringPool.UNDERLINE + fieldName);
							}
							else {
								newFieldsJSONArray.put(fieldName);
							}
						}

						columnJSONObject.put("fields", newFieldsJSONArray);

						newColumnsJSONArray.put(columnJSONObject);
					}

					rowJSONObject.put("columns", newColumnsJSONArray);

					newRowsJSONArray.put(rowJSONObject);
				}

				ddmFormField.setProperty("rows", newRowsJSONArray);
			}
			catch (JSONException jsonException) {
				_log.error("Unable to create JSON array", jsonException);
			}
		}
	}

	private void _normalizeDDMFormFieldName(DDMFormField ddmFormField) {
		if (_isFirstLetterDigit(ddmFormField.getName())) {
			ddmFormField.setName(StringPool.UNDERLINE + ddmFormField.getName());
		}
	}

	private void _normalizeDDMFormFieldOptions(DDMFormField ddmFormField) {
		DDMFormFieldOptions ddmFormFieldOptions =
			ddmFormField.getDDMFormFieldOptions();

		DDMFormFieldOptions newDDMFormFieldOptions = new DDMFormFieldOptions(
			ddmFormFieldOptions.getDefaultLocale());

		Map<String, LocalizedValue> newOptions =
			newDDMFormFieldOptions.getOptions();

		Map<String, LocalizedValue> options = ddmFormFieldOptions.getOptions();

		Map<String, String> newOptionsReferences =
			newDDMFormFieldOptions.getOptionsReferences();

		Map<String, String> optionsReferences =
			ddmFormFieldOptions.getOptionsReferences();

		for (String optionValue : ddmFormFieldOptions.getOptionsValues()) {
			if (_isFirstLetterDigit(optionValue)) {
				newOptions.put(
					StringPool.UNDERLINE + optionValue,
					options.get(optionValue));

				newOptionsReferences.put(
					StringPool.UNDERLINE + optionValue,
					optionsReferences.get(optionValue));
			}
			else {
				newOptions.put(optionValue, options.get(optionValue));

				newOptionsReferences.put(
					optionValue, optionsReferences.get(optionValue));
			}
		}

		ddmFormField.setDDMFormFieldOptions(newDDMFormFieldOptions);
	}

	private void _normalizeDDMFormFieldReference(DDMFormField ddmFormField) {
		String fieldReference = ddmFormField.getFieldReference();

		if (Validator.isNotNull(fieldReference) &&
			_isFirstLetterDigit(fieldReference)) {

			ddmFormField.setFieldReference(
				StringPool.UNDERLINE + fieldReference);
		}
	}

	private void _normalizeDDMFormFieldValidationExpression(
		DDMFormField ddmFormField) {

		DDMFormFieldValidation ddmFormFieldValidation =
			ddmFormField.getDDMFormFieldValidation();

		if (ddmFormFieldValidation != null) {
			DDMFormFieldValidationExpression ddmFormFieldValidationExpression =
				ddmFormFieldValidation.getDDMFormFieldValidationExpression();

			String ddmFormFieldValidationExpressionValue =
				ddmFormFieldValidationExpression.getValue();

			String ddmFormFieldName = ddmFormField.getName();

			if (ddmFormFieldValidationExpressionValue.contains(
					ddmFormFieldName)) {

				ddmFormFieldValidationExpression.setValue(
					StringUtil.replace(
						ddmFormFieldValidationExpressionValue, ddmFormFieldName,
						StringPool.UNDERLINE + ddmFormFieldName));
			}

			ddmFormField.setDDMFormFieldValidation(ddmFormFieldValidation);
		}
	}

	private void _normalizeDDMFormRuleActions(
		DDMFormRule ddmFormRule, DDMFormField ddmFormField) {

		List<String> ddmFormRuleActions = ddmFormRule.getActions();

		String ddmFormFieldName = ddmFormField.getName();

		List<String> newDDMFormRuleActions = new ArrayList<>();

		for (String ddmFormRuleAction : ddmFormRuleActions) {
			if (ddmFormRuleAction.contains(ddmFormFieldName)) {
				newDDMFormRuleActions.add(
					StringUtil.replace(
						ddmFormRuleAction, ddmFormFieldName,
						StringPool.UNDERLINE + ddmFormFieldName));
			}
			else {
				newDDMFormRuleActions.add(ddmFormRuleAction);
			}
		}

		ddmFormRule.setActions(newDDMFormRuleActions);
	}

	private void _normalizeDDMFormRuleCondition(
		DDMFormRule ddmFormRule, DDMFormField ddmFormField) {

		String ddmFormRuleCondition = ddmFormRule.getCondition();

		String ddmFormFieldName = ddmFormField.getName();

		if (ddmFormRuleCondition.contains(ddmFormFieldName)) {
			ddmFormRule.setCondition(
				StringUtil.replace(
					ddmFormRuleCondition, ddmFormFieldName,
					StringPool.UNDERLINE + ddmFormFieldName));
		}
	}

	private void _normalizeDDMFormRules(DDMFormField ddmFormField) {
		DDMForm ddmForm = ddmFormField.getDDMForm();

		List<DDMFormRule> ddmFormRules = ddmForm.getDDMFormRules();

		if (!ddmFormRules.isEmpty()) {
			ddmForm.setDDMFormRules(
				_getNormalizedDDMFormRules(ddmFormRules, ddmFormField));
		}
	}

	private void _upgradeDDMStructure() throws Exception {
		StringBundler sb = new StringBundler(6);

		sb.append("select DDMStructure.structureId, ");
		sb.append("DDMStructureVersion.definition from DDMStructure inner ");
		sb.append("join DDMStructureVersion on DDMStructure.structureid = ");
		sb.append("DDMStructureVersion.structureid where ");
		sb.append("DDMStructure.version = DDMStructureVersion.version and ");
		sb.append("DDMStructure.classNameId = ?");

		try (PreparedStatement ps1 = connection.prepareStatement(sb.toString());
			PreparedStatement ps2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMStructure set definition = ? where " +
						"structureId = ?")) {

			ps1.setLong(
				1, PortalUtil.getClassNameId(DDMFormInstance.class.getName()));

			try (ResultSet rs = ps1.executeQuery()) {
				while (rs.next()) {
					ps2.setString(1, rs.getString("definition"));

					ps2.setLong(2, rs.getLong("structureId"));

					ps2.addBatch();
				}

				ps2.executeBatch();
			}
		}
	}

	private void _upgradeDDMStructureLayout() throws Exception {
		StringBundler sb = new StringBundler(8);

		sb.append("select DDMStructureLayout.structureLayoutId, ");
		sb.append("DDMStructureLayout.definition from DDMStructureLayout ");
		sb.append("inner join DDMStructureVersion on ");
		sb.append("DDMStructureLayout.structureVersionId = ");
		sb.append("DDMStructureVersion.structureVersionId inner join ");
		sb.append("DDMStructure on DDMStructure.structureId = ");
		sb.append("DDMStructureVersion.structureId where ");
		sb.append("DDMStructure.classNameId = ?");

		try (PreparedStatement ps1 = connection.prepareStatement(sb.toString());
			PreparedStatement ps2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMStructureLayout set definition = ? where " +
						"structureLayoutId = ?")) {

			ps1.setLong(
				1, PortalUtil.getClassNameId(DDMFormInstance.class.getName()));

			try (ResultSet rs = ps1.executeQuery()) {
				while (rs.next()) {
					ps2.setString(
						1,
						_upgradeDDMStructureLayoutDefinition(
							rs.getString("definition")));

					long structureLayoutId = rs.getLong("structureLayoutId");

					ps2.setLong(2, structureLayoutId);

					ps2.addBatch();
				}

				ps2.executeBatch();
			}
		}
	}

	private String _upgradeDDMStructureLayoutDefinition(String definition)
		throws Exception {

		DDMFormLayout ddmFormLayout = DDMFormLayoutDeserializeUtil.deserialize(
			_ddmFormLayoutDeserializer, definition);

		upgradeDDMFormLayout(ddmFormLayout);

		DDMFormLayoutSerializerSerializeResponse
			ddmFormLayoutSerializerSerializeResponse =
				_ddmFormLayoutSerializer.serialize(
					DDMFormLayoutSerializerSerializeRequest.Builder.newBuilder(
						ddmFormLayout
					).build());

		return ddmFormLayoutSerializerSerializeResponse.getContent();
	}

	private void _upgradeDDMStructureVersion() throws Exception {
		StringBundler sb = new StringBundler(5);

		sb.append("select DDMStructureVersion.structureVersionId, ");
		sb.append("DDMStructureVersion.definition from DDMStructure inner ");
		sb.append("join DDMStructureVersion on DDMStructure.structureId = ");
		sb.append("DDMStructureVersion.structureId where ");
		sb.append("DDMStructure.classNameId = ?");

		try (PreparedStatement ps1 = connection.prepareStatement(sb.toString());
			PreparedStatement ps2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMStructureVersion set definition = ? where " +
						"structureVersionId = ?")) {

			ps1.setLong(
				1, PortalUtil.getClassNameId(DDMFormInstance.class.getName()));

			try (ResultSet rs = ps1.executeQuery()) {
				while (rs.next()) {
					ps2.setString(
						1,
						_upgradeDDMStructureVersionDefinition(
							rs.getString("definition")));

					long structureVersionId = rs.getLong("structureVersionId");

					ps2.setLong(2, structureVersionId);

					ps2.addBatch();
				}

				ps2.executeBatch();
			}
		}
	}

	private String _upgradeDDMStructureVersionDefinition(String definition)
		throws Exception {

		DDMForm ddmForm = DDMFormDeserializeUtil.deserialize(
			_ddmFormDeserializer, definition);

		upgradeDDMForm(ddmForm);

		return DDMFormSerializeUtil.serialize(ddmForm, _ddmFormSerializer);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradeDDMStructure.class);

	private final DDMFormDeserializer _ddmFormDeserializer;
	private final DDMFormLayoutDeserializer _ddmFormLayoutDeserializer;
	private final DDMFormLayoutSerializer _ddmFormLayoutSerializer;
	private final DDMFormSerializer _ddmFormSerializer;
	private final JSONFactory _jsonFactory;

}