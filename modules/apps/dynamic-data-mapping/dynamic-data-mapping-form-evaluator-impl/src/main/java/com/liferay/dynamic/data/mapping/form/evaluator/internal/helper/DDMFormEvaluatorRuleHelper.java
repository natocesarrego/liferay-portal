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

package com.liferay.dynamic.data.mapping.form.evaluator.internal.helper;

import com.liferay.dynamic.data.mapping.expression.UpdateFieldPropertyRequest;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.expression.DDMFormEvaluatorExpressionObserver;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormRule;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Rafael Praxedes
 */
public class DDMFormEvaluatorRuleHelper {

	public DDMFormEvaluatorRuleHelper(
		Map<String, DDMFormField> ddmFormFieldsMap,
		DDMFormEvaluatorExpressionObserver ddmFormEvaluatorExpressionObserver) {

		_ddmFormFieldsMap = ddmFormFieldsMap;
		_ddmFormEvaluatorExpressionObserver =
			ddmFormEvaluatorExpressionObserver;
	}

	public void checkFieldAffectedByAction(
		DDMFormRule ddmFormRule, List<String> actionsAlreadyEvaluated) {

		Collection<DDMFormField> fieldNameSet = _ddmFormFieldsMap.values();

		Stream<DDMFormField> stream = fieldNameSet.stream();

		stream.forEach(
			field -> checkFieldAffectedByAction(
				ddmFormRule, field, actionsAlreadyEvaluated));
	}

	protected void checkFieldAffectedByAction(
		DDMFormRule ddmFormRule, DDMFormField ddmFormField,
		List<String> actionsAlreadyEvaluated) {

		checkFieldAffectedBySetReadOnlyAction(
			ddmFormRule, ddmFormField, actionsAlreadyEvaluated);
		checkFieldAffectedBySetRequiredAction(
			ddmFormRule, ddmFormField, actionsAlreadyEvaluated);
		checkFieldAffectedBySetVisibleAction(
			ddmFormRule, ddmFormField, actionsAlreadyEvaluated);
	}

	protected void checkFieldAffectedBySetReadOnlyAction(
		DDMFormRule ddmFormRule, DDMFormField ddmFormField,
		List<String> actionsAlreadyEvaluated) {

		if (containsAction(
				ddmFormRule, "setEnabled", ddmFormField.getName(),
				!ddmFormField.isReadOnly(), actionsAlreadyEvaluated)) {

			UpdateFieldPropertyRequest.Builder builder =
				UpdateFieldPropertyRequest.Builder.newBuilder(
					ddmFormField.getName(), "readOnly",
					!ddmFormField.isReadOnly());

			_ddmFormEvaluatorExpressionObserver.updateFieldProperty(
				builder.build());
		}
	}

	protected void checkFieldAffectedBySetRequiredAction(
		DDMFormRule ddmFormRule, DDMFormField ddmFormField,
		List<String> actionsAlreadyEvaluated) {

		if (containsAction(
				ddmFormRule, "setRequired", ddmFormField.getName(),
				ddmFormField.isRequired(), actionsAlreadyEvaluated)) {

			UpdateFieldPropertyRequest.Builder builder =
				UpdateFieldPropertyRequest.Builder.newBuilder(
					ddmFormField.getName(), "required",
					!ddmFormField.isRequired());

			_ddmFormEvaluatorExpressionObserver.updateFieldProperty(
				builder.build());
		}
	}

	protected void checkFieldAffectedBySetVisibleAction(
		DDMFormRule ddmFormRule, DDMFormField ddmFormField,
		List<String> actionsAlreadyEvaluated) {

		if (containsAction(
				ddmFormRule, "setVisible", ddmFormField.getName(), true,
				actionsAlreadyEvaluated)) {

			UpdateFieldPropertyRequest.Builder builder =
				UpdateFieldPropertyRequest.Builder.newBuilder(
					ddmFormField.getName(), "visible", false);

			_ddmFormEvaluatorExpressionObserver.updateFieldProperty(
				builder.build());
		}
	}

	protected boolean containsAction(
		DDMFormRule ddmFormRule, String functionName, String ddmFormFieldName,
		boolean defaultValue, List<String> actionsAlreadyEvaluated) {

		String setBooleanPropertyAction = String.format(
			"%s('%s', %s)", functionName, ddmFormFieldName, defaultValue);

		List<String> actions = ddmFormRule.getActions();

		Stream<String> stream = actions.stream();

		Stream<String> actionsAlreadyEvaluatedStream =
			actionsAlreadyEvaluated.stream();

		boolean alreadyEvaluated = actionsAlreadyEvaluatedStream.anyMatch(
			action -> action.contains(setBooleanPropertyAction));

		if (stream.anyMatch(
				action -> Objects.equals(setBooleanPropertyAction, action)) &&
			!alreadyEvaluated) {

			return true;
		}

		return false;
	}

	private final DDMFormEvaluatorExpressionObserver
		_ddmFormEvaluatorExpressionObserver;
	private final Map<String, DDMFormField> _ddmFormFieldsMap;

}