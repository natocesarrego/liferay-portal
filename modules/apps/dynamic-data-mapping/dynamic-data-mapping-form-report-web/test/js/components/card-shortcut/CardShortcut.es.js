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

import {act, cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';

import CardShortcut from '../../../../src/main/resources/META-INF/resources/js/components/card-shortcut/CardShortcut.es';
import SidebarContextProviderWrapper from '../../SidebarContextProviderWrapper.es';

const fields = [
	{
		label: 'Field 1',
		name: 'field1',
		options: {Option: 'Option'},
		type: 'text',
	},
	{
		label: 'Field 2',
		name: 'field2',
		options: {option1: 'option1'},
		type: 'radio',
	},
];

const setHash = () => {
	delete window.location;

	window.location = {reload: jest.fn()};

	global.window = Object.create(window);

	const hash =
		'#_com_liferay_dynamic_data_mapping_form_report_web_portlet_DDMFormReportPortlet_card_1';

	Object.defineProperty(window, 'location', {
		value: {
			hash,
		},
	});
};

describe('CardShortcut', () => {
	afterEach(cleanup);

	const {location} = window;

	beforeEach(() => {
		jest.useFakeTimers();
	});

	afterAll(() => {
		window.location = location;
	});

	it('anchors are created for each field', () => {
		const {getByText} = render(
			<SidebarContextProviderWrapper>
				<CardShortcut fields={fields} />
			</SidebarContextProviderWrapper>
		);

		fields.forEach((field, index) => {
			expect(getByText(field.label).closest('a').href).toBe(
				`http://localhost/#_com_liferay_dynamic_data_mapping_form_report_web_portlet_DDMFormReportPortlet_card_${index}`
			);
		});
	});

	it('it highlights the item selected', () => {
		const {getByText} = render(
			<SidebarContextProviderWrapper>
				<CardShortcut fields={fields} />
			</SidebarContextProviderWrapper>
		);

		const item = getByText('Field 2');

		expect(item.closest('.selected')).toBe(null);

		fireEvent.click(item);

		act(() => {
			jest.runAllTimers();
		});

		expect(item.closest('.selected')).not.toBe(null);
	});

	it('when page reloads it selects the item according to card number of url', () => {
		setHash();

		const {getByText} = render(
			<SidebarContextProviderWrapper>
				<CardShortcut fields={fields} />
			</SidebarContextProviderWrapper>
		);

		const field1 = getByText('Field 1');
		const field2 = getByText('Field 2');

		expect(field1.closest('.selected')).toBe(null);
		expect(field2.closest('.selected')).not.toBe(null);
	});

	it('when changing tabs and return to Entries view, if an item was selected, the page is scrolled to the hash of url', () => {
		setHash();

		const scrollIntoViewMock = jest.fn();

		window.HTMLElement.prototype.scrollIntoView = scrollIntoViewMock;

		const element = document.createElement('div');
		element.setAttribute(
			'id',
			'_com_liferay_dynamic_data_mapping_form_report_web_portlet_DDMFormReportPortlet_card_1'
		);
		document.body.appendChild(element);

		render(
			<SidebarContextProviderWrapper>
				<CardShortcut fields={fields} />
			</SidebarContextProviderWrapper>
		);

		expect(scrollIntoViewMock).toBeCalled();
	});
});
