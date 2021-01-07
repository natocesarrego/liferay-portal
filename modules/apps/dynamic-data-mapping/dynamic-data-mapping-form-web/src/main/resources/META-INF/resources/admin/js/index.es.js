import {ReactFormAdapter} from 'dynamic-data-mapping-form-renderer';

export default function (props) {
	const {containerId} = props;

	return (
		<div id={containerId}>
			<ReactFormAdapter {...props}/>
		</div>
	);
}