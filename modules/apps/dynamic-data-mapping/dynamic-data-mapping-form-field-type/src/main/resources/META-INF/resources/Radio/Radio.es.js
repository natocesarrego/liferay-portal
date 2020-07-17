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

import {ClayRadio} from '@clayui/form';
import React, {useMemo} from 'react';

import {FieldBase} from '../FieldBase/ReactFieldBase.es';
import {useSyncValue} from '../hooks/useSyncValue.es';
import {setJSONArrayValue} from '../util/setters.es';

const Radio = ({
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
	displayErrors,
	errorMessage,
	inline,
	name,
	onBlur,
	onChange,
	onFocus,
	predefinedValue,
	readOnly: disabled,
	required,
	valid,
	value: initialValue,
	...otherProps
}) => {
	const predefinedValueMemo = useMemo(() => {
		const predefinedValueJSONArray =
			setJSONArrayValue(predefinedValue) || [];

		return predefinedValueJSONArray[0];
	}, [predefinedValue]);

	const [currentValue, setCurrentValue] = useSyncValue(
		initialValue ? initialValue : predefinedValueMemo
	);

	const invalid = (displayErrors && errorMessage && !valid ) ? 'true' : 'false';
	const describedBy = (displayErrors && errorMessage && !valid ) ? 'errorMessage' : null;

	return (
		<FieldBase {...otherProps} displayErrors={displayErrors} errorMessage={errorMessage} name={name} readOnly={disabled} required={required} valid={valid}>
			<fieldset aria-describedby={describedBy} aria-invalid={invalid} aria-label="single selection" aria-required={required}>
			<div className="ddm-radio" onBlur={onBlur} onFocus={onFocus}>
				{options.map((option) => (
					<ClayRadio
						checked={currentValue === option.value}
						disabled={disabled}
						inline={inline}
						key={option.value}
						label={option.label}
						name={name}
						onChange={(event) => {
							setCurrentValue(option.value);

							onChange(event);
						}}
						value={option.value}
					/>
				))}
			</div>
			</fieldset>
		</FieldBase>
	);
};

export default Radio;
