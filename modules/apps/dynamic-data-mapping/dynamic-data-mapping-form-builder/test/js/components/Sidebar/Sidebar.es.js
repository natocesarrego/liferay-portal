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

import Sidebar from '../../../../src/main/resources/META-INF/resources/js/components/Sidebar/Sidebar.es';
import mockFieldType from '../../__mock__/mockFieldType.es';

const mockFieldTypes = [
	{
		description: 'Select date from a Datepicker.',
		group: 'basic',
		icon: 'calendar',
		label: 'Date',
		name: 'date',
	},
	{
		description: 'Single line or multiline text area.',
		group: 'basic',
		icon: 'text',
		label: 'Text Field',
		name: 'text',
	},
	{
		description: 'Select only one item with a radio button.',
		group: 'basic',
		icon: 'radio-button',
		label: 'Single Selection',
		name: 'radio',
	},
	{
		description: 'Choose one or more options from a list.',
		group: 'basic',
		icon: 'list',
		label: 'Select from list',
		name: 'select',
	},
	{
		description: 'Select options from a matrix.',
		group: 'basic',
		icon: 'grid',
		label: 'Grid',
		name: 'grid',
	},
	{
		description: 'Select multiple options using a checkbox.',
		group: 'basic',
		icon: 'select-from-list',
		label: 'Multiple Selection',
		name: 'checkbox',
	},
].map(fieldType => ({
	...mockFieldType,
	...fieldType,
}));

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

