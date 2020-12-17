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

import ClayDatePicker from '@clayui/date-picker';
import moment from 'moment/min/moment-with-locales';
import React, {useEffect, useMemo, useRef, useState} from 'react';
import {createAutoCorrectedDatePipe} from 'text-mask-addons';
import {createTextMaskInputElement} from 'text-mask-core';

import {FieldBase} from '../FieldBase/ReactFieldBase.es';
import {useSyncValue} from '../hooks/useSyncValue.es';

const getDateMask = (dateFormat, dateDelimiter) => {
	return dateFormat
		.split(dateDelimiter)
		.map((item) => {
			let currentFormat;

			if (item === 'YYYY') {
				currentFormat = 'yyyy';
			}
			else if (item === 'DD') {
				currentFormat = 'dd';
			}
			else {
				currentFormat = 'MM';
			}

			return currentFormat;
		})
		.join(dateDelimiter);
};

const getDelimiter = (dateFormat) => {
	let dateDelimiter = '/';

	if (dateFormat.indexOf('.') != -1) {
		dateDelimiter = '.';
	}

	if (dateFormat.indexOf('-') != -1) {
		dateDelimiter = '-';
	}

	return dateDelimiter;
};

const getLocaleDateFormat = (format = 'L') => {
	return moment.localeData().longDateFormat(format);
};

const getMaskByDateFormat = (format) => {
	const mask = [];

	for (let i = 0; i < format.length; i++) {
		if (/[a-z]/i.test(format[i])) {
			mask.push(/\d/);
		}
		else {
			mask.push(`${format[i]}`);
		}
	}

	return mask;
};

const getDateFormat = () => {
	const dateMask = getLocaleDateFormat();
	const inputMask = getMaskByDateFormat(dateMask);
	const dateDelimiter = getDelimiter(inputMask);

	return {
		dateMask: getDateMask(dateMask, dateDelimiter),
		inputMask,
	};
};

const transformToDate = (date, locale) => {
	moment.locale(locale);

	if (typeof date === 'string' && date.indexOf('_') === -1 && date !== '') {
		return moment(date).toDate();
	}

	return date;
};

const getInitialMonth = (value) => {
	if (moment(value).isValid()) {
		return moment(value).toDate();
	}

	return moment().toDate();
};

const getValueForHidden = (value) => {
	if (moment(value).isValid()) {
		return moment(value).format('YYYY-MM-DD');
	}

	return null;
};

const DatePicker = ({
	disabled,
	locale,
	name,
	onChange,
	spritemap,
	value: initialValue,
}) => {
	const inputRef = useRef(null);
	const maskInstance = useRef(null);

	const [expanded, setExpand] = useState(false);

	const initialValueMemoized = useMemo(
		() => transformToDate(initialValue, locale),
		[initialValue, locale]
	);

	const [value, setValue] = useSyncValue(initialValueMemoized);
	const [years, setYears] = useState(() => {
		const currentYear = new Date().getFullYear();

		return {
			end: currentYear + 5,
			start: currentYear - 5,
		};
	});

	const {dateMask, inputMask} = getDateFormat();

	useEffect(() => {
		if (inputRef.current && inputMask && dateMask) {
			maskInstance.current = createTextMaskInputElement({
				guide: true,
				inputElement: inputRef.current,
				keepCharPositions: true,
				mask: inputMask,
				pipe: createAutoCorrectedDatePipe(dateMask.toLowerCase()),
				showMask: true,
			});
			maskInstance.current.update(inputRef.current.value);
		}
	}, [inputMask, dateMask, inputRef]);

	const handleNavigation = (date) => {
		const currentYear = date.getFullYear();

		setYears({
			end: currentYear + 5,
			start: currentYear - 5,
		});
	};

	return (
		<>
			<input
				aria-hidden="true"
				name={name}
				type="hidden"
				value={getValueForHidden(value)}
			/>
			<ClayDatePicker
				dateFormat={dateMask}
				disabled={disabled}
				expanded={expanded}
				initialMonth={getInitialMonth(value)}
				onExpandedChange={(expand) => {
					setExpand(expand);
				}}
				onInput={(event) => {
					maskInstance.current.update(event.target.value);
				}}
				onNavigation={handleNavigation}
				onValueChange={(value, eventType) => {
					setValue(value);

					if (eventType === 'click') {
						setExpand(false);
						inputRef.current.focus();
					}

					if (
						!value ||
						value === maskInstance.current.state.previousPlaceholder
					) {
						return onChange('');
					}

					if (moment(value).isValid()) {
						moment.locale(locale);
						onChange(moment(value).format('MM/DD/YYYY'));
					}
				}}
				ref={inputRef}
				spritemap={spritemap}
				value={value}
				years={years}
			/>
		</>
	);
};

const Main = ({
	locale,
	name,
	onChange,
	placeholder,
	predefinedValue,
	readOnly,
	spritemap,
	value,
	...otherProps
}) => (
	<FieldBase
		{...otherProps}
		name={name}
		readOnly={readOnly}
		spritemap={spritemap}
	>
		<DatePicker
			disabled={readOnly}
			locale={locale}
			name={name}
			onChange={(value) => onChange({}, value)}
			placeholder={placeholder}
			spritemap={spritemap}
			value={value ? value : predefinedValue}
		/>
	</FieldBase>
);

Main.displayName = 'DatePicker';

export default Main;
