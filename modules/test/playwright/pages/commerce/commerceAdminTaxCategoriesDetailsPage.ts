/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class CommerceAdminTaxCategoriesDetailsPage {
	readonly externalReferenceCode: Locator;
	readonly saveButton: Locator;
	readonly taxCategoriesNameInput: Locator;
	readonly taxCategoriesDescriptionInput: Locator;
	readonly page: Page;
	
	constructor(page: Page) {
		this.externalReferenceCode = page.getByRole ('textbox', {
			name: 'External Reference Code'
		}
		);

		this.page = page;

		this.saveButton = page.getByRole('button', {
			name: 'Save'
		});
		
		this.taxCategoriesNameInput = page.getByRole ('textbox', {
			name: 'Name'
		});

		this.taxCategoriesDescriptionInput = page.getByRole ('textbox', {
			name: 'Description'
		});
	}
}
