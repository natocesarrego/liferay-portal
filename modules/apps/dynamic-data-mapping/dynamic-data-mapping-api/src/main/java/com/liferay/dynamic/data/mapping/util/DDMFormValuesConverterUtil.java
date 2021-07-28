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

package com.liferay.dynamic.data.mapping.util;

import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Mateus Santana
 */
public class DDMFormValuesConverterUtil {

	public static List<DDMFormFieldValue> addMissingDDMFormFieldValues(
		List<DDMFormField> ddmFormFields,
		Map<String, List<DDMFormFieldValue>> ddmFormFieldValues) {

		List<DDMFormFieldValue> ddmFormFieldValueList = new ArrayList<>();

		ddmFormFields.forEach(
			ddmFormField -> {
				List<DDMFormFieldValue> nestedDDMFormFieldValues =
					new ArrayList<>();

				if (StringUtil.equals(
						ddmFormField.getType(),
						DDMFormFieldTypeConstants.FIELDSET)) {

					nestedDDMFormFieldValues.addAll(
						addMissingDDMFormFieldValues(
							ddmFormField.getNestedDDMFormFields(),
							ddmFormFieldValues));
				}

				if (!ddmFormFieldValues.containsKey(ddmFormField.getName())) {
					_addNewMissedDDMFormFieldValue(
						ddmFormField, ddmFormFieldValueList,
						nestedDDMFormFieldValues);
				}
				else {
					if (ListUtil.isNotEmpty(nestedDDMFormFieldValues)) {
						_addNewMissedDDMFormFieldValue(
							ddmFormField, ddmFormFieldValueList,
							nestedDDMFormFieldValues);
					}
					else {
						ddmFormFieldValueList.addAll(
							ddmFormFieldValues.get(ddmFormField.getName()));
					}
				}
			});

		return ddmFormFieldValueList;
	}

	private static void _addNewMissedDDMFormFieldValue(
		DDMFormField ddmFormField,
		List<DDMFormFieldValue> ddmFormFieldValueList,
		List<DDMFormFieldValue> nestedDDMFormFieldValues) {

		DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue() {
			{
				setInstanceId(StringUtil.randomString());
				setName(ddmFormField.getName());
			}
		};

		for (DDMFormFieldValue nestedDDMFormFieldValue :
				nestedDDMFormFieldValues) {

			ddmFormFieldValue.addNestedDDMFormFieldValue(
				nestedDDMFormFieldValue);
		}

		ddmFormFieldValueList.add(ddmFormFieldValue);
	}

}