describe('Sidebar', () => {
	beforeEach(() => {
		fetch.mockResponse(JSON.stringify({}), {
			status: 200,
		});

		jest.useFakeTimers();
	});

	afterEach(() => {
		if (component) {
			component.dispose();
		}

		jest.clearAllTimers();
	});

	it('renders a Sidebar open', () => {
		component = new Sidebar({
			fieldTypes: mockFieldTypes,
			spritemap: 'icons.svg',
		});

		component.open();

		expect(component.state.open).toBeTruthy();
	});

	it('renders a Sidebar closed', () => {
		component = new Sidebar({
			fieldTypes: mockFieldTypes,
			spritemap: 'icons.svg',
		});

		component.open();
		component.close();

		expect(component.state.open).toBeFalsy();
	});

	it('renders a Sidebar with fieldTypes', () => {
		component = new Sidebar({
			fieldTypes: mockFieldTypes,
			spritemap: 'icons.svg',
		});

		component.open();

		expect(component).toMatchSnapshot();
	});

	it('renders a Sidebar with fieldTypes separated by category', () => {
		component = new Sidebar({
			fieldTypes: mockFieldTypes,
			spritemap: 'icons.svg',
		});

		component.open();

		const basicTab = document.querySelector(
			'#ddm-field-types-basic-header'
		);
		expect(basicTab).toEqual(expect.anything());

		const customizedTab = document.querySelector(
			'#ddm-field-types-customized-header'
		);
		expect(customizedTab).toEqual(expect.anything());
	});

	it('closes the sidebar when the mouse down event is not on it', () => {
		component = new Sidebar({
			fieldTypes: mockFieldTypes,
			spritemap: 'icons.svg',
		});

		component.open();
		component._handleDocumentMouseDown({
			target: null,
		});

		expect(component.state.open).toBeFalsy();
	});

	it('emits the fieldDuplicated event when the duplicate field option is clicked on the sidebar settings', () => {
		const dispatch = jest.fn();

		component = new Sidebar({
			editingLanguageId: 'en_US',
			fieldTypes: mockFieldTypes,
			focusedField: mockFieldType,
			portletNamespace: 'portletNamespace',
			spritemap: 'icons.svg',
		});
		component.context.dispatch = dispatch;

		const spy = jest.spyOn(component, 'emit');

		const data = {
			item: {
				settingsItem: 'duplicate-field',
			},
		};

		component.open();
		component._handleElementSettingsClicked({data});

		expect(spy).toHaveBeenCalled();
		expect(dispatch).toHaveBeenCalledWith(
			'fieldDuplicated',
			expect.anything()
		);
	});

	it('emits the fieldDeleted event when the delete field option is clicked on the sidebar settings', () => {
		const dispatch = jest.fn();

		component = new Sidebar({
			editingLanguageId: 'en_US',
			fieldTypes: mockFieldTypes,
			focusedField: mockFieldType,
			portletNamespace: 'portletNamespace',
			spritemap: 'icons.svg',
		});
		component.context.dispatch = dispatch;

		const spy = jest.spyOn(component, 'emit');

		const data = {
			item: {
				settingsItem: 'delete-field',
			},
		};

		component.open();
		component._handleElementSettingsClicked({data});

		expect(spy).toHaveBeenCalled();
		expect(dispatch).toHaveBeenCalledWith(
			'fieldDeleted',
			expect.anything()
		);
	});

	describe('fieldChangesCanceled(state, event)', () => {
		it('emits event when the cancel field chages option is clicked on the sidebar settings', () => {
			const dispatch = jest.fn();

			component = new Sidebar({
				editingLanguageId: 'en_US',
				fieldTypes: mockFieldTypes,
				focusedField: mockFieldType,
				portletNamespace: 'portletNamespace',
				spritemap: 'icons.svg',
			});
			component.context.dispatch = dispatch;

			const spy = jest.spyOn(component, 'emit');

			const data = {
				item: {
					settingsItem: 'cancel-field-changes',
				},
			};

			component.open();
			component._handleElementSettingsClicked({data});

			expect(spy).toHaveBeenCalled();
		});

		it('shows modal when cancel field chages option is clicked on the sidebar settings', () => {
			const dispatch = jest.fn();

			component = new Sidebar({
				editingLanguageId: 'en_US',
				fieldTypes: mockFieldTypes,
				focusedField: mockFieldType,
				portletNamespace: 'portletNamespace',
				spritemap: 'icons.svg',
			});
			component.context.dispatch = dispatch;

			const data = {
				item: {
					settingsItem: 'cancel-field-changes',
				},
			};

			component.open();
			component._handleElementSettingsClicked({data});

			const {cancelChangesModal} = component.refs;

			expect(cancelChangesModal.body).toEqual(
				'are-you-sure-you-want-to-cancel'
			);
			expect(cancelChangesModal).toMatchSnapshot();
		});

		it('emits fieldChangesCanceled event when yes is clicked in the modal', () => {
			const dispatch = jest.fn();

			component = new Sidebar({
				editingLanguageId: 'en_US',
				fieldTypes: mockFieldTypes,
				focusedField: mockFieldType,
				portletNamespace: 'portletNamespace',
				spritemap: 'icons.svg',
			});
			component.context.dispatch = dispatch;

			const data = {
				item: {
					settingsItem: 'cancel-field-changes',
				},
			};

			component.open();
			component._handleElementSettingsClicked({data});

			document
				.querySelector(
					'.modal-content .btn-group .btn-group-item .btn-primary'
				)
				.click();

			expect(dispatch).toHaveBeenCalled();
			expect(dispatch).toHaveBeenCalledWith('fieldChangesCanceled', {});
		});
	});

	it('closes the sidebar in edition mode', () => {
		component = new Sidebar({
			fieldTypes: mockFieldTypes,
			spritemap: 'icons.svg',
		});

		component.open();
		component._handlePreviousButtonClicked();

		expect(component.state.open).toBeFalsy();
		expect(component).toMatchSnapshot();
	});

	it('propagates evaluator changed event', () => {
		const dispatch = jest.fn();

		component = new Sidebar({
			editingLanguageId: 'en_US',
			fieldTypes: mockFieldTypes,
			focusedField: mockFieldType,
			portletNamespace: 'portletNamespace',
			spritemap: 'icons.svg',
		});
		component.context.dispatch = dispatch;

		component.open();

		const changedFocusedField = {
			...mockFieldType,
			settingsContext: {
				...mockFieldType.settingsContext,
				pages: changePages(mockFieldType, 'required', false),
			},
		};

		component._handleEvaluatorChanged(
			changedFocusedField.settingsContext.pages
		);

		expect(dispatch).toHaveBeenCalledWith(
			'focusedFieldEvaluationEnded',
			changedFocusedField
		);
	});

	it('propagates field edited event', () => {
		const dispatch = jest.fn();

		component = new Sidebar({
			editingLanguageId: 'en_US',
			fieldTypes: mockFieldTypes,
			focusedField: mockFieldType,
			portletNamespace: 'portletNamespace',
			spritemap: 'icons.svg',
		});
		component.context.dispatch = dispatch;

		component.open();

		component._handleSettingsFieldEdited({
			fieldInstance: {
				fieldName: 'label',
				isDisposed: () => false,
			},
			value: 'Text Field 2',
		});

		expect(dispatch).toHaveBeenCalledWith('fieldEdited', {
			editingLanguageId: 'en_US',
			propertyName: 'label',
			propertyValue: 'Text Field 2',
		});
	});

	describe('Interaction with markup', () => {
		it('closes Sidebar when click the button close', () => {
			component = new Sidebar({
				fieldTypes: mockFieldTypes,
				spritemap: 'icons.svg',
			});

			component.open();

			expect(component.state.open).toBeTruthy();

			const spy = jest.spyOn(component, 'close');
			const {closeButton} = component.refs;

			closeButton.click();

			expect(component.state.open).toBeFalsy();
			expect(spy).toHaveBeenCalled();
		});
	});

	describe('Changing field type', () => {
		it('is always enabled when editingLanguageId is equal to defaultLanguageId', () => {
			component = new Sidebar({
				defaultLanguageId: 'en_US',
				editingLanguageId: 'en_US',
				fieldTypes: mockFieldTypes,
				focusedField: mockFieldType,
				portletNamespace: 'portletNamespace',
				spritemap: 'icons.svg',
			});

			expect(component.isChangeFieldTypeEnabled()).toBeTruthy();
		});

		it('is not enabled when editingLanguageId is not equal to defaultLanguageId', () => {
			component = new Sidebar({
				defaultLanguageId: 'en_US',
				editingLanguageId: 'pt_BR',
				fieldTypes: mockFieldTypes,
				focusedField: mockFieldType,
				portletNamespace: 'portletNamespace',
				spritemap: 'icons.svg',
			});

			expect(component.isChangeFieldTypeEnabled()).toBeFalsy();
		});
	});
});
