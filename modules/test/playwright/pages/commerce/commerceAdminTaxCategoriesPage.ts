/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';
import {searchTableRowByValue} from './commerceDNDTablePage';

export class CommerceAdminTaxCategoriesPage {
	readonly newButton: Locator;
	readonly page: Page;
	readonly taxCategoriesTable: Locator;

	
	constructor(page: Page) {
		this.newButton = page.getByRole('link', {
			name: 'Add Tax Category'
		});

		this.page = page;

		this.taxCategoriesTable = page.locator(
			'#_com_liferay_commerce_product_tax_category_web_internal_portlet_CPTaxCategoryPortlet_cpTaxCategoriesSearchContainer'
		);
	}
}
