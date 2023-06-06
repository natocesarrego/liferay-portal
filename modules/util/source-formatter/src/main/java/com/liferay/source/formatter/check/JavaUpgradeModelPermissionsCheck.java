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

		boolean hasSetGroup = false;

		Matcher groupMatcher = _setGroupPermissionsPattern.matcher(
			javaTermContent);

		if (groupMatcher.find()) {
			hasSetGroup = true;
		}

		boolean hasSetGuest = false;

		Matcher guestMatcher = _setGuestPermissionsPattern.matcher(
			javaTermContent);

		if (guestMatcher.find()) {
			hasSetGuest = true;
		}

		if ((hasSetGroup || hasSetGuest) && javaTerm.isJavaClass()) {
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

		if (hasSetGroup && hasSetGuest) {
			String method = _createMethod(
				groupMatcher.group(1), guestMatcher.group(1),
				SourceUtil.getIndent(groupMatcher.group(0)));

			javaTermContent = StringUtil.replace(
				javaTermContent,
				new String[] {groupMatcher.group(0), guestMatcher.group(0)},
				new String[] {method, StringPool.BLANK});
		}
		else if (hasSetGroup) {
			String method = _createMethod(
				groupMatcher.group(1), "new String[0]",
				SourceUtil.getIndent(groupMatcher.group(0)));

			javaTermContent = StringUtil.replace(
				javaTermContent, groupMatcher.group(0), method);
		}
		else if (hasSetGuest) {
			String method = _createMethod(
				"new String[0]", guestMatcher.group(1),
				SourceUtil.getIndent(guestMatcher.group(0)));

			javaTermContent = StringUtil.replace(
				javaTermContent, guestMatcher.group(0), method);
		}

		return javaTermContent;
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS, JAVA_METHOD};
	}

	private String _createMethod(
		String groupPermissions, String guestPermissions, String indent) {

		String startModelPermission = StringBundler.concat(
			"\n", indent, "ModelPermissions modelPermissions = ",
			"serviceContext.getModelPermissions();\n\n", indent,
			"if (modelPermissions == null) {\n", indent,
			"\tmodelPermissions = ModelPermissionsFactory.create(",
			groupPermissions, ", ", guestPermissions, ");\n", indent,
			"} else {\n");

		String midModelPermission = StringPool.BLANK;

		if (!groupPermissions.equals("new String[0]")) {
			midModelPermission = StringBundler.concat(
				midModelPermission, indent,
				"\tmodelPermissions.addRolePermissions(",
				"RoleConstants.PLACEHOLDER_DEFAULT_GROUP_ROLE, ",
				"groupPermissions);\n");
		}

		if (!guestPermissions.equals("new String[0]")) {
			midModelPermission = StringBundler.concat(
				midModelPermission, indent,
				"\tmodelPermissions.addRolePermissions(",
				"RoleConstants.GUEST, guestPermissions);\n");
		}

		String endModelPermission = StringBundler.concat(
			indent, "}\n\n", indent,
			"serviceContext.setModelPermissions(modelPermissions);");

		return StringBundler.concat(
			startModelPermission, midModelPermission, endModelPermission);
	}

	private static final Pattern _setGroupPermissionsPattern = Pattern.compile(
		"\\t*.+.setGroupPermissions\\((\\s*.+\\s*)\\);");
	private static final Pattern _setGuestPermissionsPattern = Pattern.compile(
		"\\t*.+.setGuestPermissions\\((\\s*.+\\s*)\\);");

}