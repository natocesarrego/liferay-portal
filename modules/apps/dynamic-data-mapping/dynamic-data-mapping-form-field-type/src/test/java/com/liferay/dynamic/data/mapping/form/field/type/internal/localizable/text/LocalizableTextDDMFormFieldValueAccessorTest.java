/**
 * 
 */
package com.liferay.dynamic.data.mapping.form.field.type.internal.localizable.text;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.util.LocaleUtil;

/**
 * @author Gabriel Ibson
 *
 */
@RunWith(PowerMockRunner.class)
public class LocalizableTextDDMFormFieldValueAccessorTest extends PowerMockito {

	@Before
	public void setUp() throws Exception {
		setUpLocalizableTextDDMFormFieldValueAccessor();
	}

	@Test
	public void testEmpty() {
		DDMFormFieldValue ddmFormFieldValue =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"localizableText", new UnlocalizedValue("{}"));

		Assert.assertTrue(
			_localizableTextDDMFormFieldValueAccessor.isEmpty(
				ddmFormFieldValue, LocaleUtil.US));
	}
	
	@Test
	public void testMalformedJson() {
		String malformedJson = "{";
		
		DDMFormFieldValue ddmFormFieldValue =
				DDMFormValuesTestUtil.createDDMFormFieldValue(
					"localizableText", new UnlocalizedValue(malformedJson));
		
		Assert.assertTrue(_localizableTextDDMFormFieldValueAccessor.getValue(
				ddmFormFieldValue, LocaleUtil.US).length() == 0);
	}

	@Test
	public void testNotEmpty() {
		StringBundler sb = new StringBundler(2);

		sb.append("{\"title\":\"Welcome to Liferay Forms!\",");
		sb.append("\"type\":\"document\"}");

		DDMFormFieldValue ddmFormFieldValue =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"localizableText", new UnlocalizedValue(sb.toString()));

		Assert.assertFalse(
			_localizableTextDDMFormFieldValueAccessor.isEmpty(
				ddmFormFieldValue, LocaleUtil.US));
	}

	protected void setUpLocalizableTextDDMFormFieldValueAccessor()
		throws Exception {

		_localizableTextDDMFormFieldValueAccessor =
			new LocalizableTextDDMFormFieldValueAccessor();

		field(
			LocalizableTextDDMFormFieldValueAccessor.class, "jsonFactory"
		).set(
			_localizableTextDDMFormFieldValueAccessor, _jsonFactory
		);
	}

	private LocalizableTextDDMFormFieldValueAccessor
		_localizableTextDDMFormFieldValueAccessor;
	private final JSONFactory _jsonFactory = new JSONFactoryImpl();

}
