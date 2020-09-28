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

import {FormSupport} from 'dynamic-data-mapping-form-renderer';

import * as fieldDeletedHandler from '../../../../../src/main/resources/META-INF/resources/js/components/LayoutProvider/handlers/fieldDeletedHandler.es';
import mockPages from '../../../__mock__/mockPages.es';

describe('LayoutProvider/handlers/fieldDeletedHandler', () => {
	describe('handleFieldDeleted(state, event)', () => {
		it('calls removeEmptyRows() when row is left with no fields after delete operation', () => {
			const event = {
				activePage: 0,
				fieldName: 'radio',
				removeEmptyRows: true,
			};
			const state = {
				pages: mockPages,
				rules: [],
			};

			const removeRowSpy = jest.spyOn(FormSupport, 'removeEmptyRows');

			removeRowSpy.mockImplementation(() => []);

			fieldDeletedHandler.handleFieldDeleted({}, state, event);

			expect(removeRowSpy).toHaveBeenCalled();

			removeRowSpy.mockRestore();
		});
	});
});
