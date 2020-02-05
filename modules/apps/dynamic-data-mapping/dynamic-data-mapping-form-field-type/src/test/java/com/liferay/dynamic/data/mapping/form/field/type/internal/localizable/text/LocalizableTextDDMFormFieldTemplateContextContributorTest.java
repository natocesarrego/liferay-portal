package com.liferay.dynamic.data.mapping.form.field.type.internal.localizable.text;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.modules.junit4.PowerMockRunner;

import com.liferay.dynamic.data.mapping.form.field.type.BaseDDMFormFieldTypeSettingsTestCase;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.language.LanguageImpl;

/**
 * @author Gabriel Ibson
 */
@RunWith(PowerMockRunner.class)
public class LocalizableTextDDMFormFieldTemplateContextContributorTest 
	extends BaseDDMFormFieldTypeSettingsTestCase {
	
	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		setUpJSONFactory();
		setUpLanguage();
	}
	
	@Test
	public void testGetAvailableLocales() {
		Map<String, Object> parameters = getParameters();

		JSONArray availableLocales = (JSONArray)parameters.get(
				"availableLocales");

		Assert.assertFalse(availableLocales.length() == 0);
	}
	
	@Test
	public void testGetNotDefinedPredefinedValue() {
		Map<String, Object> parameters = getParameters();

		Assert.assertNull(parameters.get("predefinedValue"));
	}

	@Test
	public void testGetPredefinedValue() {
		String expectedString = StringUtil.randomString();
		
		LocalizedValue predefinedValue = new LocalizedValue(LocaleUtil.US);

		predefinedValue.addString(LocaleUtil.US, expectedString);

		_ddmFormField.setProperty("predefinedValue", predefinedValue);

		Map<String, Object> parameters = getParameters();

		String actualPredefinedValue = (String)parameters.get(
			"predefinedValue");

		Assert.assertEquals(expectedString, actualPredefinedValue);
	}
	
	protected DDMForm getDDMForm() {
		DDMForm ddmForm = new DDMForm();
		ddmForm.setDefaultLocale(LocaleUtil.US);
		
		return ddmForm;
	}
	
	protected Map<String, Object> getParameters() {
		_ddmFormField.setDDMForm(getDDMForm());
		
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			new DDMFormFieldRenderingContext();

		ddmFormFieldRenderingContext.setLocale(LocaleUtil.US);

		Map<String, Object> parameters =
			_localizableTextDDMFormFieldTemplateContextContributor.
				getParameters(_ddmFormField, ddmFormFieldRenderingContext);
		
		return parameters;
	}
	
	protected void setUpJSONFactory() throws Exception {
		MemberMatcher.field(
			LocalizableTextDDMFormFieldTemplateContextContributor.class,
			"jsonFactory"
		).set(
			_localizableTextDDMFormFieldTemplateContextContributor, _jsonFactory
		);
	}
	
	protected void setUpLanguage() throws Exception {
		MemberMatcher.field(
			LocalizableTextDDMFormFieldTemplateContextContributor.class,
			"language"
		).set(
			_localizableTextDDMFormFieldTemplateContextContributor, _language
		);
	}
	
	private final DDMFormField _ddmFormField = 
			new DDMFormField("field", "localizableText");
	
	private final JSONFactory _jsonFactory = new JSONFactoryImpl();

	private final Language _language = new LanguageImpl();
	
	private final LocalizableTextDDMFormFieldTemplateContextContributor
		_localizableTextDDMFormFieldTemplateContextContributor =
			new LocalizableTextDDMFormFieldTemplateContextContributor();
	
}
