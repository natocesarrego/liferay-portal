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

package com.liferay.dynamic.data.mapping.form.builder.internal.converter.serializer;

import com.liferay.dynamic.data.mapping.form.builder.internal.converter.model.action.AutoFillDDMFormRuleAction;
import com.liferay.dynamic.data.mapping.spi.converter.serializer.SPIDDMFormRuleSerializerContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HashMapBuilder;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Leonardo Barros
 */
@RunWith(PowerMockRunner.class)
public class AutoFillDDMFormRuleActionSerializerTest extends PowerMockito {

	@Test
	public void testSerialize() {
		when(
			_autoFillDDMFormRuleAction.getDDMDataProviderInstanceUUID()
		).thenReturn(
			"0"
		);

		when(
			_autoFillDDMFormRuleAction.getInputParametersMapper()
		).thenReturn(
			HashMapBuilder.put(
				"key1", "field1"
			).build()
		);

		when(
			_autoFillDDMFormRuleAction.getOutputParametersMapper()
		).thenReturn(
			HashMapBuilder.put(
				"key1", "field2"
			).put(
				"key2", "field3"
			).build()
		);

		AutoFillDDMFormRuleActionSerializer
			autoFillDDMFormRuleActionSerializer =
				new AutoFillDDMFormRuleActionSerializer(
					_autoFillDDMFormRuleAction);

		String result = autoFillDDMFormRuleActionSerializer.serialize(
			_spiDDMFormRuleSerializerContext);

		Assert.assertEquals(
			"call('0', 'key1=field1', 'field2=key1;field3=key2')", result);
	}

	@Test
	public void testSerializeEmptyInput() {
		when(
			_autoFillDDMFormRuleAction.getInputParametersMapper()
		).thenReturn(
			HashMapBuilder.put(
				"key1", StringPool.BLANK
			).build()
		);

		when(
			_autoFillDDMFormRuleAction.getOutputParametersMapper()
		).thenReturn(
			HashMapBuilder.put(
				"key1", "field2"
			).put(
				"key2", "field3"
			).build()
		);

		AutoFillDDMFormRuleActionSerializer
			autoFillDDMFormRuleActionSerializer =
				new AutoFillDDMFormRuleActionSerializer(
					_autoFillDDMFormRuleAction);

		String result = autoFillDDMFormRuleActionSerializer.serialize(
			_spiDDMFormRuleSerializerContext);

		Assert.assertNull(result);
	}

	@Test
	public void testSerializeEmptyOutput() {
		when(
			_autoFillDDMFormRuleAction.getInputParametersMapper()
		).thenReturn(
			HashMapBuilder.put(
				"key1", "field1"
			).build()
		);

		when(
			_autoFillDDMFormRuleAction.getOutputParametersMapper()
		).thenReturn(
			HashMapBuilder.put(
				"key1", "field2"
			).put(
				"key2", StringPool.BLANK
			).build()
		);

		AutoFillDDMFormRuleActionSerializer
			autoFillDDMFormRuleActionSerializer =
				new AutoFillDDMFormRuleActionSerializer(
					_autoFillDDMFormRuleAction);

		String result = autoFillDDMFormRuleActionSerializer.serialize(
			_spiDDMFormRuleSerializerContext);

		Assert.assertNull(result);
	}

	@Mock
	private AutoFillDDMFormRuleAction _autoFillDDMFormRuleAction;

	@Mock
	private SPIDDMFormRuleSerializerContext _spiDDMFormRuleSerializerContext;

}