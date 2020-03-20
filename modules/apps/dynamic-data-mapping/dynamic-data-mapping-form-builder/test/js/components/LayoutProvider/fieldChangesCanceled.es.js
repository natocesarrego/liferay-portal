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

import {PagesVisitor} from 'dynamic-data-mapping-form-renderer';

import LayoutProvider from '../../../../src/main/resources/META-INF/resources/js/components/LayoutProvider/LayoutProvider.es';
import mockFieldType from '../../__mock__/mockFieldType.es';
import mockPages from '../../__mock__/mockPages.es';

const changePages = ({settingsContext}, fieldName, value) => {
	const visitor = new PagesVisitor(settingsContext.pages);

	return visitor.mapFields(field => {
		if (field.fieldName === fieldName) {
			field = {
				...field,
				value,
			};
		}

		return field;
	});
};

let component;

describe('LayoutProvider', () => {
	beforeEach(() => {
		fetch.mockResponse(JSON.stringify({}), {
			status: 200,
		});
	});

	afterEach(() => {
		if (component) {
			component.dispose();
		}
	});

	describe('fieldChangesCanceled(state, event)', () => {
		it('listens the fieldChangesCanceled event and change the state of the focusedField and pages for the data wich was received', () => {
			component = new LayoutProvider({
				editingLanguageId: 'en_US',
				focusedField: mockFieldType,
				initialPages: mockPages,
				pages: mockPages,
			});

			component.setState({
				previousFocusedField: mockFieldType,
			});

			const changedFocusedField = {
				...mockFieldType,
				settingsContext: {
					...mockFieldType.settingsContext,
					pages: changePages(mockFieldType, 'required', false),
				},
			};

			component.setState({
				focusedField: changedFocusedField,
			});

			expect(component.state.focusedField).toEqual(changedFocusedField);

			component._handleFieldChangesCanceled();

			expect(component.state.focusedField).toEqual(
				component.state.previousFocusedField
			);
		});
	});
});
