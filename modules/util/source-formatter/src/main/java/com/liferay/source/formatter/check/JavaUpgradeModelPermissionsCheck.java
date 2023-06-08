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

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Cavalcanti
 */
public class JavaUpgradeModelPermissionsCheck extends BaseJavaTermCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws Exception {

		List<String> importNames = javaTerm.getImportNames();

		String javaTermContent = javaTerm.getContent();

		if (!importNames.contains(
				"com.liferay.portal.kernel.service.ServiceContext")) {

			return javaTermContent;
		}

		boolean hasSetGroupPermissions = false;

		Matcher setGroupPermissionsMatcher =
			_setGroupPermissionsPattern.matcher(javaTermContent);

		if (setGroupPermissionsMatcher.find()) {
			hasSetGroupPermissions = true;
		}

		boolean hasSetGuestPermissions = false;

		Matcher setGuestPermissionsMatcher =
			_setGuestPermissionsPattern.matcher(javaTermContent);

		if (setGuestPermissionsMatcher.find()) {
			hasSetGuestPermissions = true;
		}

		if ((hasSetGroupPermissions || hasSetGuestPermissions) &&
			javaTerm.isJavaClass()) {

			if (!fileContent.contains(
					".portal.kernel.service.permission.ModelPermissions")) {

				javaTermContent = StringBundler.concat(
					"import com.liferay.portal.kernel.service.permission.",
					"ModelPermissions;\n\n", javaTermContent);
			}

			if (!fileContent.contains(
					".portal.kernel.model.role.RoleConstants")) {

				javaTermContent = StringBundler.concat(
					"import com.liferay.portal.kernel.model.role.",
					"RoleConstants;\n\n", javaTermContent);
			}

			return javaTermContent;
		}

		if (hasSetGroupPermissions && hasSetGuestPermissions) {
			String method = _createMethod(
				setGroupPermissionsMatcher.group(2),
				setGuestPermissionsMatcher.group(2),
				SourceUtil.getIndent(setGroupPermissionsMatcher.group(0)),
				setGroupPermissionsMatcher.group(1));

			javaTermContent = StringUtil.replace(
				javaTermContent,
				new String[] {
					setGroupPermissionsMatcher.group(0),
					setGuestPermissionsMatcher.group(0)
				},
				new String[] {method, StringPool.BLANK});
		}
		else if (hasSetGroupPermissions) {
			String method = _createMethod(
				setGroupPermissionsMatcher.group(2),
				"new String[0]", SourceUtil.getIndent(setGroupPermissionsMatcher.group(0)),
				setGroupPermissionsMatcher.group(1));

			javaTermContent = StringUtil.replace(
				javaTermContent, setGroupPermissionsMatcher.group(0), method);
		}
		else if (hasSetGuestPermissions) {
			String method = _createMethod(
				"new String[0]", setGuestPermissionsMatcher.group(2),
				SourceUtil.getIndent(setGuestPermissionsMatcher.group(0)),
				setGuestPermissionsMatcher.group(1));

			javaTermContent = StringUtil.replace(
				javaTermContent, setGuestPermissionsMatcher.group(0), method);
		}

		return javaTermContent;
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS, JAVA_METHOD};
	}

	private String _createMethod(
		String groupPermissions, String guestPermissions, String indent,
		String serviceContext) {

		StringBundler startModelPermissionImplSB = new StringBundler();

		startModelPermissionImplSB.append(
			StringPool.NEW_LINE
		).append(
			indent
		).append(
			"ModelPermissions modelPermissions = "
		).append(
			serviceContext
		).append(
			".getModelPermissions();"
		).append(
			StringPool.NEW_LINE
		).append(
			StringPool.NEW_LINE
		).append(
			indent
		).append(
			"if (modelPermissions == null) {"
		).append(
			StringPool.NEW_LINE
		).append(
			indent
		).append(
			StringPool.TAB
		).append(
			"modelPermissions = ModelPermissionsFactory.create"
		).append(
			StringPool.OPEN_PARENTHESIS
		).append(
			groupPermissions
		).append(
			StringPool.COMMA_AND_SPACE
		).append(
			guestPermissions
		).append(
			StringPool.CLOSE_PARENTHESIS
		).append(
			StringPool.SEMICOLON
		).append(
			StringPool.NEW_LINE
		).append(
			indent
		).append(
			"} else {"
		);

		StringBundler midModelPermissionImplSB = new StringBundler();

		if (!groupPermissions.equals("new String[0]")) {
			midModelPermissionImplSB.append(
				StringPool.NEW_LINE
			).append(
				indent
			).append(
				StringPool.TAB
			).append(
				"modelPermissions.addRolePermissions"
			).append(
				StringPool.OPEN_PARENTHESIS
			).append(
				"RoleConstants.PLACEHOLDER_DEFAULT_GROUP_ROLE"
			).append(
				StringPool.COMMA_AND_SPACE
			).append(
				groupPermissions
			).append(
				StringPool.CLOSE_PARENTHESIS
			).append(
				StringPool.SEMICOLON
			);
		}

		if (!guestPermissions.equals("new String[0]")) {
			midModelPermissionImplSB.append(
				StringPool.NEW_LINE
			).append(
				indent
			).append(
				StringPool.TAB
			).append(
				"modelPermissions.addRolePermissions"
			).append(
				StringPool.OPEN_PARENTHESIS
			).append(
				"RoleConstants.GUEST"
			).append(
				StringPool.COMMA_AND_SPACE
			).append(
				guestPermissions
			).append(
				StringPool.CLOSE_PARENTHESIS
			).append(
				StringPool.SEMICOLON
			);
		}

		StringBundler endModelPermissionImplSB = new StringBundler();

		endModelPermissionImplSB.append(
			StringPool.NEW_LINE
		).append(
			indent
		).append(
			StringPool.CLOSE_CURLY_BRACE
		).append(
			StringPool.NEW_LINE
		).append(
			StringPool.NEW_LINE
		).append(
			indent
		).append(
			serviceContext
		).append(
			".setModelPermissions(modelPermissions);"
		);

		return StringBundler.concat(
			startModelPermissionImplSB, midModelPermissionImplSB,
			endModelPermissionImplSB);
	}

	private static final Pattern _setGroupPermissionsPattern = Pattern.compile(
		"\\t*(\\w+)\\.setGroupPermissions\\((\\s*.+\\s*)\\);");
	private static final Pattern _setGuestPermissionsPattern = Pattern.compile(
		"\\t*(\\w+)\\.setGuestPermissions\\((\\s*.+\\s*)\\);");

}