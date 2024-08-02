/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';

export const test = mergeTests(
	apiHelpersTest,
	applicationsMenuPageTest,
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

test('Add tax rate to fixed tax engine', async ({
	apiHelpers,
	applicationsMenuPage,
	commerceAdminTaxCategoriesDetailsPage,
	commerceAdminTaxCategoriesPage,
	page
}) => {
	await applicationsMenuPage.goToTaxCategories();

	await expect(
		commerceAdminTaxCategoriesPage.newButton
	).toBeVisible();

	await commerceAdminTaxCategoriesPage.newButton.click();

	await expect(
		commerceAdminTaxCategoriesDetailsPage.externalReferenceCode
	).toBeVisible();

	await commerceAdminTaxCategoriesDetailsPage.externalReferenceCode.fill('ERC-1');

	await commerceAdminTaxCategoriesDetailsPage.taxCategoriesNameInput.fill('New Tax Rate Name');

	await commerceAdminTaxCategoriesDetailsPage.taxCategoriesDescriptionInput.fill('New Tax Rate Description');

	await commerceAdminTaxCategoriesDetailsPage.saveButton.click();

	await applicationsMenuPage.goToTaxCategories();

	await expect(
		commerceAdminTaxCategoriesPage.taxCategoriesTable
	).toBeVisible();
});
