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

import {ClayCheckbox} from '@clayui/form';
import classNames from 'classnames';
import React, {useState} from 'react';

import {FieldBase} from '../FieldBase/ReactFieldBase.es';
import {setJSONArrayValue} from '../util/setters.es';

const Switcher = ({
	checked,
	disabled,
	inline,
	label,
	name,
	onBlur,
	onChange,
	onFocus,
	value,
}) => (
	<div
		className={classNames('lfr-ddm-form-field-checkbox-switch', {
			'lfr-ddm-form-field-checkbox-switch-inline': inline,
		})}
	>
		<label className="simple-toggle-switch toggle-switch">
			<input
				checked={checked}
				className="toggle-switch-check"
				disabled={disabled}
				name={name}
				onBlur={onBlur}
				onChange={onChange}
				onFocus={onFocus}
				type="checkbox"
				value={value}
			/>
			<span aria-hidden="true" className="toggle-switch-bar">
				<span className="toggle-switch-handle"></span>
			</span>
			<span className="toggle-switch-label">{label}</span>
		</label>
	</div>
);

const CheckboxMultiple = ({
	disabled,
	displayErrors,
	errorMessage,
	inline,
	isSwitcher,
	name,
	onBlur,
	onChange,
	onFocus,
	options,
	predefinedValue,
	required,
	valid,
	value: initialValue,
}) => {
	const [value, setValue] = useState(initialValue);

	const displayValues = value && value.length > 0 ? value : predefinedValue;
	const Toggle = isSwitcher ? Switcher : ClayCheckbox;

	const invalid = (displayErrors && errorMessage && !valid ) ? 'true' : 'false';
	const describedBy = (displayErrors && errorMessage && !valid ) ? 'errorMessage' : null;

	const handleChange = (event) => {
		const {target} = event;
		const newValue = value.filter(
			(currentValue) => currentValue !== target.value
		);

		if (target.checked) {
			newValue.push(target.value);
		}

		setValue(newValue);
		onChange(event, newValue);
	};

	return (
		<div className="lfr-ddm-checkbox-multiple">
			<fieldset aria-describedby={describedBy} aria-invalid={invalid} aria-label="checkbox" aria-required={required}>
			{options.map((option) => (
				<Toggle
					checked={displayValues.includes(option.value)}
					disabled={disabled}
					inline={inline}
					key={option.value}
					label={option.label}
					name={name}
					onBlur={onBlur}
					onChange={handleChange}
					onFocus={onFocus}
					value={option.value}
				/>
			))}
			</fieldset>
		</div>
	);
};

const Main = ({
	displayErrors,
	errorMessage,
	inline,
	name,
	options = [
		{
			label: 'Option 1',
			value: 'option1',
		},
		{
			label: 'Option 2',
			value: 'option2',
		},
	],
	onBlur,
	onChange,
	onFocus,
	predefinedValue,
	readOnly,
	required,
	showAsSwitcher = true,
	valid,
	value,
	...otherProps
}) => (
	<FieldBase displayErrors={displayErrors} errorMessage={errorMessage} name={name} readOnly={readOnly} required={required} valid={valid} {...otherProps}>
		<CheckboxMultiple
			displayErrors={displayErrors}
			errorMessage={errorMessage}
			disabled={readOnly}
			inline={inline}
			isSwitcher={showAsSwitcher}
			name={name}
			onBlur={onBlur}
			onChange={onChange}
			onFocus={onFocus}
			options={options}
			predefinedValue={setJSONArrayValue(predefinedValue)}
			required={required}
			valid={valid}
			value={setJSONArrayValue(value)}
		/>
	</FieldBase>
);

Main.displayName = 'CheckboxMultiple';

export default Main;
