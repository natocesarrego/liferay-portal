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

package com.liferay.source.formatter.check.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.source.formatter.util.FileUtil;

import java.io.File;
import java.io.IOException;

/**
 * @author Nícolas Moura
 */
public class VelocityMigrationUtil {

	public static String getContentMigratedFTLFile(
			String content, String velocityFileName)
		throws IOException {

		File file = _getMigratedFTLFile(velocityFileName);

		String contentFTLFile = content;

		if (file.length() != 0) {
			contentFTLFile = FileUtil.read(file);
		}

		return contentFTLFile;
	}

	public static void writeMigratedContent(
			String migratedContent, String velocityFileName)
		throws IOException {

		File file = _getMigratedFTLFile(velocityFileName);

		FileUtil.write(file, migratedContent);
	}

	private static File _getMigratedFTLFile(String fileName) {
		int posPeriod = fileName.lastIndexOf(StringPool.PERIOD);
		int posSlash = fileName.lastIndexOf(StringPool.SLASH);

		String ftlFileName = StringBundler.concat(
			fileName.substring(0, posSlash), "/migrated/",
			fileName.substring(posSlash + 1, posPeriod), ".ftl");

		return new File(ftlFileName);
	}

}