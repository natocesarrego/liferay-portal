import Component from 'metal-component';
import Soy from 'metal-soy';

import templates from './file-upload.soy';

/**
 * FileUpload Component
 */
class FileUpload extends Component {
}

// Register component
Soy.register(FileUpload, templates, 'render');

if (!window.DDMFileUpload) {
	window.DDMFileUpload = {

	};
}

window.DDMFileUpload.render = FileUpload;

export default FileUpload;